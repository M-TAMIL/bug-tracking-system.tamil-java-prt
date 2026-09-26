package com.bugtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bugtracker.model.BugHistory;
import com.bugtracker.util.DBConnection;

public class BugHistoryDAOImpl implements BugHistoryDAO {
    @Override
    public List<BugHistory> getHistoryForBug(int bugId) {
        List<BugHistory> history = new ArrayList<>();
        if (bugId <= 0) return history;
        String sql = "SELECT id, bug_id, changed_by, action, details, created_at FROM bug_history "
                + "WHERE bug_id = ? ORDER BY created_at, id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bugId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    int changedByValue = results.getInt("changed_by");
                    Integer changedBy = results.wasNull() ? null : changedByValue;
                    history.add(new BugHistory(results.getInt("id"), results.getInt("bug_id"), changedBy,
                            results.getString("action"), results.getString("details"),
                            results.getTimestamp("created_at")));
                }
            }
        } catch (SQLException exception) {
            System.err.println("[BugHistoryDAO] Error fetching bug history: " + exception.getMessage());
        }
        return history;
    }
}