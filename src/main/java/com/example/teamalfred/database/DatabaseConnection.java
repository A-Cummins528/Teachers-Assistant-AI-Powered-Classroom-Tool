package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides access to a singleton database Connection instance for an SQLite database.
 * <p>
 * This class uses the Singleton design pattern. By default, it connects to
 * "jdbc:sqlite:database.db". For testing, a different URL (like an in-memory DB)
 * can be specified using {@link #setTestDatabaseUrl(String)}.
 * </p>
 */
public class DatabaseConnection {

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:database.db?busy_timeout=5000";
    private static Connection instance = null;
    private static String activeDbUrl = null;
    private static String testDbUrlOverride = null;

    private DatabaseConnection() {}

    public static synchronized void setTestDatabaseUrl(String url) {
        testDbUrlOverride = url;
        closeInstance();
    }

    public static synchronized Connection getInstance() throws SQLException {
        String targetUrl = (testDbUrlOverride != null) ? testDbUrlOverride : DEFAULT_DB_URL;

        try {
            if (instance == null || instance.isClosed() || !targetUrl.equals(activeDbUrl)) {
                if (instance != null && !instance.isClosed()) {
                    instance.close();
                }

                instance = DriverManager.getConnection(targetUrl);
                activeDbUrl = targetUrl;

                // Enable WAL mode and foreign keys for better performance and integrity
                try (Statement stmt = instance.createStatement()) {
                    stmt.execute("PRAGMA journal_mode=WAL;");
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }
        } catch (SQLException sqlEx) {
            System.err.println("Error getting/creating database connection for URL: " + targetUrl);
            sqlEx.printStackTrace();
            instance = null;
            activeDbUrl = null;
            throw sqlEx;
        }

        return instance;
    }

    public static synchronized void closeInstance() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    instance.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection:");
                e.printStackTrace();
            } finally {
                instance = null;
                activeDbUrl = null;
            }
        }
    }

    public static synchronized void resetForTesting() {
        closeInstance();
        testDbUrlOverride = null;
    }
}
