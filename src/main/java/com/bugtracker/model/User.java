package com.bugtracker.model;

import java.sql.Timestamp;

/**
 * Model class representing a User in the system.
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private Timestamp createdAt;

    // Default constructor
    public User() {
    }

    // Constructor without ID and createdAt (for creating a new user)
    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Constructor with 5 core fields (id, name, email, password, role)
    public User(int id, String name, String email, String password, Role role) {
        this.id = id;
        this.username = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Full constructor (for reading from database)
    public User(int id, String username, String email, String password, Role role, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Alias for name (satisfies Day 2 requirement while preserving Day 1 username)
    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
