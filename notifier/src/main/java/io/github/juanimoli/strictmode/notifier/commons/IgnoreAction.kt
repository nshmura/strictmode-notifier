package io.github.juanimoli.strictmode.notifier.commons

interface IgnoreAction {
    fun ignore(violation: StrictModeViolation?): Boolean
}
