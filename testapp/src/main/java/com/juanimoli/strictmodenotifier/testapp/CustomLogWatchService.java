package com.juanimoli.strictmodenotifier.testapp;

import android.util.Log;

import com.juanimoli.strictmodenotifier.LogWatchService;
import com.juanimoli.strictmodenotifier.commons.StrictModeViolation;

public class CustomLogWatchService extends LogWatchService {

    @Override
    protected void notifyViolation(StrictModeViolation violation) {
        super.notifyViolation(violation);

        //Custom Actions.
        // ex) Send logs to Slack.
        Log.d("CustomLogWatchService", "custom action");
    }
}
