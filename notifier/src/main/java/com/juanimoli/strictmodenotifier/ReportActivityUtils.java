package com.juanimoli.strictmodenotifier;

import android.app.ActionBar;
import android.app.Activity;

/**
 * @author nishimura_shinichi
 */
class ReportActivityUtils {
    public static void setTitle(Activity activity, String title) {
        ActionBar actionBar = activity.getActionBar();
        //noinspection ConstantConditions
        actionBar.setTitle(title);
    }

    public static void setDisplayHomeAsUpEnabled(Activity activity, boolean enabled) {
        ActionBar actionBar = activity.getActionBar();
        //noinspection ConstantConditions
        actionBar.setDisplayHomeAsUpEnabled(enabled);
    }
}
