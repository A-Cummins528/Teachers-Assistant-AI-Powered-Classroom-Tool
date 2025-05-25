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

/**
 * This class handles the messaging UI logic for displaying conversations and sending messages.
 * It's connected to the FXML UI and handles user interaction like loading messages,
 * sending messages, and switching between conversations.
 */
public class MessageController {

    // Our DB manager for handling message and conversation tables
    private final MessagingDatabaseManager dbManager = new MessagingDatabaseManager();

    // UI elements from FXML file
    @FXML private TextField messageInput;           // Input field for typing new messages
    @FXML private VBox messageContainer;            // Container to hold all messages for a conversation
    @FXML private VBox conversationList;            // Sidebar showing list of all conversations
    @FXML private Button sendButton;                // Button to send messages

    // Stores the ID of the currently selected conversation
    private static int currentConversationId = 1;

    /**
     * Utility method to show popup alerts (for errors, info, etc.)
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Called automatically when the controller is loaded.
     * Sets up the database schema and loads conversations/messages.
     */
    public void initialize() {
        try {
            // Ensure the necessary message tables exist
            try (Connection conn = DatabaseConnection.getInstance()) {
                dbManager.initializeSchema();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not initialize message tables.");
            return;
        }

        // Load messages for the current conversation and display all conversations
        loadMessages();
        loadConversations();

        // Set what happens when the user clicks "Send"
        sendButton.setOnAction(event -> {
            String content = messageInput.getText().trim();
            if (!content.isEmpty()) {
                sendMessage(content);   // actually send it
                messageInput.clear();   // clear the input box
                loadMessages();         // refresh messages
                loadConversations();    // refresh conversation previews
            }
        });
    }

    /**
     * Resets the current conversation (can be called from other controllers if needed)
     */
    public static void resetSession() {
        currentConversationId = -1;
    }

    /**
     * Loads all messages for the current conversation from the DB and displays them in the UI.
     */
    private void loadMessages() {
        messageContainer.getChildren().clear();  // Clear old messages

        String query = "SELECT senderID, content, timestamp FROM messages WHERE conversationID = ? ORDER BY timestamp";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentConversationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int senderId = rs.getInt("senderID");
                    String content = rs.getString("content");
                    String timestamp = rs.getString("timestamp");

                    // Create a new label for each message
                    Label messageLabel = new Label(content);
                    messageLabel.setWrapText(true);        // allows multiline messages
                    messageLabel.setMaxWidth(300);         // limit message bubble width

                    // Wrap label in HBox to align left/right depending on sender
                    HBox messageBox = new HBox(messageLabel);

                    // Style based on who sent the message
                    if (senderId == UserSession.getLoggedInUser().getId()) {
                        // My message
                        messageLabel.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 8; -fx-background-radius: 10;");
                        messageBox.setStyle("-fx-alignment: center-right; -fx-padding: 5;");
                    } else {
                        // Their message
                        messageLabel.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 8; -fx-background-radius: 10;");
                        messageBox.setStyle("-fx-alignment: center-left; -fx-padding: 5;");
                    }

                    messageContainer.getChildren().add(messageBox); // add to UI
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the "New Message" button is clicked.
     * Prompts user to enter a user ID, checks if valid, and starts a new convo or opens existing one.
     */
    public void handleNewMessageButton(ActionEvent event) {
        // Prompt user to enter the ID of who they want to message
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

                // Prevent users from messaging themselves
                if (targetUserId == currentUserId) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "You can't message yourself.");
                    return;
                }

                // Try to create or fetch an existing conversation
                int newConversationId = dbManager.createOrGetConversation(currentUserId, targetUserId);

                if (newConversationId > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Conversation Created",
                            "New chat started with user ID: " + targetUserId);

                    // Set this as the current conversation
                    currentConversationId = newConversationId;
                    loadConversations();
                    loadMessages();

                    // Highlight the conversation just opened
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

    /**
     * Sends a message into the currently selected conversation.
     *
     * @param content The message text.
     */
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

    /**
     * Loads all conversations for the logged-in user and shows them in the sidebar.
     * Most recent conversations appear first.
     */
    public void loadConversations() {
        int loggedInUserId = UserSession.getLoggedInUser().getId();

        String sql = """
                SELECT c.id,
                CASE 
                    WHEN c.userOneID = ? THEN 'User ' || c.userTwoID
                    WHEN c.userTwoID = ? THEN 'User ' || c.userOneID
                    ELSE 'Conversation' END AS name,
                m.content, m.timestamp
                FROM conversations c
                LEFT JOIN messages m ON m.id = (
                    SELECT id FROM messages WHERE conversationID = c.id ORDER BY timestamp DESC LIMIT 1
                )
                WHERE c.userOneID = ? OR c.userTwoID = ?
                ORDER BY m.timestamp DESC
                """;

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loggedInUserId);
            stmt.setInt(2, loggedInUserId);
            stmt.setInt(3, loggedInUserId);
            stmt.setInt(4, loggedInUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                conversationList.getChildren().clear();  // Clear old convos

                while (rs.next()) {
                    int conversationId = rs.getInt("id");
                    String name = rs.getString("name");
                    String lastMessage = rs.getString("content");
                    String timestamp = rs.getString("timestamp");

                    // Make a small preview box with name and snippet
                    HBox convBox = createConversationPreviewBox(conversationId, name, lastMessage, timestamp);
                    conversationList.getChildren().add(convBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a preview box for a conversation (name, last msg, time).
     *
     * @param conversationId The convo's ID.
     * @param name           User X / User Y
     * @param lastMessage    Most recent message
     * @param time           Timestamp string
     * @return A styled HBox preview
     */
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

        Label timestamp = new Label(time != null && time.length() >= 16 ? time.substring(11, 16) : "");
        timestamp.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11;");
        timestamp.setPrefWidth(60);

        textContainer.getChildren().addAll(nameLabel, messageSnippet);
        previewBox.getChildren().addAll(textContainer, timestamp);

        // Store ID for later
        previewBox.setUserData(conversationId);

        // Clicking it switches the chat
        previewBox.setOnMouseClicked(e -> {
            currentConversationId = (int) previewBox.getUserData();
            loadMessages();
            highlightSelectedConversation(previewBox);
        });

        return previewBox;
    }

    /**
     * Changes the background colour of the selected conversation so it's visually active.
     *
     * @param selectedBox The selected conversation HBox.
     */
    private void highlightSelectedConversation(HBox selectedBox) {
        // Reset style for all boxes
        for (var node : conversationList.getChildren()) {
            node.setStyle("-fx-padding: 10; -fx-background-color: #3a3f47;");
        }
        // Highlight the selected one
        selectedBox.setStyle("-fx-padding: 10; -fx-background-color: #1f8ef1;");
    }
}
