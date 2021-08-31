package io.github.juanimoli.strictmode.notifier

import android.content.Context
import io.github.juanimoli.strictmode.notifier.commons.NotifierConfig

object StrictModeNotifier {
    @JvmOverloads
    fun install(
        context: Context,
        serviceClass: Class<out LogWatchService?>? = LogWatchService::class.java
    ): NotifierConfig {
        StrictModeNotifierInternals.enableReportActivity(context)
        StrictModeNotifierInternals.startLogWatchService(context, serviceClass)

        return NotifierConfig.instance
    }
}
