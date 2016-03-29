package com.nshmura.strictmodenotifier.testapp;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;
import com.nshmura.strictmodenotifier.NotifierConfig;
import com.nshmura.strictmodenotifier.StrictModeNotifier;
import com.nshmura.strictmodenotifier.StrictModeViolation;
import com.nshmura.strictmodenotifier.ViolationType;

public class MainApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    StrictModeNotifier
        .install(this)
        .setDebugMode(true)
        .setHeadupEnabled(true)
        .setIgnoreAction(new NotifierConfig.IgnoreAction() {
          @Override public boolean ignore(StrictModeViolation violation) {
            return violation.violationType == ViolationType.LEAKED_CLOSABLE_OBJECTS
                && violation.getStacktraceText().contains("android.foo.barr");
          }
        })
        .addCustomAction(new NotifierConfig.CustomAction() {
          @Override public void onViolation(StrictModeViolation violation) {
            //TODO send to Slack
          }
        });

    //https://code.google.com/p/android/issues/detail?id=35298
    new Handler().post(new Runnable() {
      @Override public void run() {
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