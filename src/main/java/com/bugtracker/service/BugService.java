package com.bugtracker.service;

import java.util.List;

import com.bugtracker.dao.BugDAO;
import com.bugtracker.dao.BugDAOImpl;
import com.bugtracker.dao.ProjectDAO;
import com.bugtracker.dao.ProjectDAOImpl;
import com.bugtracker.dao.UserDAO;
import com.bugtracker.dao.UserDAOImpl;
import com.bugtracker.model.Bug;
import com.bugtracker.model.Priority;
import com.bugtracker.model.Role;
import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;
import com.bugtracker.model.User;
import com.bugtracker.util.Validation;

public class BugService {
    private final BugDAO bugDAO;
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;

    public BugService() {
        this(new BugDAOImpl(), new ProjectDAOImpl(), new UserDAOImpl());
    }

    public BugService(BugDAO bugDAO, ProjectDAO projectDAO, UserDAO userDAO) {
        this.bugDAO = bugDAO;
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
    }

    public Bug reportBug(Bug bug) {
        validateBug(bug);
        ensureReferences(bug);
        bug.setStatus(Status.OPEN);
        if (!bugDAO.addBug(bug)) throw new IllegalStateException("Bug could not be saved.");
        return bug;
    }

    public List<Bug> getAllBugs() {
        return bugDAO.getAllBugs();
    }

    public Bug findBugById(int bugId) {
        Validation.requirePositiveId(bugId, "Bug ID");
        return bugDAO.getBugById(bugId);
    }

    public Bug updateBug(Bug bug, int changedBy) {
        validateBug(bug);
        Validation.requirePositiveId(bug.getBugId(), "Bug ID");
        requireUser(changedBy, "Changed by user");
        ensureReferences(bug);
        if (!bugDAO.updateBug(bug, changedBy)) throw new IllegalStateException("Bug could not be updated.");
        return bug;
    }

    public Bug assignBug(int bugId, Integer developerId, int changedBy) {
        Bug bug = requireBug(bugId);
        requireUser(changedBy, "Changed by user");
        if (developerId != null) requireDeveloper(developerId);
        bug.setAssignedTo(developerId);
        return updateBug(bug, changedBy);
    }

    public Bug changePriority(int bugId, Priority priority, int changedBy) {
        if (priority == null) throw new IllegalArgumentException("Priority is required.");
        Bug bug = requireBug(bugId);
        bug.setPriority(priority);
        return updateBug(bug, changedBy);
    }

    public Bug changeSeverity(int bugId, Severity severity, int changedBy) {
        if (severity == null) throw new IllegalArgumentException("Severity is required.");
        Bug bug = requireBug(bugId);
        bug.setSeverity(severity);
        return updateBug(bug, changedBy);
    }

    public Bug changeStatus(int bugId, Status status, int changedBy) {
        if (status == null) throw new IllegalArgumentException("Status is required.");
        Bug bug = requireBug(bugId);
        bug.setStatus(status);
        return updateBug(bug, changedBy);
    }

    public void deleteBug(int bugId) {
        Validation.requirePositiveId(bugId, "Bug ID");
        if (!bugDAO.deleteBug(bugId)) throw new IllegalStateException("Bug not found or could not be deleted.");
    }

    public List<Bug> filterBugsByStatus(Status status) {
        if (status == null) throw new IllegalArgumentException("Status is required.");
        return bugDAO.getBugsByStatus(status);
    }

    public List<Bug> filterBugsByPriority(Priority priority) {
        if (priority == null) throw new IllegalArgumentException("Priority is required.");
        return bugDAO.getBugsByPriority(priority);
    }

    private Bug requireBug(int bugId) {
        Bug bug = findBugById(bugId);
        if (bug == null) throw new IllegalStateException("Bug " + bugId + " was not found.");
        return bug;
    }

    private void validateBug(Bug bug) {
        if (bug == null) throw new IllegalArgumentException("Bug is required.");
        bug.setTitle(Validation.requireText(bug.getTitle(), "Title"));
        bug.setDescription(Validation.requireText(bug.getDescription(), "Description"));
        Validation.requirePositiveId(bug.getProjectId(), "Project ID");
        Validation.requirePositiveId(bug.getReportedBy(), "Reporter ID");
        if (bug.getPriority() == null) throw new IllegalArgumentException("Priority is required.");
        if (bug.getSeverity() == null) throw new IllegalArgumentException("Severity is required.");
        if (bug.getStatus() == null) throw new IllegalArgumentException("Status is required.");
        if (bug.getAssignedTo() != null) Validation.requirePositiveId(bug.getAssignedTo(), "Developer ID");
    }

    private void ensureReferences(Bug bug) {
        if (projectDAO.getProjectById(bug.getProjectId()) == null) {
            throw new IllegalArgumentException("Project ID does not exist.");
        }
        requireUser(bug.getReportedBy(), "Reporter");
        if (bug.getAssignedTo() != null) requireDeveloper(bug.getAssignedTo());
    }

    private User requireUser(int userId, String label) {
        Validation.requirePositiveId(userId, label + " ID");
        User user = userDAO.getUserById(userId);
        if (user == null) throw new IllegalArgumentException(label + " ID does not exist.");
        return user;
    }

    private void requireDeveloper(int developerId) {
        User developer = requireUser(developerId, "Developer");
        if (developer.getRole() != Role.DEVELOPER) {
            throw new IllegalArgumentException("Assigned user must have the DEVELOPER role.");
        }
    }
}