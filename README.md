# Bug Tracking System 🐞

A robust, enterprise-style Bug Tracking System application built using **Core Java**, **JDBC (Java Database Connectivity)**, and **MySQL**, managed with **Apache Maven**.

---

## 📌 Project Objective

The **Bug Tracking System** is designed to streamline software issue management. It allows developers and testers to report, assign, track, update, and resolve software bugs across projects, discuss bugs with comments, and review an audit trail.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| **Java (JDK 17+)** | Core programming language |
| **MySQL (8.0+)** | Relational Database Management System (RDBMS) |
| **JDBC** | Database connectivity & SQL execution |
| **Apache Maven** | Dependency management and project build tool |
| **Git / GitHub** | Version control & repository hosting |

---

## 📂 Project Architecture & Package Structure

The project follows a clean **Layered Architecture** adhering to Separation of Concerns:

```
Bug Tracking System/
├── pom.xml                               # Maven project configuration & dependencies
├── README.md                             # Project overview and documentation
├── .gitignore                            # Git ignore rules for build artifacts & IDE files
├── database/
│   └── schema.sql                        # Database schema definition & seed data
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── bugtracker/
        │           ├── Main.java         # Application entry point (Day 1 test runner)
        │           ├── model/            # Domain entities (POJOs & Enums)
        │           │   ├── User.java
        │           │   ├── Project.java
        │           │   ├── Bug.java
        │           │   ├── BugComment.java
        │           │   ├── Role.java
        │           │   ├── Severity.java
        │           │   └── Status.java
        │           ├── dao/              # Data Access Object layer (Day 2)
        │           │   └── package-info.java
        │           ├── service/          # Business logic layer (Day 3)
        │           │   └── package-info.java
        │           ├── util/             # Helpers & JDBC connection manager
        │           │   └── DBConnection.java
        │           └── ui/               # Console UI / Presentation layer (Day 4)
        │               └── package-info.java
        └── resources/
            ├── db.properties             # Database connection credentials
            └── schema.sql                # Copy of schema.sql on classpath
```

---

## 🗄️ Database Design (MySQL)

The database `bug_tracking_db` consists of 4 relational tables connected via Primary and Foreign Keys:

```
  +---------------+       +------------------+
  |     users     | 1   * |     projects     |
  +---------------+-------+------------------+
  | id (PK)       |       | id (PK)          |
  | username      |       | name             |
  | email         |       | description      |
  | password      |       | created_by (FK)  |
  | role          |       | created_at       |
  | created_at    |       +------------------+
  +---------------+                |
        | 1                        | 1
        |                          |
        | *                        | *
  +------------------------------------------+
  |                   bugs                   |
  +------------------------------------------+
  | id (PK)                                  |
  | title                                    |
  | description                              |
  | severity (LOW, MEDIUM, HIGH, CRITICAL)   |
  | status (OPEN, IN_PROGRESS, RESOLVED, ..) |
  | project_id (FK -> projects.id)           |
  | reported_by (FK -> users.id)             |
  | assigned_to (FK -> users.id)             |
  | created_at, updated_at                   |
  +------------------------------------------+
        | 1
        |
        | *
  +------------------------------------------+
  |               bug_comments               |
  +------------------------------------------+
  | id (PK)                                  |
  | bug_id (FK -> bugs.id)                   |
  | user_id (FK -> users.id)                 |
  | comment                                  |
  | created_at                               |
  +------------------------------------------+
```

### Tables Overview

1. **`users`**: Stores user credentials and access roles (`ADMIN`, `DEVELOPER`, `TESTER`).
2. **`projects`**: Stores projects managed in the system.
3. **`bugs`**: Tracks issues, severity, status, timestamps, and assigned team members.
4. **`bug_comments`**: Maintains chronological discussion threads on bugs.

---

## 🚀 Day 1 Progress Checklist

- [x] Initialized standard Maven project structure (`pom.xml`, `src/main/java`, `src/main/resources`).
- [x] Added `mysql-connector-j` dependency in `pom.xml`.
- [x] Created packages: `model`, `dao`, `service`, `util`, `ui`.
- [x] Designed normalized MySQL schema with 4 core tables, constraints, foreign keys, and indexes.
- [x] Prepared sample seed data for initial testing.
- [x] Implemented Domain POJO models: `User`, `Project`, `Bug`, `BugComment`.
- [x] Implemented type-safe Enums: `Role`, `Severity`, `Status`.
- [x] Created JDBC utility class `DBConnection.java` with externalized configuration (`db.properties`).
- [x] Created `Main.java` diagnostic runner to verify models and connection.
- [x] Added comprehensive `README.md` and `.gitignore`.

