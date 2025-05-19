package com.example.teamalfred.database;

import java.sql.*;

public class MessagingDatabaseManager {

    public void initializeSchema() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance();
             Statement stmt = conn.createStatement()) {

            // Enable Write-Ahead Logging for better concurrency (optional)
            stmt.execute("PRAGMA journal_mode=WAL;");

            // Create conversations table
            String createConversations = "CREATE TABLE IF NOT EXISTS conversations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "userOneID INT NOT NULL, " +
                    "userTwoID INT NOT NULL" +
                    ");";
            stmt.execute(createConversations);

            // Create messages table
            String createMessages = "CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "conversationID INT NOT NULL, " +
                    "senderID INT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (conversationID) REFERENCES conversations(id)" +
                    ");";
            stmt.execute(createMessages);

        } catch (SQLException e) {
            System.err.println("Error initializing schema: " + e.getMessage());
            throw e;
        }
    }

    public void dropTable() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS messages;");
            stmt.execute("DROP TABLE IF EXISTS conversations;");
        } catch (SQLException e) {
            System.err.println("Error dropping tables: " + e.getMessage());
            throw e;
        }
    }

    public int createConversation(int userOneID, int userTwoID) {
        String sql = "INSERT INTO conversations (userOneID, userTwoID) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userOneID);
            stmt.setInt(2, userTwoID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating conversation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating conversation failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating conversation: " + e.getMessage());
            return -1;
        }
    }

    public void sendMessage(Connection conn, int conversationId, int senderId, String messageContent) {
        String sql = "INSERT INTO messages (conversationID, senderID, content) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conversationId);
            stmt.setInt(2, senderId);
            stmt.setString(3, messageContent);
            stmt.executeUpdate();
            System.out.println("Message sent successfully.");
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
