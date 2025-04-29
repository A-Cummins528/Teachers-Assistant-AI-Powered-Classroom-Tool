package com.example.teamalfred.controllers;

import com.example.teamalfred.database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label displayUserName;

    private final SwitchSceneController switchScene = new SwitchSceneController();
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            displayUserName.setText("Hey there, " + user.getFirstName());
        }
    }

    @FXML
    private void initialize() {
        // Optional: initialization logic here
    }

    @FXML
    private void handleGoToSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/UpdateProfilePage.fxml"));
            Parent root = loader.load();

            SettingsController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene newScene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }
}
