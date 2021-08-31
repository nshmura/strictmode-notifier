package io.github.juanimoli.strictmode.notifier.detector

import io.github.juanimoli.strictmode.notifier.StrictModeLog

class LeakedClosableObjectsDetector : Detector {
    override fun detect(log: StrictModeLog): Boolean {
        return log.message.contains("A resource was acquired at attached stack trace but never released.")
    }
}
