package com.nshmura.strictmodenotifier.testapp;

import android.util.Log;
import com.nshmura.strictmodenotifier.LogWatchService;
import com.nshmura.strictmodenotifier.StrictModeViolation;

public class CustomLogWatchService extends LogWatchService {

  @Override
  protected void notifyViolation(StrictModeViolation violation) {
    super.notifyViolation(violation);

    //Custom Actions.
    // ex) Send logs to Slack.
    Log.d("CustomLogWatchService", "custom action");
  }
}
