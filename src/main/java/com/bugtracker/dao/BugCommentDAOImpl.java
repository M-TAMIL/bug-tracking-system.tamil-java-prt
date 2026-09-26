package com.bugtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bugtracker.model.BugComment;
import com.bugtracker.util.DBConnection;
import com.bugtracker.util.Validation;

public class BugCommentDAOImpl implements BugCommentDAO {
    @Override
    public boolean addComment(BugComment comment) {
        if (comment == null || comment.getBugId() <= 0 || comment.getUserId() <= 0) return false;
        try {
            Validation.requireText(comment.getComment(), "Comment");
        } catch (IllegalArgumentException exception) {
            return false;
        }
        String sql = "INSERT INTO bug_comments (bug_id, user_id, comment) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, comment.getBugId());
            statement.setInt(2, comment.getUserId());
            statement.setString(3, comment.getComment().trim());
            if (statement.executeUpdate() == 0) return false;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) comment.setId(keys.getInt(1));
            }
            return comment.getId() > 0;
        } catch (SQLException exception) {
            System.err.println("[BugCommentDAO] Error adding comment: " + exception.getMessage());
            return false;
        }
    }

    @Override
    public List<BugComment> getCommentsForBug(int bugId) {
        List<BugComment> comments = new ArrayList<>();
        if (bugId <= 0) return comments;
        String sql = "SELECT id, bug_id, user_id, comment, created_at FROM bug_comments WHERE bug_id = ? ORDER BY created_at, id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bugId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Timestamp createdAt = results.getTimestamp("created_at");
                    comments.add(new BugComment(results.getInt("id"), results.getInt("bug_id"),
                            results.getInt("user_id"), results.getString("comment"), createdAt));
                }
            }
        } catch (SQLException exception) {
            System.err.println("[BugCommentDAO] Error fetching comments: " + exception.getMessage());
        }
        return comments;
    }

    @Override
    public boolean deleteComment(int commentId, int requestingUserId) {
        if (commentId <= 0 || requestingUserId <= 0) return false;
        String sql = "DELETE FROM bug_comments WHERE id = ? AND (user_id = ? OR EXISTS "
                + "(SELECT 1 FROM users WHERE id = ? AND role = 'ADMIN'))";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, commentId);
            statement.setInt(2, requestingUserId);
            statement.setInt(3, requestingUserId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            System.err.println("[BugCommentDAO] Error deleting comment: " + exception.getMessage());
            return false;
        }
    }
}