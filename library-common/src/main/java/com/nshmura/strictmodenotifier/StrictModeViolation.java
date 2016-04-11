package com.nshmura.strictmodenotifier;

import android.content.Context;
import android.text.format.DateUtils;
import java.io.Serializable;
import java.util.ArrayList;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

public class StrictModeViolation implements Serializable {

  public final ViolationType violationType;
  public final String message;
  public final String logKey;
  public final ArrayList<String> stacktreace;
  public final long time;

  public StrictModeViolation(ViolationType violationType, String message, String logKey,
      ArrayList<String> stacktreace, long time) {
    this.violationType = violationType;
    this.message = message;
    this.logKey = logKey;
    this.stacktreace = stacktreace;
    this.time = time;
  }

  public String getDateText(Context context) {
    return DateUtils.formatDateTime(context, time, FORMAT_SHOW_TIME | FORMAT_SHOW_DATE);
  }

  public String getStacktraceText() {
    StringBuilder builder = new StringBuilder();
    for (String line : stacktreace) {
      if (builder.length() > 0) {
        builder.append("\n");
      }
      builder.append(line);
    }
    return builder.toString();
  }
}
