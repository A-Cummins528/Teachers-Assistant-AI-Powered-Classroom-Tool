package com.example.teamalfred.controllers;

import com.example.teamalfred.database.MessagingDatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;


public class MessageController {
    private final MessagingDatabaseManager dbManager = new MessagingDatabaseManager();
    @FXML private TextField messageInput;

    private void addConversationPreview(String name, String lastMessage, String time) {
        HBox previewBox = new HBox();  //
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

        Label timestamp = new Label(time);
        timestamp.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11;");
        timestamp.setPrefWidth(60);

        previewBox.getChildren().addAll(textContainer, timestamp);

        // Handle click event
        //previewBox.setOnMouseClicked(e -> loadConversation(name));

        // Add to left sidebar
        //conversationList.getChildren().add(previewBox);
    }

    public void sendMessage() {
        String messageContent = messageInput.getText();
        if (messageContent == null || messageContent.trim().isEmpty()) {
            System.out.println("Message is empty.");
            return;
        }
        try {
            int conversationId = 1; // ← Replace with the actual selected conversation
            int senderId = 123;     // ← Replace with the actual logged-in user ID
            dbManager.sendMessage(dbManager.getConnection(), conversationId, senderId, messageContent);
            messageInput.clear(); // clear the input box after sending
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}