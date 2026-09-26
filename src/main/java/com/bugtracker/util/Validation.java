package com.bugtracker.util;

import java.util.Locale;
import java.util.regex.Pattern;

import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;

public final class Validation {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private Validation() {
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be empty.");
        }
        return value.trim();
    }

    public static String requireEmail(String email) {
        String normalized = requireText(email, "Email");
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Email is invalid.");
        }
        return normalized;
    }

    public static int requirePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive ID.");
        }
        return id;
    }

    public static Status parseStatus(String value) {
        return Status.valueOf(requireText(value, "Status").toUpperCase(Locale.ROOT));
    }

    public static Severity parseSeverity(String value) {
        return Severity.valueOf(requireText(value, "Severity").toUpperCase(Locale.ROOT));
    }
}