package com.bugtracker.model;

/**
 * Severity indicates the impact and urgency of a bug.
 */
public enum Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static Severity fromString(String severityStr) {
        if (severityStr == null) return MEDIUM;
        try {
            return Severity.valueOf(severityStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
