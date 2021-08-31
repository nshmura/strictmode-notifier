package io.github.juanimoli.strictmode.notifier.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class ClassInstanceLimitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Post a message and delay its execution for 10 minutes.
        Handler(Looper.getMainLooper()).postDelayed({}, (1000 * 60 * 10).toLong())
        var count = intent.getIntExtra(EXTRA_COUNT, 0)

        if (count > 0) {
            start(this, --count)
        }

        finish()
    }

    companion object {
        private const val EXTRA_COUNT = "EXTRA_COUNT"
        fun start(context: Context, count: Int) {
            val starter = Intent(context, ClassInstanceLimitActivity::class.java)
            starter.putExtra(EXTRA_COUNT, count)
            context.startActivity(starter)
        }
    }
}
