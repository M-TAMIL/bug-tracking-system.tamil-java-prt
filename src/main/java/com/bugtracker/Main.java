package com.bugtracker;

import com.bugtracker.model.Role;
import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;
import com.bugtracker.model.User;
import com.bugtracker.model.Project;
import com.bugtracker.model.Bug;
import com.bugtracker.model.BugComment;
import com.bugtracker.util.DBConnection;

/**
 * Entry point for Day 1 of the Bug Tracking System.
 * Demonstrates project structure, domain models, and tests JDBC database connectivity.
 */
public class Main {

    public static void main(String[] args) {
        printBanner();

        System.out.println("\n[1] Verifying Package and Domain Models...");
        verifyModels();

        System.out.println("\n[2] Testing MySQL JDBC Connection...");
        boolean connected = DBConnection.testConnection();

        System.out.println("\n[3] Day 1 Status Summary:");
        if (connected) {
            System.out.println("  >>> Status: ALL CHECKS PASSED!");
            System.out.println("  >>> Database is reachable and ready for Day 2 (DAO Layer).");
        } else {
            System.out.println("  >>> Status: CODE SETUP COMPLETE (Database connection pending).");
            System.out.println("  >>> Note: Start your MySQL server and execute 'database/schema.sql' to establish live connection.");
        }

        printFooter();
    }

    private static void printBanner() {
        System.out.println("==================================================================");
        System.out.println("                   BUG TRACKING SYSTEM                            ");
        System.out.println("           Day 1: Project Setup & Database Design                 ");
        System.out.println("==================================================================");
        System.out.println("Technologies: Java 17+, Maven, MySQL, JDBC");
        System.out.println("Architecture: Layered (model, dao, service, util, ui)");
    }

    private static void verifyModels() {
        // Instantiate sample model objects to verify POJO and Enum functionality
        User sampleUser = new User(1, "demo_admin", "admin@bugtracker.com", "secret", Role.ADMIN, null);
        Project sampleProject = new Project(1, "Bug Tracker App", "Core project tracking portal", sampleUser.getId(), null);
        Bug sampleBug = new Bug(1, "NullPointerException on Login", "Occurs when email is empty",
                Severity.HIGH, Status.OPEN, sampleProject.getId(), sampleUser.getId(), null, null, null);
        BugComment sampleComment = new BugComment(1, sampleBug.getId(), sampleUser.getId(), "Looking into this bug now.", null);

        System.out.println("  - Model User loaded       : " + sampleUser.getUsername() + " (" + sampleUser.getRole() + ")");
        System.out.println("  - Model Project loaded    : " + sampleProject.getName());
        System.out.println("  - Model Bug loaded        : " + sampleBug.getTitle() + " [Severity: " + sampleBug.getSeverity() + ", Status: " + sampleBug.getStatus() + "]");
        System.out.println("  - Model BugComment loaded : \"" + sampleComment.getComment() + "\"");
        System.out.println("  [OK] Domain models and Enums compiled and instantiated successfully.");
    }

    private static void printFooter() {
        System.out.println("\n==================================================================");
        System.out.println("Day 1 Setup Complete! Ready for Day 2 implementation (DAO & CRUD).");
        System.out.println("==================================================================");
    }
}
