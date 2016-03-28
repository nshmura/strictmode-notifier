package com.nshmura.strictmodenotifier;

import android.content.Context;
import com.nshmura.strictmodenotifier.internal.LogWatchService;
import com.nshmura.strictmodenotifier.internal.StrictModeNotifierInternals;

public class StrictModeNotifier {

  public static void install(Context context) {
    install(context, LogWatchService.class);
  }

  public static void install(Context context, Class<? extends LogWatchService> serviceClass) {
    StrictModeNotifierInternals.enableReportActivity(context);
    StrictModeNotifierInternals.startLogWatchService(context, serviceClass);
  }
}