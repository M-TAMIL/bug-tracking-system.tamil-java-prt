package com.bugtracker.model;

import java.util.Locale;

/**
 * Severity indicates the impact and urgency of a bug.
 */
public enum Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static Severity fromString(String severityStr) {
        if (severityStr == null || severityStr.isBlank()) {
            throw new IllegalArgumentException("Severity must not be empty.");
        }
        return Severity.valueOf(severityStr.trim().toUpperCase(Locale.ROOT));
    }
}
