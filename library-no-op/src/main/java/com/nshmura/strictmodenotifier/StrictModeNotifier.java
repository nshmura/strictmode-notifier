package com.nshmura.strictmodenotifier;

import android.content.Context;

public class StrictModeNotifier {

  public static NotifierConfig install(Context context) {
    //no-op
    return NotifierConfig.getInstance();
  }
}