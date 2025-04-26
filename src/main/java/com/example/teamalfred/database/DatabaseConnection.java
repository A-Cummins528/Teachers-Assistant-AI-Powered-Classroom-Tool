package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides access to a singleton database Connection instance for an SQLite database.
 * <p>
 * This class uses the Singleton design pattern to ensure only one connection
 * is created using a hardcoded database URL ("jdbc:sqlite:database.db").
 * </p>
 */
public class DatabaseConnection {

    /**
     * Holds the single, static Connection instance.
     * Initialized only when getInstance() is first called.
     */
    private static Connection instance = null;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Attempts to connect to the hardcoded SQLite database URL "jdbc:sqlite:database.db".
     * If the connection is successful, it assigns the Connection object directly to the
     * static {@code instance} field.
     * If a SQLException occurs during connection, it prints the exception to System.err.
     */
    private DatabaseConnection() {
        // Hardcoded URL for the SQLite database file
        String url = "jdbc:sqlite:database.db";
        try {
            // Attempt connection and assign directly to static field
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            // Basic error handling: print exception to standard error
            System.err.println(sqlEx);
            // instance remains null if connection fails
        }
    }

    /**
     * Retrieves the singleton database Connection instance.
     * <p>
     * On the first call (when the internal instance is null), it triggers the private
     * constructor to attempt database connection. Subsequent calls return the
     * previously established instance.
     * </p><p>
     * Note: If the initial connection attempt in the constructor fails (due to SQLException),
     * the internal instance remains null, and this method will return null.
     * </p>
     *
     * @return The singleton {@link Connection} instance, or null if the connection failed on the first attempt.
     */
    public static synchronized Connection getInstance() {
        try {
            // Check if the instance exists AND if it's closed
            if (instance != null && instance.isClosed()) {
                instance = null; // Set to null so a new one is created below
            }

            // If instance is null (either initially or because it was closed), create it
            if (instance == null) {
                String url = "jdbc:sqlite:database.db";
                instance = DriverManager.getConnection(url);
            }
        } catch (SQLException sqlEx) {
            System.err.println("Error getting/creating database connection:");
            sqlEx.printStackTrace(); // Good to print stack trace for errors
            instance = null; // Ensure instance is null if connection fails
        }
        return instance;
    }


    /**
     * Closes the singleton database connection instance if it's open.
     * <p>
     * This method should be called appropriately during application shutdown
     * to release database resources. Handles potential SQLExceptions during close.
     * Sets the static instance to null after closing.
     * </p>
     *
     * @implNote Call this when app exits.
     */
    public static void closeInstance() {
        if (instance != null) {
            try {
                instance.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection:");
                e.printStackTrace();
            } finally {
                instance = null; // Allow re-initialization if getInstance is called again
            }
        }
    }
}

// TODO: Throw a custom exception instead of printing errors to System.err
// TODO: Implement closeInstance() elsewhere when shutting down the application