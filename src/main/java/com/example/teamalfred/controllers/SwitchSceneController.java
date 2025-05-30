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

/**
 * Controller class responsible for switching between different scenes in the application.
 * This class uses fade-in and fade-out animations to create smooth transitions.
 *
 * Usage:
 * - Create an instance of this class in your other controllers.
 * - Call switchScene() when you want to change scenes, passing the button event and FXML path.
 */
public class SwitchSceneController {

    /**
     * Switches the current scene to a new one specified by the FXML file path.
     * Applies fade-out effect on the old scene and fade-in effect on the new scene for smooth transition.
     *
     * @param event The ActionEvent triggered by a button click or other UI interaction.
     * @param path  The relative path to the FXML file of the new scene (e.g., "/com/example/login.fxml").
     */
    @FXML
    public void switchScene(ActionEvent event, String path) {
        try {
            // Load the FXML file from the provided path
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            // Create a new Scene using the loaded FXML root node
            Scene scene = new Scene(root);

            // Get the current stage by grabbing the window from the event source (e.g., the button)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Prepare a fade-out transition on the current scene's root node
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0); // fully visible
            fadeOut.setToValue(0.0);   // fully transparent

            // After fade-out finishes, switch scenes and fade the new scene in
            fadeOut.setOnFinished(e -> {
                // Set the new scene to the stage (window)
                stage.setScene(scene);

                // Create a fade-in transition on the new scene's root node
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), scene.getRoot());
                fadeIn.setFromValue(0.0); // start transparent
                fadeIn.setToValue(1.0);   // end fully visible
                fadeIn.play();
            });

            // Start the fade-out animation
            fadeOut.play();

        } catch (IOException e) {
            // Print the stack trace if loading the FXML fails
            e.printStackTrace();

            // In a real application, you might want to show an alert or log this error more gracefully
        }
    }
}
