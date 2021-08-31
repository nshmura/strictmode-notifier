package io.github.juanimoli.strictmode.notifier

import android.content.Context
import android.content.SharedPreferences

internal class StringModeConfig(private val context: Context) {
    val isEnabled: Boolean
        get() = prefs.getBoolean(KEY, true)

    fun enable(enabled: Boolean) {
        prefs.edit().putBoolean(KEY, enabled).apply()
    }

    fun toggle() {
        enable(!isEnabled)
    }

    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    companion object {
        private const val NAME = "strictmode"
        private const val KEY = "config"

        fun from(context: Context): StringModeConfig {
            return StringModeConfig(context)
        }
    }
}
