package io.github.juanimoli.strictmode.notifier

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.VmPolicy

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        StrictModeNotifier.install(this).isDebugMode = true

        // https://code.google.com/p/android/issues/detail?id=35298
        Handler(Looper.getMainLooper()).post {
            val threadPolicy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .permitDiskWrites()
                .penaltyLog()
                .build()
            StrictMode.setThreadPolicy(threadPolicy)

            val vmPolicy = VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
            StrictMode.setVmPolicy(vmPolicy)
        }
    }
}
