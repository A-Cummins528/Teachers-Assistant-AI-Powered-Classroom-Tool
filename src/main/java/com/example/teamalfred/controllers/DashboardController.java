package com.example.teamalfred.controllers;

import com.example.teamalfred.database.User;
import com.example.teamalfred.main.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import java.io.IOException;


import java.io.IOException;

public class DashboardController {

    @FXML
    private Label displayUserName;
    @FXML private StackPane contentPane;
    // ToggleButtons for each navigation item:
    @FXML private ToggleButton classManagementToggle;
    @FXML private ToggleButton assessmentToggle;
    @FXML private ToggleButton analyticsToggle;
    @FXML private ToggleButton resourcesToggle;
    @FXML private ToggleButton aiTutorToggle;
    @FXML private ToggleButton messageToggle;
    @FXML private ToggleButton aiQuizToggle;
    @FXML private ToggleButton settingsToggle;
    @FXML private ToggleButton aiLessonPlansToggle;
    @FXML private AnchorPane dashboardRoot; // root of the dashboard layout

    private double currentFontSize = 14.0; // base font size

    // Define styles for active/inactive toggle buttons:
    private static final String ACTIVE_BUTTON_STYLE =
            "-fx-background-color: lightblue; -fx-text-fill: white; -fx-alignment: center; -fx-border-radius: 4;";
    private static final String INACTIVE_BUTTON_STYLE =
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center; -fx-border-radius: 4;";

    // ToggleGroup to make buttons exclusive
    private ToggleGroup navGroup = new ToggleGroup();
    private final SwitchSceneController switchScene = new SwitchSceneController();
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            displayUserName.setText("Hey there, " + user.getFirstName());
        }
        User currentUser = UserSession.getInstance().getLoggedInUser();
    }

    @FXML
    private void initialize() {
        // Add all navigation toggle buttons to one ToggleGroup for mutual exclusivity
        classManagementToggle.setToggleGroup(navGroup);
        assessmentToggle.setToggleGroup(navGroup);
        analyticsToggle.setToggleGroup(navGroup);
        resourcesToggle.setToggleGroup(navGroup);
        aiTutorToggle.setToggleGroup(navGroup);
        aiQuizToggle.setToggleGroup(navGroup);
        settingsToggle.setToggleGroup(navGroup);
        aiLessonPlansToggle.setToggleGroup(navGroup);


        if (messageToggle != null) {  // in case "Message" toggle exists
            messageToggle.setToggleGroup(navGroup);
            // Optional: initialization logic here
        }
    }

    @FXML
    private void handleGoToSettings(ActionEvent event) {
        System.out.println("howdy");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/UpdateProfilePage.fxml"));
            Parent root = loader.load();

            SettingsController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene newScene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(newScene);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleNavigation(ActionEvent event) {
        // Determine which toggle was clicked
        ToggleButton clickedButton = (ToggleButton) event.getSource();
        String fxmlToLoad = null;

        if (clickedButton == classManagementToggle) {
            fxmlToLoad = "/com/example/teamalfred/ClassManagement.fxml";
        } else if (clickedButton == assessmentToggle) {
            fxmlToLoad = "/com/example/teamalfred/Assessment.fxml";
        } else if (clickedButton == analyticsToggle) {
            fxmlToLoad = "/com/example/teamalfred/Analytics.fxml";
        } else if (clickedButton == resourcesToggle) {
            fxmlToLoad = "/com/example/teamalfred/Resources.fxml";
        } else if (clickedButton == aiTutorToggle) {
            fxmlToLoad = "/com/example/teamalfred/AiTutor.fxml";
        } else if (clickedButton == messageToggle) {
            fxmlToLoad = "/com/example/teamalfred/Message.fxml";
        } else if (clickedButton == aiQuizToggle) {
            fxmlToLoad = "/com/example/teamalfred/AiQuiz.fxml";
        } else if (clickedButton == settingsToggle) {
        fxmlToLoad = "/com/example/teamalfred/SettingsPage.fxml";
        } else if (clickedButton == aiLessonPlansToggle) {
        fxmlToLoad = "/com/example/teamalfred/AiLessonPlans.fxml";
        }




        if (fxmlToLoad != null) {
            try {
                // Load the FXML content for the selected section
                Parent newContent = FXMLLoader.load(getClass().getResource(fxmlToLoad));
                // Replace the content of the contentPane with the new content
                contentPane.getChildren().setAll(newContent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Update styles: highlight the active button, reset others
        updateToggleStyles(clickedButton);
    }
    /** Utility method to update the background style of toggles, highlighting the active one. */
    private void updateToggleStyles(ToggleButton activeToggle) {
        // Set all toggles to the inactive style, then override the active one
        classManagementToggle.setStyle(INACTIVE_BUTTON_STYLE);
        assessmentToggle.setStyle(INACTIVE_BUTTON_STYLE);
        analyticsToggle.setStyle(INACTIVE_BUTTON_STYLE);
        resourcesToggle.setStyle(INACTIVE_BUTTON_STYLE);
        aiTutorToggle.setStyle(INACTIVE_BUTTON_STYLE);
        aiQuizToggle.setStyle(INACTIVE_BUTTON_STYLE);
        settingsToggle.setStyle(INACTIVE_BUTTON_STYLE);
        aiLessonPlansToggle.setStyle(INACTIVE_BUTTON_STYLE);


        if (activeToggle == aiLessonPlansToggle) {
            activeToggle.setStyle(ACTIVE_BUTTON_STYLE);
        }
        if (messageToggle != null) {
            messageToggle.setStyle(INACTIVE_BUTTON_STYLE);
        }
        System.out.println("activeToggle == assessmentToggle? " + (activeToggle == assessmentToggle));
        System.out.println("activeToggle == resourcesToggle? " + (activeToggle == resourcesToggle));
        System.out.println("activeToggle == aiTutorToggle? " + (activeToggle == aiTutorToggle));
        System.out.println("activeToggle == messageToggle? " + (activeToggle == messageToggle));
        // Set the clicked toggle's style to the active highlight color
        activeToggle.setStyle(ACTIVE_BUTTON_STYLE);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }
    @FXML
    private void increaseFontSize() {
        currentFontSize += 2;
        applyFontSize();
    }

    @FXML
    private void decreaseFontSize() {
        currentFontSize = Math.max(10, currentFontSize - 2); // prevent shrinking too small
        applyFontSize();
    }

    private void applyFontSize() {
        if (dashboardRoot != null) {
            dashboardRoot.setStyle("-fx-font-size: " + currentFontSize + "px;");
        }
    }
}