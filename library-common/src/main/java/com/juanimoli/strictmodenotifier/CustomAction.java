package com.juanimoli.strictmodenotifier;

public interface CustomAction {
    void onViolation(StrictModeViolation violation);
}