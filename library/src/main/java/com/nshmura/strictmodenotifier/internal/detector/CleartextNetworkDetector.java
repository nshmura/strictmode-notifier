package com.nshmura.strictmodenotifier.internal.detector;

import com.nshmura.strictmodenotifier.internal.StrictModeLog;

public class CleartextNetworkDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("CLEARTEXT communication not supported:");
  }
}
