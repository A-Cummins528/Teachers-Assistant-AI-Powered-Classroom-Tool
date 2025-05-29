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

/**
 * Controller for the main dashboard of the application.
 * Manages navigation between different sections and provides
 * features like logout, settings, and dynamic font resizing.
 */
public class DashboardController {

    @FXML private Label displayUserName;
    @FXML private StackPane contentPane;

    // Navigation toggle buttons
    @FXML private ToggleButton classManagementToggle;
    @FXML private ToggleButton assessmentToggle;
    @FXML private ToggleButton aiTutorToggle;
    @FXML private ToggleButton messageToggle;
    @FXML private ToggleButton aiQuizToggle;
    @FXML private ToggleButton settingsToggle;
    @FXML private ToggleButton aiLessonPlansToggle;

    @FXML private AnchorPane dashboardRoot;

    private double currentFontSize = 14.0;

    // Style constants
    private static final String ACTIVE_BUTTON_STYLE =
            "-fx-background-color: lightblue; -fx-text-fill: white; -fx-alignment: center; -fx-border-radius: 4;";
    private static final String INACTIVE_BUTTON_STYLE =
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center; -fx-border-radius: 4;";

    private ToggleGroup navGroup = new ToggleGroup();
    private final SwitchSceneController switchScene = new SwitchSceneController();
    private User currentUser;

    /**
     * Sets the current user and updates the user greeting label.
     *
     * @param user The user currently logged in.
     */
    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            displayUserName.setText("Hey there, " + user.getFirstName());
        }
        // Optionally retrieve user from session for consistency
        User currentUser = UserSession.getInstance().getLoggedInUser();
    }

    /**
     * Initializes the controller after FXML components are loaded.
     * Sets up navigation button groups and styles.
     */
    @FXML
    private void initialize() {
        // Group navigation buttons for mutual exclusivity
        classManagementToggle.setToggleGroup(navGroup);
        assessmentToggle.setToggleGroup(navGroup);
        aiTutorToggle.setToggleGroup(navGroup);
        aiQuizToggle.setToggleGroup(navGroup);
        settingsToggle.setToggleGroup(navGroup);
        aiLessonPlansToggle.setToggleGroup(navGroup);

        if (messageToggle != null) {
            messageToggle.setToggleGroup(navGroup);
        }
    }

    /**
     * Loads the Settings page and passes the current user to its controller.
     *
     * @param event The ActionEvent triggered by clicking the settings button.
     */
    @FXML
    private void handleGoToSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/SettingsPage.fxml"));
            Parent root = loader.load();

            SettingsController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles navigation between different dashboard sections.
     * Dynamically loads FXML content into the content pane.
     *
     * @param event The ActionEvent from a toggle button click.
     */
    @FXML
    private void handleNavigation(ActionEvent event) {
        ToggleButton clickedButton = (ToggleButton) event.getSource();
        String fxmlToLoad = null;

        if (clickedButton == classManagementToggle) {
            fxmlToLoad = "/com/example/teamalfred/ClassManagement.fxml";
        } else if (clickedButton == assessmentToggle) {
            fxmlToLoad = "/com/example/teamalfred/Assessment.fxml";
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
                Parent newContent = FXMLLoader.load(getClass().getResource(fxmlToLoad));
                contentPane.getChildren().setAll(newContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updateToggleStyles(clickedButton);
    }

    /**
     * Updates the visual style of toggle buttons to indicate the active section.
     *
     * @param activeToggle The toggle button that was clicked.
     */
    private void updateToggleStyles(ToggleButton activeToggle) {
        classManagementToggle.setStyle(INACTIVE_BUTTON_STYLE);
        assessmentToggle.setStyle(INACTIVE_BUTTON_STYLE);
        aiTutorToggle.setStyle(INACTIVE_BUTTON_STYLE);
        aiQuizToggle.setStyle(INACTIVE_BUTTON_STYLE);
        settingsToggle.setStyle(INACTIVE_BUTTON_STYLE);
        aiLessonPlansToggle.setStyle(INACTIVE_BUTTON_STYLE);

        if (messageToggle != null) {
            messageToggle.setStyle(INACTIVE_BUTTON_STYLE);
        }

        activeToggle.setStyle(ACTIVE_BUTTON_STYLE);
    }

    /**
     * Logs the user out and switches back to the login scene.
     *
     * @param event The logout button's ActionEvent.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        MessageController.resetSession();
        UserSession.clearSession(); // clear current user session
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }

    /**
     * Increases the dashboard's font size.
     */
    @FXML
    private void increaseFontSize() {
        currentFontSize += 2;
        applyFontSize();
    }

    /**
     * Decreases the dashboard's font size, with a minimum limit.
     */
    @FXML
    private void decreaseFontSize() {
        currentFontSize = Math.max(10, currentFontSize - 2);
        applyFontSize();
    }

    /**
     * Applies the current font size setting to the dashboard root.
     */
    private void applyFontSize() {
        if (dashboardRoot != null) {
            dashboardRoot.setStyle("-fx-font-size: " + currentFontSize + "px;");
        }
    }


}
