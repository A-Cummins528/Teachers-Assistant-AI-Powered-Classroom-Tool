package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides access to a singleton database Connection instance for an SQLite database.
 * <p>
 * This class uses the Singleton design pattern. By default, it connects to
 * "jdbc:sqlite:database.db". For testing, a different URL (like an in-memory DB)
 * can be specified using {@link #setTestDatabaseUrl(String)}.
 * </p>
 */
public class DatabaseConnection {

    private static String DEFAULT_DB_URL = "jdbc:sqlite:database.db";
    private static Connection instance = null;
    private static String activeDbUrl = null; // Store the URL of the *current* active instance
    private static String testDbUrlOverride = null; // Holds the override URL for testing

    /**
     * Sets the default database URL for new connections.
     * Closes any existing connection to ensure the new URL is used on the next
     * call to {@link #getInstance()} if no test URL is set.
     *
     * @param newUrl The new default database URL string (e.g., "jdbc:sqlite:another_database.db").
     */
    public static void setDatabaseUrl(String newUrl) {
        DEFAULT_DB_URL = newUrl;
        instance = null; // force reconnect on next use
    }
    /** Private constructor to prevent instantiation. */
    private DatabaseConnection() {}

    /**
     * Sets a specific database URL to be used for the connection, typically for testing.
     * This URL will be used the next time {@link #getInstance()} needs to establish
     * or re-establish a connection. Closes any existing connection to ensure the
     * new URL is used on the next getInstance() call.
     * Setting this to null reverts to the default URL for subsequent connections.
     * <p>
     * IMPORTANT: Call this BEFORE calling {@link #getInstance()} in your test setup.
     * </p>
     *
     * @param url The database URL string (e.g., "jdbc:sqlite::memory:") or null to use default.
     */
    public static synchronized void setTestDatabaseUrl(String url) {
        testDbUrlOverride = url;
        // Close existing instance, if any, to force re-creation with the potentially new URL
        closeInstance();
    }

    /**
     * Retrieves the singleton database Connection instance.
     * <p>
     * Connects using the URL specified by {@link #setTestDatabaseUrl(String)} if set,
     * otherwise uses the default URL ("jdbc:sqlite:database.db").
     * If the instance is null or closed, it attempts to establish a new connection.
     * </p>
     *
     * @return The singleton {@link Connection} instance.
     * @throws SQLException if a database access error occurs during connection.
     */
    public static synchronized Connection getInstance() throws SQLException {
        // Determine the URL that *should* be used for a new connection
        String targetUrl = (testDbUrlOverride != null) ? testDbUrlOverride : DEFAULT_DB_URL;

        try {
            // Reconnect if:
            // 1. No instance exists
            // 2. Instance is closed
            // 3. The target URL is different from the URL of the current active instance
            if (instance == null || instance.isClosed() || !targetUrl.equals(activeDbUrl)) {

                // Close the old instance if it exists and is open (e.g., URL changed)
                if (instance != null && !instance.isClosed()) {
                    instance.close();
                }

                // Establish new connection with the target URL
                // System.out.println("DEBUG: Connecting to DB: " + targetUrl); // Optional debug line
                instance = DriverManager.getConnection(targetUrl);
                activeDbUrl = targetUrl; // Update the URL of the active instance
            }
        } catch (SQLException sqlEx) {
            System.err.println("Error getting/creating database connection for URL: " + targetUrl);
            sqlEx.printStackTrace();
            // Ensure instance state is consistent on error
            instance = null;
            activeDbUrl = null;
            throw sqlEx; // Re-throw the exception
        }
        return instance;
    }

    /**
     * Closes the singleton database connection instance if it's open and not null.
     * Sets the internal instance variable and active URL to null.
     */
    public static synchronized void closeInstance() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    // System.out.println("DEBUG: Closing DB connection: " + activeDbUrl); // Optional debug line
                    instance.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection:");
                e.printStackTrace();
            } finally {
                instance = null;
                activeDbUrl = null; // Reset active URL tracking
            }
        }
    }

    /**
     * Resets the DatabaseConnection for testing.
     * Closes any existing connection and removes the test database URL override.
     * Subsequent calls to getInstance() will use the default URL unless
     * setTestDatabaseUrl is called again.
     */
    public static synchronized void resetForTesting() {
        closeInstance(); // Close any existing connection
        testDbUrlOverride = null; // Remove the test URL override
    }
}