package io.github.juanimoli.strictmode.notifier.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import io.github.juanimoli.strictmode.notifier.LogWatchService
import io.github.juanimoli.strictmode.notifier.testapp.R
import io.github.juanimoli.strictmode.notifier.ui.adapter.Adapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = Adapter()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        if (item.itemId == R.id.menu_stop_service) {
            stopService(Intent(this, LogWatchService::class.java))
            return true
        }

        if (item.itemId == R.id.menu_start_service) {
            val intent = Intent(this, LogWatchService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}