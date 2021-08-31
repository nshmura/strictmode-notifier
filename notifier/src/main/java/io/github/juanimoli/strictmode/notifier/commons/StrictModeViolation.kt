package io.github.juanimoli.strictmode.notifier.commons

import android.content.Context
import android.text.format.DateUtils
import java.io.Serializable
import java.util.*

class StrictModeViolation(
    val violationType: ViolationType?,
    val message: String?,
    val logKey: String?,
    val stacktrace: ArrayList<String?>,
    val time: Long
) : Serializable {
    fun getDateText(context: Context?): String {
        return DateUtils.formatDateTime(
            context,
            time,
            DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE
        )
    }

    val stacktraceText: String
        get() {
            val builder = StringBuilder()

            for (line in stacktrace) {
                if (builder.isNotEmpty()) {
                    builder.append("\n")
                }
                builder.append(line)
            }

            return builder.toString()
        }
}