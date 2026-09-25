package com.bugtracker.model;

import java.sql.Timestamp;

/**
 * Model class representing a Project in the Bug Tracking System.
 */
public class Project {
    private int id;
    private String name;
    private String description;
    private Integer createdBy; // User ID who created the project (nullable)
    private Timestamp createdAt;

    // Default constructor
    public Project() {
    }

    // Constructor without ID and createdAt (for creating a new project)
    public Project(String name, String description, Integer createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Constructor with 4 core fields (id, name, description, createdBy)
    public Project(int id, String name, String description, Integer createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Full constructor (for reading from database)
    public Project(int id, String name, String description, Integer createdBy, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
