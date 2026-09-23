package com.bugtracker.model;

/**
 * Status indicates the lifecycle stage of a bug.
 */
public enum Status {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    public static Status fromString(String statusStr) {
        if (statusStr == null) return OPEN;
        try {
            return Status.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OPEN;
        }
    }
}
