package com.nshmura.strictmodenotifier.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class LeakedBroadcastReceiver extends BroadcastReceiver {

  public LeakedBroadcastReceiver() {
  }

  @Override public void onReceive(Context context, Intent intent) {
    // Post a message and delay its execution for 10 minutes.
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
      }
    }, 1000 * 60 * 10);
  }
}