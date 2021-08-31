package io.github.juanimoli.strictmode.notifier

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import io.github.juanimoli.strictmode.notifier.commons.StrictModeViolation
import java.io.*
import java.util.*

internal class ViolationStore(private val context: Context) {
    //ignore
    val all: ArrayList<StrictModeViolation>
        get() {
            try {
                val serialized = prefs.getString(KEY, null)
                if (serialized != null) {
                    return fromString(serialized) as ArrayList<StrictModeViolation>
                }
            } catch (e: Exception) {
                //ignore
            }
            return ArrayList()
        }

    @Throws(IOException::class)
    fun append(report: StrictModeViolation) {
        val reports = all
        if (reports.size > MAX_REPORTS) {
            reports.subList(0, MAX_REPORTS)
        }

        reports.add(0, report)

        prefs.edit().putString(KEY, toString(reports)).apply()
    }

    fun remove(target: StrictModeViolation?) {
        val reports = all
        var targetIndex: Int? = null

        for (i in reports.indices) {
            val report = reports[i]
            if (report.logKey == target!!.logKey && report.time == target.time) {
                targetIndex = i
                break
            }
        }

        if (targetIndex != null) {
            reports.removeAt(targetIndex.toInt())
            try {
                prefs.edit().putString(KEY, toString(reports)).apply()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    companion object {
        private const val NAME = "strictmode"
        private const val KEY = "reports"
        private const val MAX_REPORTS = 50

        /**
         * Read the object from Base64 string.
         *
         * @see {http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string}
         */
        @Throws(IOException::class, ClassNotFoundException::class)
        private fun fromString(s: String): Any {
            val data = Base64.decode(s, Base64.DEFAULT)
            val ois = ObjectInputStream(ByteArrayInputStream(data))
            val o = ois.readObject()

            ois.close()

            return o
        }

        /**
         * Write the object to a Base64 string.
         *
         * @see {http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string}
         */
        @Throws(IOException::class)
        private fun toString(o: Serializable): String {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)

            oos.writeObject(o)
            oos.close()

            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        }
    }
}
