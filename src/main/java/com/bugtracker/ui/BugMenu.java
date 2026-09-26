package com.bugtracker.ui;

import java.util.Scanner;

import com.bugtracker.model.Bug;
import com.bugtracker.model.Priority;
import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;
import com.bugtracker.service.BugService;
import com.bugtracker.util.Validation;

public class BugMenu {
    private final BugService bugService;

    public BugMenu() {
        this(new BugService());
    }

    public BugMenu(BugService bugService) {
        this.bugService = bugService;
    }

    public void run(Scanner scanner) {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                switch (readInt(scanner, "Bug option: ")) {
                    case 1 -> reportBug(scanner);
                    case 2 -> bugService.getAllBugs().forEach(System.out::println);
                    case 3 -> findBug(scanner);
                    case 4 -> updateBug(scanner);
                    case 5 -> assignBug(scanner);
                    case 6 -> changePriority(scanner);
                    case 7 -> changeSeverity(scanner);
                    case 8 -> changeStatus(scanner);
                    case 9 -> deleteBug(scanner);
                    case 10 -> filterByStatus(scanner);
                    case 11 -> filterByPriority(scanner);
                    case 0 -> running = false;
                    default -> System.out.println("Choose an option from 0 to 11.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Unable to complete operation: " + exception.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Bug Management ---");
        System.out.println("1 Report  2 View all  3 Search by ID  4 Update details");
        System.out.println("5 Assign developer  6 Change priority  7 Change severity");
        System.out.println("8 Change status  9 Delete  10 Filter by status  11 Filter by priority  0 Back");
    }

    private void reportBug(Scanner scanner) {
        String title = readText(scanner, "Title: ", "Title");
        String description = readText(scanner, "Description: ", "Description");
        Priority priority = Priority.fromString(readText(scanner, "Priority (LOW/MEDIUM/HIGH/CRITICAL): ", "Priority"));
        Severity severity = Severity.fromString(readText(scanner, "Severity (LOW/MEDIUM/HIGH/CRITICAL): ", "Severity"));
        int projectId = readPositiveId(scanner, "Project ID: ");
        int reporterId = readPositiveId(scanner, "Reporter user ID: ");
        String assignedText = readLine(scanner, "Developer ID (blank for unassigned): ").trim();
        Integer assignedTo = assignedText.isEmpty() ? null : Validation.requirePositiveId(parseInt(assignedText), "Developer ID");
        Bug bug = new Bug(title, description, priority, severity, Status.OPEN, projectId, reporterId, assignedTo);
        System.out.println("Bug reported: #" + bugService.reportBug(bug).getBugId());
    }

    private void findBug(Scanner scanner) {
        printOrNotFound(bugService.findBugById(readPositiveId(scanner, "Bug ID: ")));
    }

    private void updateBug(Scanner scanner) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        Bug bug = requireBug(bugId);
        String title = readLine(scanner, "Title (blank keeps current): ").trim();
        String description = readLine(scanner, "Description (blank keeps current): ").trim();
        if (!title.isEmpty()) bug.setTitle(title);
        if (!description.isEmpty()) bug.setDescription(description);
        bugService.updateBug(bug, readPositiveId(scanner, "Changed by user ID: "));
        System.out.println("Bug updated.");
    }

    private void assignBug(Scanner scanner) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        String developerText = readText(scanner, "Developer ID: ", "Developer ID");
        bugService.assignBug(bugId, Validation.requirePositiveId(parseInt(developerText), "Developer ID"),
                readPositiveId(scanner, "Changed by user ID: "));
        System.out.println("Developer assigned.");
    }

    private void changePriority(Scanner scanner) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        Priority priority = Priority.fromString(readText(scanner, "New priority: ", "Priority"));
        bugService.changePriority(bugId, priority, readPositiveId(scanner, "Changed by user ID: "));
        System.out.println("Priority updated.");
    }

    private void changeSeverity(Scanner scanner) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        Severity severity = Severity.fromString(readText(scanner, "New severity: ", "Severity"));
        bugService.changeSeverity(bugId, severity, readPositiveId(scanner, "Changed by user ID: "));
        System.out.println("Severity updated.");
    }

    private void changeStatus(Scanner scanner) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        Status status = Status.fromString(readText(scanner, "New status: ", "Status"));
        bugService.changeStatus(bugId, status, readPositiveId(scanner, "Changed by user ID: "));
        System.out.println("Status updated.");
    }

    private void deleteBug(Scanner scanner) {
        int bugId = readPositiveId(scanner, "Bug ID: ");
        if ("yes".equalsIgnoreCase(readLine(scanner, "Delete bug " + bugId + "? (yes/no): ").trim())) {
            bugService.deleteBug(bugId);
            System.out.println("Bug deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void filterByStatus(Scanner scanner) {
        Status status = Status.fromString(readText(scanner, "Status: ", "Status"));
        bugService.filterBugsByStatus(status).forEach(System.out::println);
    }

    private void filterByPriority(Scanner scanner) {
        Priority priority = Priority.fromString(readText(scanner, "Priority: ", "Priority"));
        bugService.filterBugsByPriority(priority).forEach(System.out::println);
    }

    private Bug requireBug(int bugId) {
        Bug bug = bugService.findBugById(bugId);
        if (bug == null) throw new IllegalStateException("Bug " + bugId + " was not found.");
        return bug;
    }

    private void printOrNotFound(Bug bug) {
        if (bug == null) System.out.println("Bug not found.");
        else System.out.println(bug);
    }

    private int readPositiveId(Scanner scanner, String prompt) {
        return Validation.requirePositiveId(readInt(scanner, prompt), "ID");
    }

    private int readInt(Scanner scanner, String prompt) {
        return parseInt(readLine(scanner, prompt).trim());
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a numeric ID.");
        }
    }

    private String readText(Scanner scanner, String prompt, String field) {
        return Validation.requireText(readLine(scanner, prompt), field);
    }

    private String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}