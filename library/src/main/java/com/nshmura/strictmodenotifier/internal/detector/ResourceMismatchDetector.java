package com.nshmura.strictmodenotifier.internal.detector;

import com.nshmura.strictmodenotifier.internal.StrictModeLog;

public class ResourceMismatchDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("StrictMode$StrictModeResourceMismatchViolation");
  }
}
