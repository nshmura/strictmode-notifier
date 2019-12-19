package com.juanimoli.strictmodenotifier.detector;

import com.juanimoli.strictmodenotifier.StrictModeLog;

public class NetworkDetector implements Detector {
    @Override
    public boolean detect(StrictModeLog log) {
        return log.message.contains("StrictMode$StrictModeNetworkViolation");
    }
}
