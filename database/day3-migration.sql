USE bug_tracking_db;

ALTER TABLE bugs
    ADD COLUMN priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM' AFTER description,
    MODIFY COLUMN status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'REOPENED') NOT NULL DEFAULT 'OPEN';

UPDATE bugs SET priority = severity;

CREATE INDEX idx_bugs_priority ON bugs(priority);