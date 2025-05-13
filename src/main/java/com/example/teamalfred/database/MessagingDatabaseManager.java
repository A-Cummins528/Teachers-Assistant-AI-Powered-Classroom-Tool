package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.example.teamalfred.database.DatabaseConnection.closeInstance;
import static java.sql.DriverManager.getConnection;


public class MessagingDatabaseManager {

    public void initializeSchema() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS conversations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "userOneID INT NOT NULL, " +
                "userTwoID INT NOT NULL" +
                ");";
        Connection conn = getConnection(); // Get the shared connection
        // Use try-with-resources ONLY for the Statement
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        String sqlb = "CREATE TABLE IF NOT EXISTS messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "conversationID INT NOT NULL, " +
                "senderID INT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (conversationID) REFERENCES conversations(id)" +
                ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlb);
        }
        // DO NOT close the connection here - it's managed by DatabaseConnection
        // Messages Table
    }

    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + "messages";

        Connection conn = getConnection(); // Get the shared connection
        // Use try-with-resources ONLY for the Statement
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        // DO NOT close the connection here - it's managed by DatabaseConnection
    }

    private Connection getConnection() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        // getInstance() now throws SQLException if connection fails, so null check isn't strictly needed here
        // but doesn't hurt.
        if (conn == null) {
            // This case should ideally not be reached if getInstance throws exception on failure
            throw new SQLException("Database connection could not be established (returned null).");
        }
        // Check if connection is closed (optional, defensive check)
        if (conn.isClosed()) {
            throw new SQLException("Database connection is closed.");
        }
        return conn;
    }
}
