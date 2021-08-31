package io.github.juanimoli.strictmode.notifier

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Suppress("MemberVisibilityCanBePrivate")
internal object StrictModeNotifierInternals {
    private val fileIoExecutor = newSingleThreadExecutor("File-IO")
    private const val CHANNEL_ID = "strict_mode_notifier_channel"
    private const val CHANNEL_NAME = "Strict Mode Notifier Notification"
    private const val NOTIFICATION_ID = 1

    fun enableReportActivity(context: Context) {
        setEnabled(context, StrictModeReportActivity::class.java, true)
    }

    fun startLogWatchService(
        context: Context,
        serviceClass: Class<out LogWatchService?>?
    ) {
        val intent = Intent(context, serviceClass)

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun setEnabled(
        context: Context,
        componentClass: Class<*>?,
        enabled: Boolean
    ) {
        val appContext = context.applicationContext
        executeOnFileIoThread(Runnable { setEnabledBlocking(appContext, componentClass, enabled) })
    }

    fun setEnabledBlocking(
        appContext: Context,
        componentClass: Class<*>?,
        enabled: Boolean
    ) {
        val component = ComponentName(appContext, componentClass!!)
        val packageManager = appContext.packageManager
        val newState =
            if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        // Blocks on IPC.
        packageManager.setComponentEnabledSetting(component, newState, PackageManager.DONT_KILL_APP)
    }

    fun executeOnFileIoThread(runnable: Runnable?) {
        fileIoExecutor.execute(runnable)
    }

    fun newSingleThreadExecutor(threadName: String): Executor {
        return Executors.newSingleThreadExecutor(StrictModeNotifierSingleThreadFactory(threadName))
    }

    fun showNotification(
        context: Context,
        contentTitle: CharSequence,
        contentText: CharSequence,
        headUpEnabled: Boolean,
        pendingIntent: PendingIntent
    ) {
        if (VERSION.SDK_INT < VERSION_CODES.O) {
            showNotificationFor16(context, contentTitle, contentText, pendingIntent)
        } else {
            showNotificationFor26(context, contentTitle, contentText, headUpEnabled, pendingIntent)
        }
    }

    @Suppress("DEPRECATION")
    @TargetApi(VERSION_CODES.JELLY_BEAN)
    private fun showNotificationFor16(
        context: Context,
        contentTitle: CharSequence,
        contentText: CharSequence,
        pendingIntent: PendingIntent
    ) {
        val builder = Notification.Builder(context)
            .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        builder.setFullScreenIntent(pendingIntent, true)
        getNotificationManager(context).notify(NOTIFICATION_ID, builder.build())
    }

    @TargetApi(VERSION_CODES.O)
    private fun showNotificationFor26(
        context: Context,
        contentTitle: CharSequence,
        contentText: CharSequence,
        headUpEnabled: Boolean,
        pendingIntent: PendingIntent
    ) {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        getNotificationManager(context).createNotificationChannel(channel)

        var builder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentTitle(contentTitle)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
            .setContentText(contentText)
            .setChannelId(CHANNEL_ID)

        if (headUpEnabled) {
            builder = builder.setFullScreenIntent(pendingIntent, true)
        }

        getNotificationManager(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}
