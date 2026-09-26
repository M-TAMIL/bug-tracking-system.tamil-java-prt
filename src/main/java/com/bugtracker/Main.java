package com.bugtracker;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.bugtracker.dao.BugCommentDAO;
import com.bugtracker.dao.BugCommentDAOImpl;
import com.bugtracker.dao.BugDAO;
import com.bugtracker.dao.BugDAOImpl;
import com.bugtracker.dao.BugHistoryDAO;
import com.bugtracker.dao.BugHistoryDAOImpl;
import com.bugtracker.dao.DashboardDAO;
import com.bugtracker.dao.DashboardDAOImpl;
import com.bugtracker.model.Bug;
import com.bugtracker.model.BugComment;
import com.bugtracker.model.BugHistory;
import com.bugtracker.model.DashboardStats;
import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;
import com.bugtracker.util.DBConnection;
import com.bugtracker.util.Validation;

/**
 * Console entry point for bug tracking workflows.
 */
public class Main {

    public static void main(String[] args) {
        printBanner();
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Database connected: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Set credentials in src/main/resources/db.properties and run database/schema.sql.");
            return;
        }
        runMenu(new Scanner(System.in));
    }

    private static void printBanner() {
        System.out.println("==================================================================");
        System.out.println("                   BUG TRACKING SYSTEM                            ");
        System.out.println("        Day 4: Bugs, Comments, History & Dashboard                ");
        System.out.println("==================================================================");
        System.out.println("Technologies: Java 17+, Maven, MySQL, JDBC");
        System.out.println("Architecture: Layered (model, dao, service, util, ui)");
    }

    private static void runMenu(Scanner scanner) {
        BugDAO bugDAO = new BugDAOImpl();
        BugCommentDAO commentDAO = new BugCommentDAOImpl();
        BugHistoryDAO historyDAO = new BugHistoryDAOImpl();
        DashboardDAO dashboardDAO = new DashboardDAOImpl();
        boolean running = true;
        while (running) {
            System.out.println("\n1 Dashboard  2 List bugs  3 Report bug  4 Update bug");
            System.out.println("5 Delete bug  6 Add comment  7 View comments  8 Delete comment");
            System.out.println("9 Bug history  0 Exit");
            try {
                switch (readInt(scanner, "Select: ")) {
                    case 1 -> printDashboard(dashboardDAO.getStats());
                    case 2 -> bugDAO.getAllBugs().forEach(System.out::println);
                    case 3 -> reportBug(scanner, bugDAO);
                    case 4 -> updateBug(scanner, bugDAO);
                    case 5 -> deleteBug(scanner, bugDAO);
                    case 6 -> addComment(scanner, bugDAO, commentDAO);
                    case 7 -> viewComments(scanner, bugDAO, commentDAO);
                    case 8 -> deleteComment(scanner, commentDAO);
                    case 9 -> viewHistory(scanner, bugDAO, historyDAO);
                    case 0 -> running = false;
                    default -> System.out.println("Choose a menu option from 0 to 9.");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Invalid input: " + exception.getMessage());
            }
        }
        System.out.println("Bug Tracking System closed.");
    }

    private static void printDashboard(DashboardStats stats) {
        System.out.println("\n--- Bug Dashboard ---");
        System.out.println("Total bugs:       " + stats.getTotalBugs());
        System.out.println("Open:             " + stats.getOpenBugs());
        System.out.println("In progress:      " + stats.getInProgressBugs());
        System.out.println("Resolved:         " + stats.getResolvedBugs());
        System.out.println("Closed:           " + stats.getClosedBugs());
        System.out.println("Critical:         " + stats.getCriticalBugs());
    }

    private static void reportBug(Scanner scanner, BugDAO bugDAO) {
        String title = readRequiredText(scanner, "Title: ", "Title");
        String description = readRequiredText(scanner, "Description: ", "Description");
        Severity severity = Validation.parseSeverity(readRequiredText(scanner,
                "Severity (LOW, MEDIUM, HIGH, CRITICAL): ", "Severity"));
        int projectId = readPositiveId(scanner, "Project ID: ");
        int reporterId = readPositiveId(scanner, "Reporter user ID: ");
        String assigned = readLine(scanner, "Developer ID (blank for unassigned): ").trim();
        Integer assignedTo = assigned.isEmpty() ? null : Validation.requirePositiveId(parseInt(assigned), "Developer ID");
        Bug bug = new Bug(title, description, severity, Status.OPEN, projectId, reporterId, assignedTo);
        System.out.println(bugDAO.addBug(bug) ? "Bug reported with ID " + bug.getId() : "Bug was not saved.");
    }

    private static void updateBug(Scanner scanner, BugDAO bugDAO) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        Bug bug = bugDAO.getBugById(bugId);
        if (bug == null) {
            System.out.println("Bug not found.");
            return;
        }
        int changedBy = readPositiveId(scanner, "Your user ID: ");
        String status = readLine(scanner, "Status (blank keeps " + bug.getStatus() + "): ").trim();
        String severity = readLine(scanner, "Severity (blank keeps " + bug.getSeverity() + "): ").trim();
        String developer = readLine(scanner, "Developer ID (blank keeps current, 0 unassigns): ").trim();
        if (!status.isEmpty()) bug.setStatus(Validation.parseStatus(status));
        if (!severity.isEmpty()) bug.setSeverity(Validation.parseSeverity(severity));
        if (!developer.isEmpty()) {
            int developerId = parseInt(developer);
            bug.setAssignedTo(developerId == 0 ? null : Validation.requirePositiveId(developerId, "Developer ID"));
        }
        System.out.println(bugDAO.updateBug(bug, changedBy) ? "Bug updated." : "Bug was not updated.");
    }

    private static void deleteBug(Scanner scanner, BugDAO bugDAO) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        String confirm = readLine(scanner, "Delete bug " + bugId + " and its comments/history? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm.trim())) {
            System.out.println(bugDAO.deleteBug(bugId) ? "Bug deleted." : "Bug was not deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void addComment(Scanner scanner, BugDAO bugDAO, BugCommentDAO commentDAO) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        if (bugDAO.getBugById(bugId) == null) {
            System.out.println("Bug not found.");
            return;
        }
        int userId = readPositiveId(scanner, "Your user ID: ");
        String text = readRequiredText(scanner, "Comment: ", "Comment");
        BugComment comment = new BugComment(bugId, userId, text);
        System.out.println(commentDAO.addComment(comment) ? "Comment added with ID " + comment.getId() : "Comment was not saved.");
    }

    private static void viewComments(Scanner scanner, BugDAO bugDAO, BugCommentDAO commentDAO) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        if (bugDAO.getBugById(bugId) == null) {
            System.out.println("Bug not found.");
            return;
        }
        commentDAO.getCommentsForBug(bugId).forEach(System.out::println);
    }

    private static void deleteComment(Scanner scanner, BugCommentDAO commentDAO) {
        int commentId = readPositiveId(scanner, "Comment ID: ");
        int userId = readPositiveId(scanner, "Your user ID: ");
        System.out.println(commentDAO.deleteComment(commentId, userId)
                ? "Comment deleted." : "Not permitted, or comment not found.");
    }

    private static void viewHistory(Scanner scanner, BugDAO bugDAO, BugHistoryDAO historyDAO) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        if (bugDAO.getBugById(bugId) == null) {
            System.out.println("Bug not found.");
            return;
        }
        for (BugHistory event : historyDAO.getHistoryForBug(bugId)) {
            System.out.printf("%s | %s | user %s | %s%n", event.getCreatedAt(), event.getAction(),
                    event.getChangedBy(), event.getDetails());
        }
    }

    private static int readPositiveId(Scanner scanner, String prompt) {
        return Validation.requirePositiveId(readInt(scanner, prompt), "ID");
    }

    private static int readInt(Scanner scanner, String prompt) {
        return parseInt(readLine(scanner, prompt).trim());
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a numeric ID or menu option.");
        }
    }

    private static String readRequiredText(Scanner scanner, String prompt, String fieldName) {
        return Validation.requireText(readLine(scanner, prompt), fieldName);
    }

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
