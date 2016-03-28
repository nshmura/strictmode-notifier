package com.nshmura.strictmodenotifier.internal.detector;

import com.nshmura.strictmodenotifier.internal.StrictModeLog;

public class FileUriExposureDetector implements Detector {

  @Override public boolean detect(StrictModeLog log) {
    return log.message.contains("StrictMode.onFileUriExposed");
  }
}
