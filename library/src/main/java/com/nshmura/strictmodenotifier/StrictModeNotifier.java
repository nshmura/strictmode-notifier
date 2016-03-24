package com.nshmura.strictmodenotifier;

import android.content.Context;
import android.content.Intent;
import com.nshmura.strictmodenotifier.internal.LogWatchService;
import com.nshmura.strictmodenotifier.internal.ReportActivity;
import com.nshmura.strictmodenotifier.internal.StrictModeNotifierInternals;

public class StrictModeNotifier {

  public static void install(Context context) {
    enableReportActivity(context);
    Intent intent = new Intent(context, LogWatchService.class);
    context.startService(intent);
  }

  private static void enableReportActivity(Context context) {
    StrictModeNotifierInternals.setEnabled(context, ReportActivity.class, true);
  }
}