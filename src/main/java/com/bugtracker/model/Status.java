package com.bugtracker.model;

import java.util.Locale;

/**
 * Status indicates the lifecycle stage of a bug.
 */
public enum Status {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    public static Status fromString(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            throw new IllegalArgumentException("Status must not be empty.");
        }
        return Status.valueOf(statusStr.trim().toUpperCase(Locale.ROOT));
    }
}
