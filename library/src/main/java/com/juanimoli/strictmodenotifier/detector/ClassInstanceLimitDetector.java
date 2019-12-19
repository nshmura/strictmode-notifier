package com.juanimoli.strictmodenotifier.detector;

import com.juanimoli.strictmodenotifier.StrictModeLog;

public class ClassInstanceLimitDetector implements Detector {

    @Override
    public boolean detect(StrictModeLog log) {
        return log.message.contains("StrictMode.setClassInstanceLimit");
    }
}
