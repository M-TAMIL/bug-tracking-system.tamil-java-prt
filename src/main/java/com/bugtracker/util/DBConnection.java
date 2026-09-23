package com.bugtracker.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing MySQL JDBC database connections.
 * <p>
 * Reads database connection configuration from `db.properties` on the classpath.
 * Provides helper methods to acquire, close, and test database connections.
 */
public class DBConnection {

    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        loadProperties();
        try {
            // Load MySQL JDBC driver class
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found in classpath!");
            System.err.println("Ensure mysql-connector-j dependency is added to pom.xml.");
            e.printStackTrace();
        }
    }

    // Private constructor to prevent instantiation
    private DBConnection() {
    }

    /**
     * Loads database configuration properties from db.properties file.
     * Falls back to default values if the properties file cannot be loaded.
     */
    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/bug_tracking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "");
            } else {
                System.out.println("[WARN] db.properties not found in classpath. Using default settings.");
                setDefaultProperties();
            }
        } catch (Exception e) {
            System.err.println("[WARN] Failed to load db.properties: " + e.getMessage() + ". Using default settings.");
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        driver = "com.mysql.cj.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/bug_tracking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        username = "root";
        password = "";
    }

    /**
     * Establishes and returns a new database connection.
     *
     * @return active java.sql.Connection instance
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Safely closes an open database connection.
     *
     * @param conn the Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to close database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Tests the database connection and prints diagnostic information.
     *
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        System.out.println("--------------------------------------------------");
        System.out.println("Testing MySQL Database Connection via JDBC...");
        System.out.println("URL: " + url);
        System.out.println("User: " + username);
        System.out.println("Driver: " + driver);
        System.out.println("--------------------------------------------------");

        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("[SUCCESS] Successfully connected to MySQL database: " + conn.getCatalog());
                System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName() +
                        " " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("JDBC Driver: " + conn.getMetaData().getDriverName() +
                        " " + conn.getMetaData().getDriverVersion());
                System.out.println("--------------------------------------------------");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[FAILURE] Could not establish connection to the database!");
            System.err.println("Error Message : " + e.getMessage());
            System.err.println("SQL State     : " + e.getSQLState());
            System.err.println("Error Code    : " + e.getErrorCode());
            System.err.println("\nTroubleshooting Tips:");
            System.err.println(" 1. Ensure your MySQL server (XAMPP / MySQL Workbench / Docker) is running on port 3306.");
            System.err.println(" 2. Run 'database/schema.sql' to create the 'bug_tracking_db' database.");
            System.err.println(" 3. Verify 'db.username' and 'db.password' in 'src/main/resources/db.properties'.");
            System.err.println("--------------------------------------------------");
        }
        return false;
    }

    /**
     * Main method to allow running DBConnection directly for testing.
     */
    public static void main(String[] args) {
        testConnection();
    }
}
