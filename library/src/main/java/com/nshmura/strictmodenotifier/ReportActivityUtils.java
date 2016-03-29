package com.nshmura.strictmodenotifier;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;

/**
 * @author nishimura_shinichi
 */
public class ReportActivityUtils {
  public static void setTitle(Activity activity, String title) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      ActionBar actionBar = activity.getActionBar();
      //noinspection ConstantConditions
      actionBar.setTitle(title);
    }
  }

  public static void setDisplayHomeAsUpEnabled(Activity activity, boolean enabled) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      ActionBar actionBar = activity.getActionBar();
      //noinspection ConstantConditions
      actionBar.setDisplayHomeAsUpEnabled(enabled);
    }
  }
}
