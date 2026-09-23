package com.bugtracker.model;

import java.sql.Timestamp;

/**
 * Model class representing a Comment posted on a Bug.
 */
public class BugComment {
    private int id;
    private int bugId;
    private int userId;
    private String comment;
    private Timestamp createdAt;

    // Default constructor
    public BugComment() {
    }

    // Constructor without ID and createdAt (for adding a new comment)
    public BugComment(int bugId, int userId, String comment) {
        this.bugId = bugId;
        this.userId = userId;
        this.comment = comment;
    }

    // Full constructor (for reading from database)
    public BugComment(int id, int bugId, int userId, String comment, Timestamp createdAt) {
        this.id = id;
        this.bugId = bugId;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBugId() {
        return bugId;
    }

    public void setBugId(int bugId) {
        this.bugId = bugId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BugComment{" +
                "id=" + id +
                ", bugId=" + bugId +
                ", userId=" + userId +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