---

## 💻 How to Run Day 1

### Step 1: Set up the MySQL Database
1. Open MySQL Command Line, MySQL Workbench, or phpMyAdmin (XAMPP).
2. Execute the script found in `database/schema.sql`:
   ```bash
   mysql -u root -p < database/schema.sql
   ```
   *(Or copy-paste the contents of `database/schema.sql` into MySQL Workbench / phpMyAdmin and run).*

### Step 2: Configure Database Credentials
Open `src/main/resources/db.properties` and verify your MySQL username and password:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/bug_tracking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=your_mysql_password
```

### Step 3: Run the Day 1 Application

#### Option A: Using Maven (Recommended)
```bash
# Compile and execute Main
mvn compile exec:java
```

#### Option B: Using standard Java (Directly in VS Code / Eclipse / IntelliJ IDEA)
Open the project directory in your IDE, navigate to `src/main/java/com/bugtracker/Main.java`, and click **Run**.

---

## 📦 Git Commands to Commit and Push Day 1

Execute the following commands in your project terminal:

```bash
# 1. Initialize git repository (if not already done)
git init

# 2. Check changed files
git status

# 3. Stage all Day 1 files
git add .

# 4. Commit with Day 1 message
git commit -m "Day 1 - Project setup and database"

# 5. Link to your remote GitHub repository (replace with your repo URL)
git branch -M main
git remote add origin https://github.com/M-TAMIL/bug-tracking-system.tamil-java-prt.git

# 6. Push to GitHub
git push -u origin main
```

---

## 🚀 Day 2 Progress Checklist (User & Project Management)

- [x] Enhanced `User` model with 5-field constructor (`id`, `name`, `email`, `password`, `role`) and dual `getName()` / `getUsername()` accessors.
- [x] Created `UserDAO` interface and JDBC implementation `UserDAOImpl`:
  - `addUser(User user)`: Adds new user and returns generated ID.
  - `getAllUsers()`: Retrieves all registered users.
  - `getUserById(int id)`: Finds user by primary key ID.
  - `updateUser(User user)`: Updates user details.
  - `deleteUser(int id)`: Deletes user by ID.
- [x] Enhanced `Project` model with 4-field constructor (`id`, `name`, `description`, `createdBy`).
- [x] Created `ProjectDAO` interface and JDBC implementation `ProjectDAOImpl`:
  - `addProject(Project project)`: Adds new project with relational creator FK.
  - `getAllProjects()`: Retrieves all registered projects.
  - `getProjectById(int id)`: Finds project by primary key ID.
  - `updateProject(Project project)`: Updates project name, description, or creator.
  - `deleteProject(int id)`: Deletes project by ID.
- [x] Updated `Main.java` with automated CRUD verification suite for both UserDAO and ProjectDAO.

---

## 💻 How to Run & Test Day 2

### 1. Using Maven (Recommended)
```bash
mvn compile exec:java
```

### 2. Using IDE
Open the project in VS Code, IntelliJ IDEA, or Eclipse, navigate to `src/main/java/com/bugtracker/Main.java`, and click **Run**.

### Sample Output (Live Database):
```text
==================================================================
                   BUG TRACKING SYSTEM                            
           Day 2: User & Project Management (CRUD)                
==================================================================
Technologies: Java 17+, Maven, MySQL, JDBC
Architecture: Layered (model, dao, service, util, ui)

[STEP 1] Testing Database Connection...
  [OK] Successfully connected to MySQL database: bug_tracking_db

==================================================================
                 [USER MANAGEMENT - CRUD DEMO]                    
==================================================================

--- 1. Add User ---
User added: SUCCESS (Generated ID: 4)

--- 2. View All Users ---
ID    | Name/Username      | Email                     | Role        
------------------------------------------------------------------
1     | admin_user         | admin@bugtracker.com      | ADMIN       
2     | john_dev           | john@bugtracker.com       | DEVELOPER   
3     | sarah_qa           | sarah@bugtracker.com      | TESTER      
4     | test_dev_1234      | test_dev_1234@example.com | DEVELOPER   

--- 3. Find User By ID (4) ---
Found: User{id=4, username='test_dev_1234', email='test_dev_1234@example.com', role=DEVELOPER, ...}

--- 4. Update User ---
User updated: SUCCESS
Verified updated role: ADMIN

