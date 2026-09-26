-- ============================================================================
-- BUG TRACKING SYSTEM - DATABASE SCHEMA
-- Day 1: Initial Database Setup and Table Definitions
-- Database Engine: MySQL 8.0+
-- ============================================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS bug_tracking_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE bug_tracking_db;

-- ----------------------------------------------------------------------------
-- Table: users
-- Purpose: Stores system users (Admins, Developers, Testers)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'DEVELOPER', 'TESTER') NOT NULL DEFAULT 'DEVELOPER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table: projects
-- Purpose: Stores software projects where bugs are tracked
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_projects_created_by FOREIGN KEY (created_by)
        REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table: bugs
-- Purpose: Stores reported bugs/issues linked to projects and users
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bugs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    project_id INT NOT NULL,
    reported_by INT NOT NULL,
    assigned_to INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bugs_project FOREIGN KEY (project_id)
        REFERENCES projects(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_bugs_reported_by FOREIGN KEY (reported_by)
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_bugs_assigned_to FOREIGN KEY (assigned_to)
        REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table: bug_comments
-- Purpose: Stores discussion comments for a specific bug
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bug_comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bug_id INT NOT NULL,
    user_id INT NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_bug FOREIGN KEY (bug_id)
        REFERENCES bugs(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Table: bug_history
-- Purpose: Audits lifecycle, assignment, and severity changes for bugs
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bug_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bug_id INT NOT NULL,
    changed_by INT NULL,
    action VARCHAR(40) NOT NULL,
    details TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_bug FOREIGN KEY (bug_id)
        REFERENCES bugs(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (changed_by)
        REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- Performance Indexes for Foreign Keys and Frequent Search Fields
-- ----------------------------------------------------------------------------
CREATE INDEX idx_bugs_status ON bugs(status);
CREATE INDEX idx_bugs_severity ON bugs(severity);
CREATE INDEX idx_bugs_project_id ON bugs(project_id);
CREATE INDEX idx_comments_bug_id ON bug_comments(bug_id);
CREATE INDEX idx_history_bug_id ON bug_history(bug_id);

-- ----------------------------------------------------------------------------
-- Sample Seed Data (For testing Day 1 setup)
-- ----------------------------------------------------------------------------
INSERT INTO users (username, email, password, role) VALUES
('admin_user', 'admin@bugtracker.com', 'admin123', 'ADMIN'),
('john_dev', 'john@bugtracker.com', 'dev123', 'DEVELOPER'),
('sarah_qa', 'sarah@bugtracker.com', 'qa123', 'TESTER')
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO projects (name, description, created_by) VALUES
('E-Commerce Portal', 'Online retail shopping website and mobile API backend', 1),
('Hospital Management', 'Electronic medical records and patient appointment tracking', 1)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO bugs (title, description, severity, status, project_id, reported_by, assigned_to) VALUES
('Checkout page 500 error on payment', 'Submitting credit card payment throws a 500 Internal Server Error.', 'CRITICAL', 'OPEN', 1, 3, 2),
('Profile picture upload timeout', 'Uploading images larger than 2MB causes the request to hang indefinitely.', 'MEDIUM', 'IN_PROGRESS', 1, 3, 2)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO bug_comments (bug_id, user_id, comment) VALUES
(1, 2, 'Investigating the payment gateway timeout logs.'),
(1, 3, 'Reproduced consistently on Chrome and Firefox.')
ON DUPLICATE KEY UPDATE id=id;
