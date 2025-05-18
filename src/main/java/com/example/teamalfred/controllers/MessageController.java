package com.example.teamalfred.controllers;

import com.example.teamalfred.database.MessagingDatabaseManager;
import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.main.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;



public class MessageController {
    private final MessagingDatabaseManager dbManager = new MessagingDatabaseManager();
    @FXML private TextField messageInput;
    @FXML
    private VBox messageContainer;
    @FXML private VBox conversationList;
    @FXML
    private Button sendButton;


    private int currentConversationId = 1;

    public void initialize() {
        loadMessages();

        sendButton.setOnAction(event -> {
            String content = messageInput.getText().trim();
            if (!content.isEmpty()) {
                sendMessage(content);
                messageInput.clear();
                loadMessages();
                loadConversations();
            }
        });
    }

    private void loadMessages() {
        messageContainer.getChildren().clear();

        String query = "SELECT sender_id, content, timestamp FROM messages WHERE conversation_id = ? ORDER BY timestamp";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentConversationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sender = "User " + rs.getInt("sender_id");
                String content = rs.getString("content");
                String rawTimestamp = rs.getString("timestamp");
                String formattedTime = rawTimestamp.substring(11, 16); // HH:mm

                Label message = new Label(sender + " [" + formattedTime + "]: " + content);
                message.setStyle("-fx-text-fill: white;");

                messageContainer.getChildren().add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String content) {
        String insert = "INSERT INTO messages (conversation_id, sender_id, content, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(insert)) {

            stmt.setInt(1, currentConversationId);
            stmt.setInt(2, UserSession.getLoggedInUser().getId()); // Example sender_id, adjust as needed
            stmt.setString(3, content);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void loadConversations() {
        conversationList.getChildren().clear();

        String query = "SELECT c.id, c.name, m.content, m.timestamp " +
                "FROM conversations c " +
                "LEFT JOIN messages m ON m.id = (" +
                "SELECT id FROM messages WHERE conversation_id = c.id ORDER BY timestamp DESC LIMIT 1" +
                ") " +
                "ORDER BY c.last_updated DESC";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int conversationId = rs.getInt("id");
                String name = rs.getString("name");
                String lastMessage = rs.getString("content");
                String time = rs.getString("timestamp");

                if (lastMessage == null) {
                    lastMessage = "(No messages)";
                    time = "";
                }

                HBox previewBox = createConversationPreviewBox(conversationId, name, lastMessage, time);
                conversationList.getChildren().add(previewBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private HBox createConversationPreviewBox(int conversationId, String name, String lastMessage, String time) {
        HBox previewBox = new HBox();
        previewBox.setPrefHeight(50);
        previewBox.setStyle("-fx-padding: 10; -fx-background-color: #3a3f47;");
        previewBox.setSpacing(10);

        VBox textContainer = new VBox();
        textContainer.setPrefWidth(190);
        textContainer.setSpacing(3);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label messageSnippet = new Label(lastMessage);
        messageSnippet.setStyle("-fx-text-fill: #CCCCCC; -fx-font-size: 12;");
        messageSnippet.setMaxWidth(180);
        messageSnippet.setWrapText(false);

        textContainer.getChildren().addAll(nameLabel, messageSnippet);

        Label timestamp = new Label(time != null ? time.substring(11, 16) : "");
        timestamp.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11;");
        timestamp.setPrefWidth(60);

        previewBox.getChildren().addAll(textContainer, timestamp);

        // Store conversationId to switch conversations on click
        previewBox.setUserData(conversationId);

        previewBox.setOnMouseClicked(e -> {
            currentConversationId = (int) previewBox.getUserData();
            loadMessages();
            highlightSelectedConversation(previewBox);
        });

        return previewBox;
    }
    private void highlightSelectedConversation(HBox selectedBox) {
        for (var node : conversationList.getChildren()) {
            node.setStyle("-fx-padding: 10; -fx-background-color: #3a3f47;");
        }
        selectedBox.setStyle("-fx-padding: 10; -fx-background-color: #1f8ef1;");  // highlight color
    }
}




