package com.juanimoli.strictmodenotifier.commons;

public interface IgnoreAction {
    boolean ignore(StrictModeViolation violation);
}