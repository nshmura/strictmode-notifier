package com.nshmura.strictmodenotifier.detector;

import com.nshmura.strictmodenotifier.StrictModeLog;

public class CustomSlowCallDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("StrictMode$StrictModeCustomViolation");
  }
}
