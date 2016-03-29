package com.nshmura.strictmodenotifier.detector;

import com.nshmura.strictmodenotifier.StrictModeLog;

public class CleartextNetworkDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("CLEARTEXT communication not supported:");
  }
}
