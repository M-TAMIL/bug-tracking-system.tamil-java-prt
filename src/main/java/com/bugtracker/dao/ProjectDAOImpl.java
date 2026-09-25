package com.bugtracker.dao;

import com.bugtracker.model.Project;
import com.bugtracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ProjectDAO for MySQL database operations.
 */
public class ProjectDAOImpl implements ProjectDAO {

    private static final String INSERT_PROJECT_SQL =
            "INSERT INTO projects (name, description, created_by) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_PROJECTS_SQL =
            "SELECT id, name, description, created_by, created_at FROM projects ORDER BY id ASC";
    private static final String SELECT_PROJECT_BY_ID_SQL =
            "SELECT id, name, description, created_by, created_at FROM projects WHERE id = ?";
    private static final String UPDATE_PROJECT_SQL =
            "UPDATE projects SET name = ?, description = ?, created_by = ? WHERE id = ?";
    private static final String DELETE_PROJECT_SQL =
            "DELETE FROM projects WHERE id = ?";

    @Override
    public boolean addProject(Project project) {
        if (project == null) {
            System.err.println("[ProjectDAO] Cannot insert null project.");
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_PROJECT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());

            if (project.getCreatedBy() != null) {
                ps.setInt(3, project.getCreatedBy());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        project.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ProjectDAO] Error inserting project: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Project> getAllProjects() {
        List<Project> projectList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_PROJECTS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                projectList.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProjectDAO] Error fetching projects: " + e.getMessage());
        }
        return projectList;
    }

    @Override
    public Project getProjectById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROJECT_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProject(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ProjectDAO] Error fetching project by ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateProject(Project project) {
        if (project == null || project.getId() <= 0) {
            System.err.println("[ProjectDAO] Invalid project object or ID for update.");
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PROJECT_SQL)) {

            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());

            if (project.getCreatedBy() != null) {
                ps.setInt(3, project.getCreatedBy());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, project.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProjectDAO] Error updating project ID " + project.getId() + ": " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteProject(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_PROJECT_SQL)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProjectDAO] Error deleting project ID " + id + ": " + e.getMessage());
        }
        return false;
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int createdByVal = rs.getInt("created_by");
        Integer createdBy = rs.wasNull() ? null : createdByVal;
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new Project(id, name, description, createdBy, createdAt);
    }
}
