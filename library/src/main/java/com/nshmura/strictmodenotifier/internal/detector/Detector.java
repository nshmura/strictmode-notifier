package com.nshmura.strictmodenotifier.internal.detector;

import com.nshmura.strictmodenotifier.internal.StrictModeLog;

public interface Detector {
  boolean detect(StrictModeLog log);
}
