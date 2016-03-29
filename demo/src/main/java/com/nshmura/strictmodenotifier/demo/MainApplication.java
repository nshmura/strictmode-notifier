package com.nshmura.strictmodenotifier.demo;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;
import com.nshmura.strictmodenotifier.StrictModeNotifier;

public class MainApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      StrictModeNotifier.install(this);

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