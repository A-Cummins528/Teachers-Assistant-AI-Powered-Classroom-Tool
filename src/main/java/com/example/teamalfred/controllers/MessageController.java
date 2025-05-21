package com.example.teamalfred.controllers;

import com.example.teamalfred.database.MessagingDatabaseManager;
import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.main.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.Optional;

public class MessageController {
    private final MessagingDatabaseManager dbManager = new MessagingDatabaseManager();

    @FXML private TextField messageInput;
    @FXML private VBox messageContainer;
    @FXML private VBox conversationList;
    @FXML private Button sendButton;

    private static int currentConversationId = 1;
    // Remove persistent Connection field to avoid reuse of closed connections
    // private Connection conn;  <-- removed

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initialize() {
        try {
            // Initialize schema once using a fresh connection
            try (Connection conn = DatabaseConnection.getInstance()) {
                dbManager.initializeSchema();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not initialize message tables.");
            return;
        }

        loadMessages();
        loadConversations();

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

    public static void resetSession() {
        currentConversationId = -1;
    }


    private void loadMessages() {
        messageContainer.getChildren().clear();

        String query = "SELECT senderID, content, timestamp FROM messages WHERE conversationID = ? ORDER BY timestamp";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentConversationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int senderId = rs.getInt("senderID");
                    String content = rs.getString("content");
                    String timestamp = rs.getString("timestamp");

                    Label messageLabel = new Label(content);
                    messageLabel.setWrapText(true);

                    // Simple style: Different background depending on sender
                    if (senderId == UserSession.getLoggedInUser().getId()) {
                        messageLabel.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 8; -fx-background-radius: 10; -fx-alignment: center-right;");
                        messageLabel.setMaxWidth(300);
                        // Align right for your own messages
                        HBox messageBox = new HBox();
                        messageBox.getChildren().add(messageLabel);
                        messageBox.setStyle("-fx-alignment: center-right; -fx-padding: 5;");
                        messageContainer.getChildren().add(messageBox);
                    } else {
                        messageLabel.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 8; -fx-background-radius: 10; -fx-alignment: center-left;");
                        messageLabel.setMaxWidth(300);
                        // Align left for others' messages
                        HBox messageBox = new HBox();
                        messageBox.getChildren().add(messageLabel);
                        messageBox.setStyle("-fx-alignment: center-left; -fx-padding: 5;");
                        messageContainer.getChildren().add(messageBox);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleNewMessageButton(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Message");
        dialog.setHeaderText("Start a New Chat");
        dialog.setContentText("Enter the User ID of the person you want to message:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(userIdStr -> {
            if (userIdStr.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "User ID cannot be empty.");
                return;
            }

            try {
                int targetUserId = Integer.parseInt(userIdStr);
                int currentUserId = UserSession.getLoggedInUser().getId();

                if (targetUserId == currentUserId) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "You can't message yourself.");
                    return;
                }

                int newConversationId = dbManager.createOrGetConversation(currentUserId, targetUserId);

                if (newConversationId > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Conversation Created",
                            "New chat started with user ID: " + targetUserId);

                    currentConversationId = newConversationId;

                    // Reload conversations and messages
                    loadConversations();
                    loadMessages();

                    // Find and highlight the new conversation in the UI
                    for (var node : conversationList.getChildren()) {
                        if (node.getUserData() instanceof Integer id && id == newConversationId) {
                            highlightSelectedConversation((HBox) node);
                            break;
                        }
                    }

                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Could not create conversation.");
                }

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "User ID must be a number.");
            }
        });
    }

    private void sendMessage(String content) {
        String insert = "INSERT INTO messages (conversationID, senderID, content, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, currentConversationId);
            stmt.setInt(2, UserSession.getLoggedInUser().getId());
            stmt.setString(3, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadConversations() {
        int loggedInUserId = UserSession.getLoggedInUser().getId(); // dynamically get current user

        String sql = "SELECT c.id, " +
                "CASE " +
                "WHEN c.userOneID = ? THEN 'User ' || c.userTwoID " +
                "WHEN c.userTwoID = ? THEN 'User ' || c.userOneID " +
                "ELSE 'Conversation' END AS name, " +
                "m.content, m.timestamp " +
                "FROM conversations c " +
                "LEFT JOIN messages m ON m.id = ( " +
                "  SELECT id FROM messages WHERE conversationID = c.id ORDER BY timestamp DESC LIMIT 1 " +
                ") " +
                "WHERE c.userOneID = ? OR c.userTwoID = ? " +
                "ORDER BY m.timestamp DESC";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loggedInUserId);
            stmt.setInt(2, loggedInUserId);
            stmt.setInt(3, loggedInUserId);
            stmt.setInt(4, loggedInUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                conversationList.getChildren().clear();

                while (rs.next()) {
                    int conversationId = rs.getInt("id");
                    String name = rs.getString("name");
                    String lastMessage = rs.getString("content");
                    String timestamp = rs.getString("timestamp");

                    HBox convBox = createConversationPreviewBox(conversationId, name, lastMessage, timestamp);
                    conversationList.getChildren().add(convBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally show alert here
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

        Label timestamp = new Label(time != null && time.length() >= 16 ? time.substring(11, 16) : "");
        timestamp.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11;");
        timestamp.setPrefWidth(60);

        previewBox.getChildren().addAll(textContainer, timestamp);

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
        selectedBox.setStyle("-fx-padding: 10; -fx-background-color: #1f8ef1;");
    }
}
