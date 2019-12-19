package com.juanimoli.strictmodenotifier.commons;

public interface CustomAction {
    void onViolation(StrictModeViolation violation);
}