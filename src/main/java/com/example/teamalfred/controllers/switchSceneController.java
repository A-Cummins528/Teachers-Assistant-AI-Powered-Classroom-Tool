package com.example.teamalfred.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class switchSceneController {


    @FXML
    public void switchScene(ActionEvent event, String path) {
        try {
            // Load the LogIn.fxml file in the background
            FXMLLoader loaderc = new FXMLLoader(getClass().getResource(path));
            Parent root = loaderc.load();

            // Create a new scene with the loaded FXML
            Scene scene = new Scene(root);

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Apply fade-out effect for the current scene
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                // Switch the scene after the fade-out is complete
                stage.setScene(scene);

                // Apply fade-in effect for the new scene
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), scene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }
}
