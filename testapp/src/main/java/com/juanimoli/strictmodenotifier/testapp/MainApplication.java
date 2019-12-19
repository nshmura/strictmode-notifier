package com.juanimoli.strictmodenotifier.testapp;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;

import com.juanimoli.strictmodenotifier.StrictModeNotifier;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        StrictModeNotifier
                .install(this)
                .setDebugMode(true);

        //https://code.google.com/p/android/issues/detail?id=35298
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().detectAll()
                        .permitDiskReads()
                        .permitDiskWrites()
                        .penaltyLog()
                        .build();
                StrictMode.setThreadPolicy(threadPolicy);

                StrictMode.VmPolicy vmPolicy =
                        new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build();
                StrictMode.setVmPolicy(vmPolicy);
            }
        });
    }
}