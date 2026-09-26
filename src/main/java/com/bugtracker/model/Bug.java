package com.bugtracker.model;

import java.sql.Timestamp;

/**
 * Model class representing a Bug report in the Bug Tracking System.
 */
public class Bug {
    private int id;
    private String title;
    private String description;
    private Priority priority;
    private Severity severity;
    private Status status;
    private int projectId;
    private int reportedBy;
    private Integer assignedTo; // Nullable if not yet assigned
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Bug() {
    }

    // Constructor without ID, createdAt, updatedAt (for reporting a new bug)
    public Bug(String title, String description, Severity severity, Status status,
               int projectId, int reportedBy, Integer assignedTo) {
        this(title, description, Priority.MEDIUM, severity, status, projectId, reportedBy, assignedTo);
    }

    public Bug(String title, String description, Priority priority, Severity severity, Status status,
               int projectId, int reportedBy, Integer assignedTo) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.status = status;
        this.projectId = projectId;
        this.reportedBy = reportedBy;
        this.assignedTo = assignedTo;
    }

    // Full constructor (for reading from database)
    public Bug(int id, String title, String description, Severity severity, Status status,
               int projectId, int reportedBy, Integer assignedTo,
               Timestamp createdAt, Timestamp updatedAt) {
        this(id, title, description, Priority.MEDIUM, severity, status, projectId, reportedBy,
            assignedTo, createdAt, updatedAt);
        }

        public Bug(int id, String title, String description, Priority priority, Severity severity, Status status,
               int projectId, int reportedBy, Integer assignedTo,
               Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.status = status;
        this.projectId = projectId;
        this.reportedBy = reportedBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getBugId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBugId(int bugId) {
        this.id = bugId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(int reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getCreatedDate() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdAt = createdDate;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public Timestamp getUpdatedDate() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedAt = updatedDate;
    }

    @Override
    public String toString() {
        return "Bug{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", severity=" + severity +
                ", status=" + status +
                ", projectId=" + projectId +
                ", reportedBy=" + reportedBy +
                ", assignedTo=" + assignedTo +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
