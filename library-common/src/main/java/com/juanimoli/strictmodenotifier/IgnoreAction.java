package com.juanimoli.strictmodenotifier;

public interface IgnoreAction {
    boolean ignore(StrictModeViolation violation);
}