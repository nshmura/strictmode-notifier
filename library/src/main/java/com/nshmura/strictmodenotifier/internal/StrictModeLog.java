package com.nshmura.strictmodenotifier.internal;

class StrictModeLog {
  final String header;
  final String message;
  final long time;

  public StrictModeLog(String header, String message, long time) {
    this.header = header;
    this.message = message;
    this.time = time;
  }
}
