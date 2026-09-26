package com.bugtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.bugtracker.model.Bug;
import com.bugtracker.model.Severity;
import com.bugtracker.model.Status;
import com.bugtracker.util.DBConnection;
import com.bugtracker.util.Validation;

public class BugDAOImpl implements BugDAO {
    private static final String BUG_COLUMNS =
            "id, title, description, severity, status, project_id, reported_by, assigned_to, created_at, updated_at";

    @Override
    public boolean addBug(Bug bug) {
        if (!isValidBug(bug)) return false;
        String sql = "INSERT INTO bugs (title, description, severity, status, project_id, reported_by, assigned_to) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bindBug(statement, bug);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        connection.rollback();
                        return false;
                    }
                    bug.setId(keys.getInt(1));
                }
                addHistory(connection, bug.getId(), bug.getReportedBy(), "BUG_CREATED", "Bug created.");
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException | RuntimeException exception) {
            System.err.println("[BugDAO] Error inserting bug: " + exception.getMessage());
            return false;
        }
    }

    @Override
    public List<Bug> getAllBugs() {
        List<Bug> bugs = new ArrayList<>();
        String sql = "SELECT " + BUG_COLUMNS + " FROM bugs ORDER BY id ASC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) bugs.add(mapBug(results));
        } catch (SQLException exception) {
            System.err.println("[BugDAO] Error fetching bugs: " + exception.getMessage());
        }
        return bugs;
    }

    @Override
    public Bug getBugById(int id) {
        if (id <= 0) return null;
        String sql = "SELECT " + BUG_COLUMNS + " FROM bugs WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapBug(results) : null;
            }
        } catch (SQLException exception) {
            System.err.println("[BugDAO] Error fetching bug " + id + ": " + exception.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateBug(Bug bug, int changedBy) {
        if (!isValidBug(bug) || bug.getId() <= 0 || changedBy <= 0) return false;
        String selectSql = "SELECT " + BUG_COLUMNS + " FROM bugs WHERE id = ? FOR UPDATE";
        String updateSql = "UPDATE bugs SET title = ?, description = ?, severity = ?, status = ?, project_id = ?, "
                + "reported_by = ?, assigned_to = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Bug previous;
                try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                    select.setInt(1, bug.getId());
                    try (ResultSet results = select.executeQuery()) {
                        if (!results.next()) {
                            connection.rollback();
                            return false;
                        }
                        previous = mapBug(results);
                    }
                }
                try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                    bindBug(update, bug);
                    update.setInt(8, bug.getId());
                    update.executeUpdate();
                }
                recordChanges(connection, previous, bug, changedBy);
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException | RuntimeException exception) {
            System.err.println("[BugDAO] Error updating bug " + bug.getId() + ": " + exception.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteBug(int id) {
        if (id <= 0) return false;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM bugs WHERE id = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            System.err.println("[BugDAO] Error deleting bug " + id + ": " + exception.getMessage());
            return false;
        }
    }

    private boolean isValidBug(Bug bug) {
        if (bug == null || bug.getSeverity() == null || bug.getStatus() == null) return false;
        try {
            Validation.requireText(bug.getTitle(), "Title");
            Validation.requireText(bug.getDescription(), "Description");
            Validation.requirePositiveId(bug.getProjectId(), "Project ID");
            Validation.requirePositiveId(bug.getReportedBy(), "Reporter ID");
            if (bug.getAssignedTo() != null) Validation.requirePositiveId(bug.getAssignedTo(), "Developer ID");
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private void bindBug(PreparedStatement statement, Bug bug) throws SQLException {
        statement.setString(1, bug.getTitle().trim());
        statement.setString(2, bug.getDescription().trim());
        statement.setString(3, bug.getSeverity().name());
        statement.setString(4, bug.getStatus().name());
        statement.setInt(5, bug.getProjectId());
        statement.setInt(6, bug.getReportedBy());
        if (bug.getAssignedTo() == null) statement.setNull(7, Types.INTEGER);
        else statement.setInt(7, bug.getAssignedTo());
    }

    private void recordChanges(Connection connection, Bug oldBug, Bug newBug, int changedBy) throws SQLException {
        if (oldBug.getStatus() != newBug.getStatus()) {
            addHistory(connection, newBug.getId(), changedBy, "STATUS_CHANGED",
                    oldBug.getStatus() + " -> " + newBug.getStatus());
            if (newBug.getStatus() == Status.RESOLVED) {
                addHistory(connection, newBug.getId(), changedBy, "BUG_RESOLVED", "Bug resolved.");
            } else if ((oldBug.getStatus() == Status.RESOLVED || oldBug.getStatus() == Status.CLOSED)
                    && (newBug.getStatus() == Status.OPEN || newBug.getStatus() == Status.IN_PROGRESS)) {
                addHistory(connection, newBug.getId(), changedBy, "BUG_REOPENED",
                        "Bug reopened as " + newBug.getStatus() + ".");
            }
        }
        if (oldBug.getSeverity() != newBug.getSeverity()) {
            addHistory(connection, newBug.getId(), changedBy, "PRIORITY_CHANGED",
                    oldBug.getSeverity() + " -> " + newBug.getSeverity());
        }
        if (!java.util.Objects.equals(oldBug.getAssignedTo(), newBug.getAssignedTo())) {
            addHistory(connection, newBug.getId(), changedBy, "DEVELOPER_ASSIGNED",
                    (oldBug.getAssignedTo() == null ? "Unassigned" : oldBug.getAssignedTo()) + " -> "
                            + (newBug.getAssignedTo() == null ? "Unassigned" : newBug.getAssignedTo()));
        }
    }

    private void addHistory(Connection connection, int bugId, Integer changedBy, String action, String details)
            throws SQLException {
        String sql = "INSERT INTO bug_history (bug_id, changed_by, action, details) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bugId);
            if (changedBy == null) statement.setNull(2, Types.INTEGER);
            else statement.setInt(2, changedBy);
            statement.setString(3, action);
            statement.setString(4, details);
            statement.executeUpdate();
        }
    }

    private Bug mapBug(ResultSet results) throws SQLException {
        int assignedValue = results.getInt("assigned_to");
        Integer assignedTo = results.wasNull() ? null : assignedValue;
        return new Bug(results.getInt("id"), results.getString("title"), results.getString("description"),
                Severity.valueOf(results.getString("severity")), Status.valueOf(results.getString("status")),
                results.getInt("project_id"), results.getInt("reported_by"), assignedTo,
                results.getTimestamp("created_at"), results.getTimestamp("updated_at"));
    }
}