package com.bugtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bugtracker.model.Role;
import com.bugtracker.model.User;
import com.bugtracker.util.DBConnection;
import com.bugtracker.util.Validation;

/**
 * JDBC implementation of UserDAO for MySQL database operations.
 */
public class UserDAOImpl implements UserDAO {

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_USERS_SQL =
            "SELECT id, username, email, password, role, created_at FROM users ORDER BY id ASC";
    private static final String SELECT_USER_BY_ID_SQL =
            "SELECT id, username, email, password, role, created_at FROM users WHERE id = ?";
    private static final String UPDATE_USER_SQL =
            "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";
    private static final String DELETE_USER_SQL =
            "DELETE FROM users WHERE id = ?";

    @Override
    public boolean addUser(User user) {
        if (!isValidUser(user)) return false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername() != null ? user.getUsername().trim() : user.getName().trim());
            ps.setString(2, user.getEmail().trim());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error inserting user: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error fetching users: " + e.getMessage());
        }
        return userList;
    }

    @Override
    public User getUserById(int id) {
        if (id <= 0) return null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error fetching user by ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        if (!isValidUser(user) || user.getId() <= 0) return false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER_SQL)) {

            ps.setString(1, user.getUsername() != null ? user.getUsername().trim() : user.getName().trim());
            ps.setString(2, user.getEmail().trim());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole() != null ? user.getRole().name() : Role.DEVELOPER.name());
            ps.setInt(5, user.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error updating user ID " + user.getId() + ": " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteUser(int id) {
        if (id <= 0) return false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER_SQL)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error deleting user ID " + id + ": " + e.getMessage());
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        Role role = Role.fromString(rs.getString("role"));
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new User(id, username, email, password, role, createdAt);
    }

    private boolean isValidUser(User user) {
        if (user == null || user.getRole() == null) return false;
        try {
            Validation.requireText(user.getUsername() != null ? user.getUsername() : user.getName(), "Username");
            Validation.requireEmail(user.getEmail());
            Validation.requireText(user.getPassword(), "Password");
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
