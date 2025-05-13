package com.example.teamalfred.controllers.messaging;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageController {

    private void addConversationPreview(String name, String lastMessage, String time) {
        HBox previewBox = new HBox();  // ✅ LOCAL declaration — this is important
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
        previewBox.setOnMouseClicked(e -> loadConversation(name));

        // Add to left sidebar
        conversationList.getChildren().add(previewBox);
    }

}
