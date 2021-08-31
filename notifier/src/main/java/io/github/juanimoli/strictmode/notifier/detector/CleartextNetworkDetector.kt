package io.github.juanimoli.strictmode.notifier.detector

import io.github.juanimoli.strictmode.notifier.StrictModeLog

class CleartextNetworkDetector : Detector {
    private val detectMessageList = listOf(
        "Cleartext HTTP traffic to (.*) not permitted".toRegex(),
        "CLEARTEXT communication not supported:".toRegex()
    )

    override fun detect(log: StrictModeLog): Boolean {
        return detectMessageList.any { it.find(log.message) != null }
    }
}
