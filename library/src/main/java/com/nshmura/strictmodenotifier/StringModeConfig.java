package com.nshmura.strictmodenotifier;

import android.content.Context;
import android.content.SharedPreferences;

class StringModeConfig {
  private static final String NAME = "strictmode";
  private static final String KEY = "config";

  private final Context context;

  public static StringModeConfig from(Context context) {
    return new StringModeConfig(context);
  }

  StringModeConfig(Context context) {
    this.context = context;
  }

  public boolean isEnabled() {
    return getPrefs().getBoolean(KEY, true);
  }

  public void enable(boolean enabled) {
    getPrefs().edit().putBoolean(KEY, enabled).apply();
  }

  public void toggle() {
    enable(!isEnabled());
  }

  private SharedPreferences getPrefs() {
    return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
  }
}
