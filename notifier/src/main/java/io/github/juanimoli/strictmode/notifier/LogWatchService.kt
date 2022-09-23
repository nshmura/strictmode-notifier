package io.github.juanimoli.strictmode.notifier

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import io.github.juanimoli.strictmode.notifier.commons.NotifierConfig
import io.github.juanimoli.strictmode.notifier.commons.StrictModeViolation
import io.github.juanimoli.strictmode.notifier.commons.ViolationType
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

open class LogWatchService @JvmOverloads constructor(name: String? = TAG) : IntentService(name) {
    private val notifierConfig: NotifierConfig = NotifierConfig.instance
    private var proc: Process? = null
    private val violationStore: ViolationStore
    private var logs: MutableList<StrictModeLog> = ArrayList()
    private var timer: Timer? = null
    private val violationTypes = ViolationType.values()
    private val channelId = "strict_mode_notifier_channel_service"

    init {
        setIntentRedelivery(true)

        violationStore = ViolationStore(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                channelId,
                "StrictMode Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("StrictMode Notifier")
                .setContentText("StrictMode Notifier is running on background")
                .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .build()
            startForeground(1, notification)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        startReadLoop()
    }

    @Deprecated("Deprecated in Java")
    override fun onDestroy() {
        super.onDestroy()

        log("onDestroy")

        if (proc != null) {
            proc!!.destroy()
            proc = null
        }
    }

    /**
     * notify the StrictModeViolation.
     *
     * @param violation StrictModeViolation
     */
    protected open fun notifyViolation(violation: StrictModeViolation) {
        //Custom Actions
        val customActions = notifierConfig.customActions
        for (customAction in customActions) {
            customAction.onViolation(violation)
        }

        //Default Action
        val notificationTitle: String = if (violation.violationType != null) {
            ViolationTypeInfo.convert(violation.violationType).violationName()
        } else {
            getString(R.string.strictmode_notifier_title, packageName)
        }

        StrictModeNotifierInternals.showNotification(
            this, notificationTitle,
            getString(R.string.strictmode_notifier_more_detail),
            notifierConfig.isHeadUpEnabled,
            StrictModeReportActivity.createPendingIntent(this, violation)
        )
    }

    private fun startReadLoop() {
        while (true) {
            val startTime = System.currentTimeMillis()

            log("start readLoop")

            readLoop()

            log("end readLoop")

            if (System.currentTimeMillis() - startTime <= EXIT_SPAN) {
                log("exit readLoop")
                break
            }
        }
    }

    private fun readLoop() {
        var reader: BufferedReader? = null

        try {
            //clear log
            Runtime.getRuntime().exec("logcat -c")

            //read only StrictMode error
            proc = Runtime.getRuntime().exec(LOGCAT_COMMAND)
            reader = BufferedReader(InputStreamReader(proc!!.inputStream), 1024)

            while (true) {
                val line = reader.readLine()
                if (line != null && line.isNotEmpty()) {
                    log(line)

                    val log = parseLine(line)

                    if (log != null) {
                        storeLog(log)
                        startReportTimer()
                    }
                } else {
                    error("error readLoop")

                    break
                }
            }
        } catch (e: IOException) {
            error(e.message)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    //ignore
                }
            }
        }
    }

    private fun parseLine(line: String): StrictModeLog? {
        val split = line.split(PARSE_REGEXP.toRegex()).toTypedArray()

        if (split.size < 2) {
            return null
        }

        return if (split[1] == "null") {
            null
        } else StrictModeLog(split[0], split[1], System.currentTimeMillis())
    }

    private fun storeLog(log: StrictModeLog) {
        synchronized(this) { logs.add(log) }
    }

    private fun startReportTimer() {
        synchronized(this) {
            if (timer != null) {
                return
            }
            timer = Timer(true)
        }

        timer!!.schedule(object : TimerTask() {
            override fun run() {
                synchronized(this@LogWatchService) {
                    val count = logs.size
                    var prevIsAt = false
                    var lastReadTime: Long = 0
                    val targets: MutableList<StrictModeLog> = ArrayList()

                    for (i in 0 until count) {
                        val log = logs[i]
                        val isAt = log.isAt

                        if (!isAt && prevIsAt && targets.size > 0 && !log.isCausedBy) {
                            val report = createViolation(targets)

                            if (report != null &&
                                StringModeConfig.from(this@LogWatchService).isEnabled
                            ) {
                                storeViolation(report)
                                notifyViolation(report)
                            }

                            targets.clear()
                        }
                        prevIsAt = isAt

                        targets.add(log)

                        lastReadTime = log.time
                    }

                    if (targets.size > 0 && System.currentTimeMillis() - lastReadTime >= LOG_DELAY) {
                        val report = createViolation(targets)

                        if (report != null &&
                            StringModeConfig.from(this@LogWatchService).isEnabled
                        ) {
                            storeViolation(report)
                            notifyViolation(report)
                        }

                        targets.clear()
                    }
                    timer = null

                    if (targets.size > 0) {
                        logs = targets
                        startReportTimer()
                    } else {
                        logs.clear()
                    }
                }
            }
        }, NOTIFICATION_DELAY)
    }

    private fun createViolation(logs: List<StrictModeLog>): StrictModeViolation? {
        val stackTrace = ArrayList<String?>(logs.size)
        var title: String? = ""
        var logKey: String? = ""
        var time: Long = 0

        for (log in logs) {
            if (TextUtils.isEmpty(title)) {
                title = log.message
                logKey = log.tag
                time = log.time
            }

            stackTrace.add(log.message)
        }

        val violationType = getViolationType(logs)

        if (violationType == ViolationType.UNKNOWN && logKey!!.contains(EXCEPTION_KEY)) {
            return null
        }

        val violation = StrictModeViolation(violationType, title, logKey, stackTrace, time)

        // Ignore Action
        if (notifierConfig.ignoreAction != null) {
            if (notifierConfig.ignoreAction!!.ignore(violation)) {
                return null
            }
        }
        return violation
    }

    private fun storeViolation(violation: StrictModeViolation) {
        try {
            violationStore.append(violation)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getViolationType(logs: List<StrictModeLog>): ViolationType {
        for (log in logs) {
            for (type in violationTypes) {
                val info: ViolationTypeInfo = ViolationTypeInfo.convert(type)

                if (info.detector?.detect(log) == true) {
                    return type
                }
            }
        }
        return ViolationType.UNKNOWN
    }

    private fun log(message: String) {
        if (notifierConfig.isDebugMode) {
            Log.d(TAG, message)
        }
    }

    private fun error(message: String?) {
        if (notifierConfig.isDebugMode) {
            Log.e(TAG, message!!)
        }
    }

    companion object {
        private val THREAD_NAME = LogWatchService::class.java.simpleName
        private val TAG = THREAD_NAME
        private const val LOGCAT_COMMAND = "logcat -v time -s StrictMode:* System.err:*"
        private const val PARSE_REGEXP = "(StrictMode|System.err)(\\([ 0-9]+\\))?:"
        private val EXCEPTION_KEY: CharSequence = "System.err"
        private const val NOTIFICATION_DELAY: Long = 2000 //ms
        private const val LOG_DELAY: Long = 1000 //ms
        private const val EXIT_SPAN = (1000 * 60 //ms
                ).toLong()
    }
}