--- 5. Delete User (4) ---
User deleted: SUCCESS
Post-delete check (should be null): null

==================================================================
                [PROJECT MANAGEMENT - CRUD DEMO]                  
==================================================================

--- 1. Add Project ---
Project added: SUCCESS (Generated ID: 3)

--- 2. View All Projects ---
ID    | Name                     | Description                    | Created By
------------------------------------------------------------------------------
1     | E-Commerce Portal        | Online retail shopping webs... | 1         
2     | Hospital Management      | Electronic medical records ... | 1         
3     | AI Chatbot Engine        | Next-gen conversational AI ... | 1         

--- 3. Find Project By ID (3) ---
Found: Project{id=3, name='AI Chatbot Engine', description='Next-gen conversational AI service', createdBy=1, ...}

--- 4. Update Project ---
Project updated: SUCCESS
Verified updated name: AI Chatbot Engine v2

--- 5. Delete Project (3) ---
Project deleted: SUCCESS
Post-delete check (should be null): null

==================================================================
Day 2 Implementation Complete! (Ready for Day 3: Bug Management)  
==================================================================
```

---

## 📦 Git Commands to Commit and Push Day 2

```bash
# 1. Check changed files
git status

# 2. Stage all Day 2 additions and modifications
git add .

# 3. Commit with Day 2 commit message
git commit -m "Day 2 - User and project management"

# 4. Push to GitHub
git push origin main
```

---

## Day 4: Bug Workflows, Comments, Dashboard, and Testing

### Features

- Bug create, list, read, update, and delete through JDBC.
- Add and view bug comments. Authors may delete their own comments; a database-verified administrator may delete any comment.
- Bug history records creation, status changes, developer assignments, severity/priority changes, resolution, and reopening. Bug updates and their audit events commit atomically.
- Dashboard totals for all, open, in-progress, resolved, closed, and critical bugs.
- Validation for required text, email format, positive IDs, status, and severity values.
- Existing user and project CRUD DAOs remain available and now validate required fields and IDs.

### Database Setup

For a new database, execute `database/schema.sql` in MySQL. This creates the `bug_history` audit table in addition to the existing users, projects, bugs, and comments tables. Then configure the username and password in `src/main/resources/db.properties`.

For an existing Day 3 database, apply this migration once:

```sql
USE bug_tracking_db;
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
CREATE INDEX idx_history_bug_id ON bug_history(bug_id);
```

### How to Run

```bash
mvn test
mvn compile exec:java
```

The Maven test command always runs offline validation tests. Set `BUGTRACKER_MYSQL_TESTS=true` to additionally run the destructive-cleanup MySQL integration workflow tests against the configured database (they create uniquely named fixtures and remove them afterward). In PowerShell:

```powershell
$env:BUGTRACKER_MYSQL_TESTS = "true"
mvn test
```

At startup, the application verifies its MySQL connection and presents a menu for the dashboard, bug CRUD, comments, and history. Existing user/project CRUD methods remain available through their DAOs.

### Sample Workflow

1. Select `3` and report a bug with a title, description, severity, project ID, reporter ID, and optional developer ID.
2. Select `6` to add a comment, then `7` to review discussion on the bug.
3. Select `4` to assign a developer and change the status to `IN_PROGRESS`; the update is recorded in bug history.
4. Update the bug to `RESOLVED`, then back to `IN_PROGRESS` to record resolution and reopening events.
5. Select `9` to inspect the audit trail and `1` to review dashboard totals.

### Sample Output

```text
--- Bug Dashboard ---
Total bugs:       2
Open:             1
In progress:      1
Resolved:         0
Closed:           0
Critical:         1

1 Dashboard  2 List bugs  3 Report bug  4 Update bug
5 Delete bug  6 Add comment  7 View comments  8 Delete comment
9 Bug history  0 Exit
Select:
```

Counts vary with the current contents of the database; the example matches the two initial sample bugs in a fresh setup.

### Day 4 Progress

- [x] Implement bug CRUD, comment DAO/model behavior, and permission-checked deletion.
- [x] Record bug creation, status, assignment, severity, resolution, and reopening events transactionally.
- [x] Add dashboard aggregates and the interactive console menu.
- [x] Add input validation and unit/integration test coverage.
- [x] Document setup, migration, execution, and a sample workflow.

### Day 4 GitHub Push

```bash
git status
git add .
git commit -m "Day 4 - Comments, dashboard and testing"
git push origin main
```

