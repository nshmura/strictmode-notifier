package com.juanimoli.strictmodenotifier.detector;

import com.juanimoli.strictmodenotifier.StrictModeLog;

public interface Detector {
    boolean detect(StrictModeLog log);
}
