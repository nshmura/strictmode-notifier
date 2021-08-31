package io.github.juanimoli.strictmode.notifier.commons

interface CustomAction {
    fun onViolation(violation: StrictModeViolation?)
}
