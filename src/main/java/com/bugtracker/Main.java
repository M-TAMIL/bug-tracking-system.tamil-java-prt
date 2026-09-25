package com.bugtracker;

import com.bugtracker.dao.ProjectDAO;
import com.bugtracker.dao.ProjectDAOImpl;
import com.bugtracker.dao.UserDAO;
import com.bugtracker.dao.UserDAOImpl;
import com.bugtracker.model.Project;
import com.bugtracker.model.Role;
import com.bugtracker.model.User;
import com.bugtracker.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Entry point for Bug Tracking System.
 * Day 2: User Management and Project Management CRUD Verification.
 */
public class Main {

    public static void main(String[] args) {
        printBanner();

        // 1. Verify JDBC Connection
        System.out.println("\n[STEP 1] Testing Database Connection...");
        boolean isConnected = false;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                isConnected = true;
                System.out.println("  [OK] Successfully connected to MySQL database: " + conn.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("  [NOTICE] Live database connection unavailable: " + e.getMessage());
            System.err.println("  Ensure MySQL is running and credentials in 'src/main/resources/db.properties' are correct.");
        }

        if (isConnected) {
            // Run live CRUD operations
            testUserManagement();
            testProjectManagement();
        } else {
            // Run offline demonstration so students see expected output and model integrity
            runOfflineDemo();
        }

        printFooter();
    }

    private static void printBanner() {
        System.out.println("==================================================================");
        System.out.println("                   BUG TRACKING SYSTEM                            ");
        System.out.println("           Day 2: User & Project Management (CRUD)                ");
        System.out.println("==================================================================");
        System.out.println("Technologies: Java 17+, Maven, MySQL, JDBC");
        System.out.println("Architecture: Layered (model, dao, service, util, ui)");
    }

    private static void testUserManagement() {
        System.out.println("\n==================================================================");
        System.out.println("                 [USER MANAGEMENT - CRUD DEMO]                    ");
        System.out.println("==================================================================");

        UserDAO userDAO = new UserDAOImpl();

        // 1. ADD USER
        System.out.println("\n--- 1. Add User ---");
        String uniqueName = "test_dev_" + (System.currentTimeMillis() % 10000);
        User newUser = new User(uniqueName, uniqueName + "@example.com", "Pass@123", Role.DEVELOPER);
        boolean added = userDAO.addUser(newUser);
        System.out.println("User added: " + (added ? "SUCCESS (Generated ID: " + newUser.getId() + ")" : "FAILED"));

        // 2. VIEW ALL USERS
        System.out.println("\n--- 2. View All Users ---");
        List<User> users = userDAO.getAllUsers();
        System.out.printf("%-5s | %-18s | %-25s | %-12s%n", "ID", "Name/Username", "Email", "Role");
        System.out.println("------------------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d | %-18s | %-25s | %-12s%n",
                    u.getId(), u.getName(), u.getEmail(), u.getRole());
        }

