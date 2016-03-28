package com.nshmura.strictmodenotifier.internal.detector;

import com.nshmura.strictmodenotifier.internal.StrictModeLog;

public class LeakedClosableObjectsDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("A resource was acquired at attached stack trace but never released.");
  }
}

