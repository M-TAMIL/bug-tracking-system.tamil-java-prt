package com.bugtracker.model;

import java.sql.Timestamp;

public class BugHistory {
    private final int id;
    private final int bugId;
    private final Integer changedBy;
    private final String action;
    private final String details;
    private final Timestamp createdAt;

    public BugHistory(int id, int bugId, Integer changedBy, String action, String details, Timestamp createdAt) {
        this.id = id;
        this.bugId = bugId;
        this.changedBy = changedBy;
        this.action = action;
        this.details = details;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getBugId() { return bugId; }
    public Integer getChangedBy() { return changedBy; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Timestamp getCreatedAt() { return createdAt; }
}