package com.nshmura.strictmodenotifier.demo;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;
import com.nshmura.strictmodenotifier.CustomAction;
import com.nshmura.strictmodenotifier.IgnoreAction;
import com.nshmura.strictmodenotifier.StrictModeNotifier;
import com.nshmura.strictmodenotifier.StrictModeViolation;
import com.nshmura.strictmodenotifier.ViolationType;

public class MainApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      StrictModeNotifier
          .install(this)
          .setDebugMode(true)
          .setHeadupEnabled(true)
          .setIgnoreAction(new IgnoreAction() {
            @Override public boolean ignore(StrictModeViolation violation) {
              return violation.violationType == ViolationType.LEAKED_CLOSABLE_OBJECTS
                  && violation.getStacktraceText().contains("android.foo.barr");
            }
          })
          .addCustomAction(new CustomAction() {
            @Override public void onViolation(StrictModeViolation violation) {
              //ex) Send messages into Slack
            }
          });

      //https://code.google.com/p/android/issues/detail?id=35298
      new Handler().post(new Runnable() {
        @Override public void run() {
          StrictMode.setThreadPolicy(
              new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        }
      });
    }
  }
}