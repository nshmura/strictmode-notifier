package com.nshmura.strictmodenotifier.detector;

import com.nshmura.strictmodenotifier.StrictModeLog;

public class LeakedClosableObjectsDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("A resource was acquired at attached stack trace but never released.");
  }
}