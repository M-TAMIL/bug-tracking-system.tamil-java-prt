package com.bugtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bugtracker.model.DashboardStats;
import com.bugtracker.util.DBConnection;

public class DashboardDAOImpl implements DashboardDAO {
    @Override
    public DashboardStats getStats() {
        String sql = "SELECT COUNT(*) AS total_bugs, "
                + "COALESCE(SUM(status = 'OPEN'), 0) AS open_bugs, "
                + "COALESCE(SUM(status = 'IN_PROGRESS'), 0) AS in_progress_bugs, "
                + "COALESCE(SUM(status = 'RESOLVED'), 0) AS resolved_bugs, "
                + "COALESCE(SUM(status = 'CLOSED'), 0) AS closed_bugs, "
                + "COALESCE(SUM(severity = 'CRITICAL' OR priority = 'CRITICAL'), 0) AS critical_bugs FROM bugs";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            if (results.next()) {
                return new DashboardStats(results.getInt("total_bugs"), results.getInt("open_bugs"),
                        results.getInt("in_progress_bugs"), results.getInt("resolved_bugs"),
                        results.getInt("closed_bugs"), results.getInt("critical_bugs"));
            }
        } catch (SQLException exception) {
            System.err.println("[DashboardDAO] Error reading dashboard: " + exception.getMessage());
        }
        return new DashboardStats(0, 0, 0, 0, 0, 0);
    }
}