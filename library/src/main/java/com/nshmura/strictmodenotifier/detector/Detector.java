package com.nshmura.strictmodenotifier.detector;

import com.nshmura.strictmodenotifier.StrictModeLog;

public interface Detector {
  boolean detect(StrictModeLog log);
}
