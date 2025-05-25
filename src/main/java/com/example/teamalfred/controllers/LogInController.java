package com.example.teamalfred.controllers;

import com.example.teamalfred.database.UserDAO;
import com.example.teamalfred.database.SqliteUserDAO;
import com.example.teamalfred.database.User;
import com.example.teamalfred.main.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


/**
 * Controller for the Login screen (Login.fxml).
 * Handles user authentication against the database.
 */
public class LogInController {

    // DAO for accessing user data
    private final UserDAO userDAO;
    // Helper for switching scenes
    private final SwitchSceneController switchScene = new SwitchSceneController();

    @FXML private Label welcomeText;
    @FXML private Button loginButton;
    @FXML private Label failedLogin;
    @FXML private TextField emailLogin;
    @FXML private PasswordField password;
    @FXML private BorderPane loginRoot;

    private double currentFontSize = 14.0;
    /**
     * Constructor: Initializes the controller, creating an instance of the UserDAO.
     * Note: Creates a direct dependency on SqliteUserDAO.
     */
    public LogInController() {
        // Directly creates the DAO instance.
        this.userDAO = new SqliteUserDAO();
    }


    /**
     * Handles the action event when the user clicks the login button.
     * Initiates the login check process and handles potential database errors.
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If scene switching fails.
     */
    @FXML
    public void userLogin(ActionEvent event) throws IOException {
        try {
            // Attempt to log the user in
            checkLogin(event);
        } catch (SQLException e) {
            // Handle potential database errors during login check
            e.printStackTrace(); // Log the error for debugging
            failedLogin.setText("Database error. Please try again later.");
        }
    }

    /**
     * Performs the actual login verification logic.
     * Retrieves user input, queries the database via DAO, and checks credentials.
     * @param event The ActionEvent from the login button (used for scene switching).
     * @throws IOException If scene switching fails.
     * @throws SQLException If a database error occurs during user lookup.
     */
    private void checkLogin(ActionEvent event) throws IOException, SQLException {
        String email = emailLogin.getText().trim().toLowerCase(); // Get and trim email
        String plainTextPassword = password.getText(); // Get plaintext password input

        // Basic validation for empty fields
        if (email.isEmpty() || plainTextPassword.isEmpty()) {
            failedLogin.setText("Email and password fields are mandatory.");
            return;
        }

        // Find user by email using the DAO
        Optional<User> optionalUser = userDAO.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            // User found in database
            User user = optionalUser.get();

            if (user.checkPassword(plainTextPassword)) {
                // Password matches the stored hash
                failedLogin.setText("Login successful!"); // Or clear the message
                openDashboardWithUser(event, user); // Pass user to Dashboard
            } else {
                // Password does NOT match the stored hash
                failedLogin.setText("Incorrect email or password.");
                resetInputs();
            }
        } else {
            // User email not found in database
            failedLogin.setText("Incorrect email or password.");
            resetInputs();
        }
    }

    /**
     * Opens the Dashboard and passes the logged-in User object to it.
     * @param event The ActionEvent from the login button.
     * @param user The authenticated User object.
     * @throws IOException If scene loading fails.
     */
    private void openDashboardWithUser(ActionEvent event, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/Dashboard.fxml"));
        // Creates user session
        UserSession.initSession(user);
        System.out.println("made user session + " + user.getFirstName());
        Parent root = loader.load();

        DashboardController dashboardController = loader.getController();
        dashboardController.setUser(user); // Pass the logged-in user to dashboard

        // Set up the stage
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("AcademiAI Dashboard");
        stage.show();
    }

    /**
     * Clears the email and password input fields.
     */
    private void resetInputs() {
        emailLogin.setText("");
        password.setText("");
    }

    /**
     * Handles the action event for the Sign Up button/link.
     * Switches the scene to the Sign Up view.
     * @param event The ActionEvent triggered by the button/link click.
     */
    @FXML
    private void handleSignUp(ActionEvent event) {
        // Switch to the sign-up scene
        switchScene.switchScene(event,"/com/example/teamalfred/SignUp.fxml");
    }
    @FXML
    private void increaseFontSize() {
        currentFontSize += 2;
        applyFontSize();
    }

    @FXML
    private void decreaseFontSize() {
        currentFontSize = Math.max(10, currentFontSize - 2);
        applyFontSize();
    }

    private void applyFontSize() {
        if (loginRoot != null) {
            loginRoot.setStyle("-fx-font-size: " + currentFontSize + "px;");
        }
    }
}