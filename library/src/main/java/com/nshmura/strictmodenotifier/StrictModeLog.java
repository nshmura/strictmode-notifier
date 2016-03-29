package com.nshmura.strictmodenotifier;

public class StrictModeLog {
  public final String tag;
  public final String message;
  public final long time;

  public StrictModeLog(String tag, String message, long time) {
    this.tag = tag;
    this.message = message;
    this.time = time;
  }

  public boolean isAt() {
    return message.matches("^\\s+at.*");
  }
}
