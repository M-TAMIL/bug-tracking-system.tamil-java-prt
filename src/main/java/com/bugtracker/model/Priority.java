package com.bugtracker.model;

import java.util.Locale;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static Priority fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Priority must not be empty.");
        }
        return Priority.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}