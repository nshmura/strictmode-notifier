package com.nshmura.strictmodenotifier.internal.detector;

import com.nshmura.strictmodenotifier.internal.StrictModeLog;

public class NetworkDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("StrictMode$StrictModeNetworkViolation");
  }
}
