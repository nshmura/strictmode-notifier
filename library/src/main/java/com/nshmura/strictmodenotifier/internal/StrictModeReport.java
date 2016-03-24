package com.nshmura.strictmodenotifier.internal;

import java.io.Serializable;
import java.util.ArrayList;

class StrictModeReport implements Serializable {

  final String title;
  final String logKey;
  final ArrayList<String> stacktreace;
  final long time;

  public StrictModeReport(String title, String logKey, ArrayList<String> stacktreace, long time) {
    this.title = title;
    this.logKey = logKey;
    this.stacktreace = stacktreace;
    this.time = time;
  }
}
