package io.github.juanimoli.strictmode.notifier.detector

import io.github.juanimoli.strictmode.notifier.StrictModeLog

class ResourceMismatchDetector : Detector {
    private val detectMessageList = listOf(
        "StrictMode(.*)StrictModeResourceMismatchViolation".toRegex(),
        "StrictMode(.*)ResourceMismatchViolation".toRegex()
    )

    override fun detect(log: StrictModeLog): Boolean {
        return detectMessageList.any { it.find(log.message) != null }
    }
}
