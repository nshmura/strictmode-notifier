package io.github.juanimoli.strictmode.notifier.detector

import io.github.juanimoli.strictmode.notifier.StrictModeLog

class NetworkDetector : Detector {
    private val detectMessageList = listOf(
        "StrictMode(.*)StrictModeNetworkViolation".toRegex(),
        "StrictMode(.*)NetworkViolation".toRegex()
    )

    override fun detect(log: StrictModeLog): Boolean {
        return detectMessageList.any { it.find(log.message) != null }
    }
}
