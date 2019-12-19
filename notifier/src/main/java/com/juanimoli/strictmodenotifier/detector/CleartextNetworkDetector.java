package com.juanimoli.strictmodenotifier.detector;

import com.juanimoli.strictmodenotifier.StrictModeLog;

public class CleartextNetworkDetector implements Detector {
    @Override
    public boolean detect(StrictModeLog log) {
        return log.message.contains("CLEARTEXT communication not supported:");
    }
}
