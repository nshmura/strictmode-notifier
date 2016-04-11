package com.nshmura.strictmodenotifier;

public interface IgnoreAction {
  boolean ignore(StrictModeViolation violation);
}