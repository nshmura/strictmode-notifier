package com.nshmura.strictmodenotifier;

public interface CustomAction {
  void onViolation(StrictModeViolation violation);
}