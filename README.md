# Bug Tracking System 🐞

A robust, enterprise-style Bug Tracking System application built using **Core Java**, **JDBC (Java Database Connectivity)**, and **MySQL**, managed with **Apache Maven**.

---

## 📌 Project Objective

The **Bug Tracking System** is designed to streamline software issue management. It allows developers, testers, and project managers to report, assign, track, update, and resolve software bugs across different projects with role-based access control and comment tracking.

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
git remote add origin https://github.com/<your-username>/bug-tracking-system.git

# 6. Push to GitHub
git push -u origin main
```

---

## 🔮 Next Steps (Day 2 Preview)
* Implement DAO interfaces (`UserDAO`, `ProjectDAO`, `BugDAO`, `BugCommentDAO`).
* Implement JDBC CRUD operations with `PreparedStatement` to safely interact with MySQL.