        // 3. FIND USER BY ID
        if (newUser.getId() > 0) {
            System.out.println("\n--- 3. Find User By ID (" + newUser.getId() + ") ---");
            User found = userDAO.getUserById(newUser.getId());
            if (found != null) {
                System.out.println("Found: " + found);
            } else {
                System.out.println("User not found.");
            }

            // 4. UPDATE USER
            System.out.println("\n--- 4. Update User ---");
            found.setRole(Role.ADMIN);
            found.setEmail("updated_" + newUser.getEmail());
            boolean updated = userDAO.updateUser(found);
            System.out.println("User updated: " + (updated ? "SUCCESS" : "FAILED"));
            User recheck = userDAO.getUserById(found.getId());
            System.out.println("Verified updated role: " + (recheck != null ? recheck.getRole() : "N/A"));

            // 5. DELETE USER
            System.out.println("\n--- 5. Delete User (" + newUser.getId() + ") ---");
            boolean deleted = userDAO.deleteUser(newUser.getId());
            System.out.println("User deleted: " + (deleted ? "SUCCESS" : "FAILED"));
            User deletedCheck = userDAO.getUserById(newUser.getId());
            System.out.println("Post-delete check (should be null): " + deletedCheck);
        }
    }

    private static void testProjectManagement() {
        System.out.println("\n==================================================================");
        System.out.println("                [PROJECT MANAGEMENT - CRUD DEMO]                  ");
        System.out.println("==================================================================");

        ProjectDAO projectDAO = new ProjectDAOImpl();
        UserDAO userDAO = new UserDAOImpl();

        // Find a valid user to associate as creator
        List<User> existingUsers = userDAO.getAllUsers();
        Integer creatorId = existingUsers.isEmpty() ? null : existingUsers.get(0).getId();

        // 1. ADD PROJECT
        System.out.println("\n--- 1. Add Project ---");
        Project newProject = new Project("AI Chatbot Engine", "Next-gen conversational AI service", creatorId);
        boolean added = projectDAO.addProject(newProject);
        System.out.println("Project added: " + (added ? "SUCCESS (Generated ID: " + newProject.getId() + ")" : "FAILED"));

        // 2. VIEW ALL PROJECTS
        System.out.println("\n--- 2. View All Projects ---");
        List<Project> projects = projectDAO.getAllProjects();
        System.out.printf("%-5s | %-24s | %-30s | %-10s%n", "ID", "Name", "Description", "Created By");
        System.out.println("------------------------------------------------------------------------------");
        for (Project p : projects) {
            String desc = p.getDescription() != null && p.getDescription().length() > 28
                    ? p.getDescription().substring(0, 25) + "..."
                    : p.getDescription();
            System.out.printf("%-5d | %-24s | %-30s | %-10s%n",
                    p.getId(), p.getName(), desc, p.getCreatedBy());
        }

        // 3. FIND PROJECT BY ID
        if (newProject.getId() > 0) {
            System.out.println("\n--- 3. Find Project By ID (" + newProject.getId() + ") ---");
            Project found = projectDAO.getProjectById(newProject.getId());
            if (found != null) {
                System.out.println("Found: " + found);
            } else {
                System.out.println("Project not found.");
            }

            // 4. UPDATE PROJECT
            System.out.println("\n--- 4. Update Project ---");
            found.setName("AI Chatbot Engine v2");
            found.setDescription("Updated high-throughput conversational AI microservice");
            boolean updated = projectDAO.updateProject(found);
            System.out.println("Project updated: " + (updated ? "SUCCESS" : "FAILED"));
            Project recheck = projectDAO.getProjectById(found.getId());
            System.out.println("Verified updated name: " + (recheck != null ? recheck.getName() : "N/A"));

            // 5. DELETE PROJECT
            System.out.println("\n--- 5. Delete Project (" + newProject.getId() + ") ---");
            boolean deleted = projectDAO.deleteProject(newProject.getId());
            System.out.println("Project deleted: " + (deleted ? "SUCCESS" : "FAILED"));
            Project deletedCheck = projectDAO.getProjectById(newProject.getId());
            System.out.println("Post-delete check (should be null): " + deletedCheck);
        }
    }

    private static void runOfflineDemo() {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println("Running In-Memory Validation (Day 2 Models & Architecture):");
        System.out.println("------------------------------------------------------------------");

        // Validate 5-field User constructor and name alias
        User demoUser = new User(10, "Developer Dave", "dave@bugtracker.com", "pass123", Role.DEVELOPER);
        System.out.println("[OK] User Model created successfully:");
        System.out.println("     - ID: " + demoUser.getId());
        System.out.println("     - Name (getName): " + demoUser.getName());
        System.out.println("     - Username (getUsername): " + demoUser.getUsername());
        System.out.println("     - Email: " + demoUser.getEmail());
        System.out.println("     - Role: " + demoUser.getRole());

        // Validate 4-field Project constructor
        Project demoProject = new Project(5, "Payment Gateway Integration", "Stripe & PayPal connector", demoUser.getId());
        System.out.println("\n[OK] Project Model created successfully:");
        System.out.println("     - ID: " + demoProject.getId());
        System.out.println("     - Name: " + demoProject.getName());
        System.out.println("     - Description: " + demoProject.getDescription());
        System.out.println("     - Created By (User ID): " + demoProject.getCreatedBy());

        System.out.println("\n[INFO] To execute live database CRUD tests against MySQL:");
        System.out.println("  1. Ensure MySQL is running on port 3306.");
        System.out.println("  2. Run 'database/schema.sql' in MySQL.");
        System.out.println("  3. Set your password in 'src/main/resources/db.properties'.");
        System.out.println("  4. Re-run: mvn compile exec:java");
    }

    private static void printFooter() {
        System.out.println("\n==================================================================");
        System.out.println("Day 2 Implementation Complete! (Ready for Day 3: Bug Management)  ");
        System.out.println("==================================================================");
    }
}
