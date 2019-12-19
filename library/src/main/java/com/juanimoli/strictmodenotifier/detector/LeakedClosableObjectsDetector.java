package com.juanimoli.strictmodenotifier.detector;

import com.juanimoli.strictmodenotifier.StrictModeLog;

public class LeakedClosableObjectsDetector implements Detector {

    @Override
    public boolean detect(StrictModeLog log) {
        return log.message.contains("A resource was acquired at attached stack trace but never released.");
    }
}