package com.juanimoli.strictmodenotifier.demo;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.BuildConfig;

import com.juanimoli.strictmodenotifier.StrictModeNotifier;
import com.juanimoli.strictmodenotifier.commons.ViolationType;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            StrictModeNotifier
                    .install(this)
                    .setDebugMode(true)
                    .setHeadupEnabled(true)
                    .setIgnoreAction(violation -> violation.violationType == ViolationType.LEAKED_CLOSABLE_OBJECTS
                            && violation.getStacktraceText().contains("android.foo.barr"))
                    .addCustomAction(violation -> {
                        //ex) Send messages into Slack
                    });

            //https://code.google.com/p/android/issues/detail?id=35298
            new Handler().post(() -> StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()));
        }
    }
}