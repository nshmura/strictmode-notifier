package io.github.juanimoli.strictmode.notifier.detector

import io.github.juanimoli.strictmode.notifier.StrictModeLog

interface Detector {
    fun detect(log: StrictModeLog): Boolean
}
