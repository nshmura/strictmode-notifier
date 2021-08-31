package io.github.juanimoli.strictmode.notifier.service

import android.util.Log
import io.github.juanimoli.strictmode.notifier.LogWatchService
import io.github.juanimoli.strictmode.notifier.commons.StrictModeViolation

class CustomLogWatchService : LogWatchService() {
    override fun notifyViolation(violation: StrictModeViolation) {
        super.notifyViolation(violation)

        //Custom Actions.
        // ex) Send logs to Slack.
        Log.d("CustomLogWatchService", "custom action")
    }
}
