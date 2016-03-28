package com.nshmura.strictmodenotifier;

import android.content.Context;
import com.nshmura.strictmodenotifier.internal.StrictModeNotifierInternals;

public class StrictModeNotifier {

  public static void install(Context context) {
    StrictModeNotifierInternals.enableReportActivity(context);
    StrictModeNotifierInternals.startLogWatchService(context);
  }
}