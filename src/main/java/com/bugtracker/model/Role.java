package com.bugtracker.model;

/**
 * Role represents the user permissions and responsibilities
 * within the Bug Tracking System.
 */
public enum Role {
    ADMIN,
    DEVELOPER,
    TESTER;

    public static Role fromString(String roleStr) {
        if (roleStr == null) return DEVELOPER;
        try {
            return Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEVELOPER;
        }
    }
}
