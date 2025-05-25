package com.example.teamalfred.database;

import java.sql.*;

public class MessagingDatabaseManager {

    /**
     * Sets up the database tables if they don't already exist.
     * This includes the "conversations" and "messages" tables.
     */
    public void initializeSchema() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance();
             Statement stmt = conn.createStatement()) {

            // Enable Write-Ahead Logging for better performance and concurrency
            stmt.execute("PRAGMA journal_mode=WAL;");

            // Create the "conversations" table if it doesn't already exist
            String createConversations = "CREATE TABLE IF NOT EXISTS conversations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +  // Unique ID for the conversation
                    "userOneID INT NOT NULL, " +                         // One participant's user ID
                    "userTwoID INT NOT NULL" +                           // Other participant's user ID
                    ");";
            stmt.execute(createConversations);

            // Create the "messages" table if it doesn't already exist
            String createMessages = "CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +  // Unique ID for the message
                    "conversationID INT NOT NULL, " +                    // Foreign key to the conversation
                    "senderID INT NOT NULL, " +                          // User ID of sender
                    "content TEXT NOT NULL, " +                          // Actual message content
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +  // Time the message was sent
                    "FOREIGN KEY (conversationID) REFERENCES conversations(id)" + // Ensures referential integrity
                    ");";
            stmt.execute(createMessages);

        } catch (SQLException e) {
            System.err.println("Error initializing schema: " + e.getMessage());
            throw e; // Rethrow to notify calling method
        }
    }

    /**
     * Deletes the messages and conversations tables.
     * Useful for resetting the database during development or testing.
     */
    public void dropTable() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance();
             Statement stmt = conn.createStatement()) {

            // Drop the messages table first due to foreign key dependency
            stmt.execute("DROP TABLE IF EXISTS messages;");
            stmt.execute("DROP TABLE IF EXISTS conversations;");

        } catch (SQLException e) {
            System.err.println("Error dropping tables: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates a new conversation between two users.
     * Returns the ID of the newly created conversation.
     */
    /**
     * Returns an existing conversation ID between two users, or creates a new one if none exists.
     */
    public int createOrGetConversation(int userOneID, int userTwoID) {
        String checkSql = "SELECT id FROM conversations WHERE " +
                "(userOneID = ? AND userTwoID = ?) OR (userOneID = ? AND userTwoID = ?)";
        String insertSql = "INSERT INTO conversations (userOneID, userTwoID) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance()) {
            // Check for existing conversation
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userOneID);
                checkStmt.setInt(2, userTwoID);
                checkStmt.setInt(3, userTwoID); // reverse check
                checkStmt.setInt(4, userOneID);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id"); // Return existing conversation ID
                }
            }

            // If no conversation exists, create a new one
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userOneID);
                insertStmt.setInt(2, userTwoID);
                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return new conversation ID
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating or retrieving conversation: " + e.getMessage());
        }

        return -1; // If something fails
    }


    /**
     * Adds a new message to a conversation.
     * This method is useful when you already have a connection open.
     */
    public void sendMessage(Connection conn, int conversationId, int senderId, String messageContent) {
        String sql = "INSERT INTO messages (conversationID, senderID, content) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Bind values to the prepared statement
            stmt.setInt(1, conversationId);
            stmt.setInt(2, senderId);
            stmt.setString(3, messageContent);

            stmt.executeUpdate(); // Insert the message
            System.out.println("Message sent successfully.");
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
