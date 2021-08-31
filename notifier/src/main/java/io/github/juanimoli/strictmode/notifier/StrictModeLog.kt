package io.github.juanimoli.strictmode.notifier

class StrictModeLog(val tag: String, val message: String, val time: Long) {
    val isAt: Boolean
        get() = message.matches("^\\s+at.*".toRegex())
    val isCausedBy: Boolean
        get() = message.matches("^\\s+Caused by.*".toRegex())
}
