package io.github.juanimoli.strictmode.notifier.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class LeakedClosableObjectsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val outputWriter = OutputStreamWriter(openFileOutput("FILETEST", MODE_PRIVATE))
            val writer = BufferedWriter(outputWriter)
            writer.write("Hi This is my File to test StrictMode")
            //writer.close();
        } catch (e: Exception) {
            e.printStackTrace()
        }

        finish()
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, LeakedClosableObjectsActivity::class.java)
            context.startActivity(starter)
        }
    }
}
