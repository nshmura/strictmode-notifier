package io.github.juanimoli.strictmode.notifier.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

class LeakedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Post a message and delay its execution for 10 minutes.
        Handler(Looper.getMainLooper()).postDelayed({}, (1000 * 60 * 10).toLong())
    }
}
