package com.bugtracker;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.bugtracker.dao.BugCommentDAO;
import com.bugtracker.dao.BugCommentDAOImpl;
import com.bugtracker.dao.BugDAO;
import com.bugtracker.dao.BugDAOImpl;
import com.bugtracker.dao.BugHistoryDAO;
import com.bugtracker.dao.BugHistoryDAOImpl;
import com.bugtracker.dao.DashboardDAO;
import com.bugtracker.dao.DashboardDAOImpl;
import com.bugtracker.dao.ProjectDAO;
import com.bugtracker.dao.ProjectDAOImpl;
import com.bugtracker.dao.UserDAO;
import com.bugtracker.dao.UserDAOImpl;
import com.bugtracker.model.Bug;
import com.bugtracker.model.BugComment;
import com.bugtracker.model.BugHistory;
import com.bugtracker.model.DashboardStats;
import com.bugtracker.model.Project;
import com.bugtracker.model.Role;
import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;
import com.bugtracker.model.User;

@EnabledIfEnvironmentVariable(named = "BUGTRACKER_MYSQL_TESTS", matches = "true")
class BugWorkflowIntegrationTest {
    private final UserDAO userDAO = new UserDAOImpl();
    private final ProjectDAO projectDAO = new ProjectDAOImpl();
    private final BugDAO bugDAO = new BugDAOImpl();
    private final BugCommentDAO commentDAO = new BugCommentDAOImpl();
    private final BugHistoryDAO historyDAO = new BugHistoryDAOImpl();
    private final DashboardDAO dashboardDAO = new DashboardDAOImpl();

    private User owner;
    private User developer;
    private User admin;
    private Project project;
    private Bug bug;
    private int ownerCommentId;
    private int developerCommentId;

    @BeforeEach
    void createFixtures() {
        String token = UUID.randomUUID().toString().replace("-", "");
        owner = createUser("day4_owner_" + token, Role.TESTER);
        developer = createUser("day4_dev_" + token, Role.DEVELOPER);
        admin = createUser("day4_admin_" + token, Role.ADMIN);
        project = new Project("Day 4 " + token, "Integration test project", owner.getId());
        assertTrue(projectDAO.addProject(project));
    }

    @AfterEach
    void removeFixtures() {
        if (bug != null && bug.getId() > 0) bugDAO.deleteBug(bug.getId());
        if (project != null && project.getId() > 0) projectDAO.deleteProject(project.getId());
        if (owner != null && owner.getId() > 0) userDAO.deleteUser(owner.getId());
        if (developer != null && developer.getId() > 0) userDAO.deleteUser(developer.getId());
        if (admin != null && admin.getId() > 0) userDAO.deleteUser(admin.getId());
    }

    @Test
    void exercisesCrudCommentsHistoryAndDashboardWorkflow() {
        User loadedOwner = userDAO.getUserById(owner.getId());
        assertNotNull(loadedOwner);
        loadedOwner.setEmail("updated_" + owner.getEmail());
        assertTrue(userDAO.updateUser(loadedOwner));
        assertEquals(loadedOwner.getEmail(), userDAO.getUserById(owner.getId()).getEmail());

        project.setName(project.getName() + " updated");
        assertTrue(projectDAO.updateProject(project));
        assertEquals(project.getName(), projectDAO.getProjectById(project.getId()).getName());
        assertNull(bugDAO.getBugById(-1));

        DashboardStats before = dashboardDAO.getStats();
        bug = new Bug("Checkout fails", "Submitting a valid order returns an error.", Severity.HIGH,
                Status.OPEN, project.getId(), owner.getId(), null);
        assertTrue(bugDAO.addBug(bug));
        assertNotNull(bugDAO.getBugById(bug.getId()));
        assertTrue(bugDAO.getAllBugs().stream().anyMatch(item -> item.getId() == bug.getId()));

        BugComment ownerComment = new BugComment(bug.getId(), owner.getId(), "I reproduced this issue.");
        BugComment developerComment = new BugComment(bug.getId(), developer.getId(), "I am investigating.");
        assertTrue(commentDAO.addComment(ownerComment));
        assertTrue(commentDAO.addComment(developerComment));
        ownerCommentId = ownerComment.getId();
        developerCommentId = developerComment.getId();
        assertEquals(2, commentDAO.getCommentsForBug(bug.getId()).size());
        assertFalse(commentDAO.deleteComment(ownerCommentId, developer.getId()));
        assertTrue(commentDAO.deleteComment(developerCommentId, admin.getId()));
        assertTrue(commentDAO.deleteComment(ownerCommentId, owner.getId()));

        bug = bugDAO.getBugById(bug.getId());
        bug.setStatus(Status.IN_PROGRESS);
        bug.setSeverity(Severity.CRITICAL);
        bug.setAssignedTo(developer.getId());
        assertTrue(bugDAO.updateBug(bug, developer.getId()));
        bug = bugDAO.getBugById(bug.getId());
        bug.setStatus(Status.RESOLVED);
        assertTrue(bugDAO.updateBug(bug, developer.getId()));
        bug = bugDAO.getBugById(bug.getId());
        bug.setStatus(Status.IN_PROGRESS);
        assertTrue(bugDAO.updateBug(bug, owner.getId()));

        List<BugHistory> history = historyDAO.getHistoryForBug(bug.getId());
        List<String> actions = history.stream().map(BugHistory::getAction).collect(Collectors.toList());
        assertTrue(actions.contains("BUG_CREATED"));
        assertTrue(actions.contains("STATUS_CHANGED"));
        assertTrue(actions.contains("DEVELOPER_ASSIGNED"));
        assertTrue(actions.contains("PRIORITY_CHANGED"));
        assertTrue(actions.contains("BUG_RESOLVED"));
        assertTrue(actions.contains("BUG_REOPENED"));

        DashboardStats after = dashboardDAO.getStats();
        assertEquals(before.getTotalBugs() + 1, after.getTotalBugs());
        assertEquals(before.getCriticalBugs() + 1, after.getCriticalBugs());
        assertEquals(before.getInProgressBugs() + 1, after.getInProgressBugs());

        int bugId = bug.getId();
        assertTrue(bugDAO.deleteBug(bugId));
        bug = null;
        assertNull(bugDAO.getBugById(bugId));
        assertTrue(commentDAO.getCommentsForBug(bugId).isEmpty());
        assertTrue(historyDAO.getHistoryForBug(bugId).isEmpty());

        int projectId = project.getId();
        assertTrue(projectDAO.deleteProject(projectId));
        project = null;
        assertNull(projectDAO.getProjectById(projectId));
        assertTrue(userDAO.deleteUser(owner.getId()));
        assertNull(userDAO.getUserById(owner.getId()));
        owner = null;
        assertTrue(userDAO.deleteUser(developer.getId()));
        developer = null;
        assertTrue(userDAO.deleteUser(admin.getId()));
        admin = null;
    }

    private User createUser(String username, Role role) {
        User user = new User(username, username + "@example.com", "test-password", role);
        assertTrue(userDAO.addUser(user));
        assertTrue(user.getId() > 0);
        return user;
    }
}