package com.example.teamalfred.controllers;

import com.example.teamalfred.database.UserDAO;
import com.example.teamalfred.database.SqliteUserDAO;
import com.example.teamalfred.database.User;
import com.example.teamalfred.main.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Controller for managing user settings including updating profile details,
 * changing passwords, deleting accounts, and font resizing.
 */
public class SettingsController {

    private final UserDAO userDAO = new SqliteUserDAO();
    private final SwitchSceneController switchScene = new SwitchSceneController();
    private User currentUser;

    @FXML private TextField firstNameSettings;
    @FXML private TextField lastNameSettings;
    @FXML private TextField mobileSettings;
    @FXML private TextField emailSettings;
    @FXML private PasswordField passwordSettings;
    @FXML private PasswordField passwordSettingsConfirm;
    @FXML private BorderPane rootPane;
    @FXML private Label invalidFirstname;
    @FXML private Label invalidLastname;
    @FXML private Label invalidMobile;
    @FXML private Label invalidEmail;
    @FXML private Label invalidPassword;
    @FXML private Label invalidPasswordConfirm;

    private static final int MIN_REQUIRED_VALID_FIELDS = 4;
    private double currentFontSize = 14.0;

    /**
     * Initializes the controller by setting the current user and populating the fields.
     */
    @FXML
    public void initialize() {
        setCurrentUser();
    }

    /**
     * Sets the current user from the session and populates the form fields.
     */
    private void setCurrentUser() {
        currentUser = UserSession.getLoggedInUser();
        if (currentUser == null) {
            System.err.println("SettingsController: currentUser is null!");
            return;
        }
        populateFields(currentUser);
    }

    /**
     * Populates text fields with the current user's data.
     *
     * @param user the current logged-in user
     */
    private void populateFields(User user) {
        firstNameSettings.setText(user.getFirstName());
        lastNameSettings.setText(user.getLastName());
        mobileSettings.setText(user.getMobile());
        emailSettings.setText(user.getEmail());
    }

    /**
     * Validates and updates user details. If validations pass,
     * updates the user in the database and session.
     *
     * @param event the button event triggering the update
     */
    @FXML
    public void updateUserDetails(ActionEvent event) {
        clearAllValidationLabels();
        if (currentUser == null) {
            System.err.println("Error: Current user is null.");
            return;
        }
        boolean proceedWithUpdate = true;
        int validFieldsCounter = 0;

        String updatedFirstname = validateFirstname();
        if ("Invalid".equals(updatedFirstname)) proceedWithUpdate = false; else validFieldsCounter++;

        String updatedLastname = validateLastname();
        if ("Invalid".equals(updatedLastname)) proceedWithUpdate = false; else validFieldsCounter++;

        String updatedMobile = validateMobile();
        if ("Invalid".equals(updatedMobile)) proceedWithUpdate = false; else validFieldsCounter++;

        String updatedEmail = validateEmail();
        if ("Invalid".equals(updatedEmail)) proceedWithUpdate = false; else validFieldsCounter++;

        String newPlainTextPassword = null;
        if (!passwordSettings.getText().isEmpty() || !passwordSettingsConfirm.getText().isEmpty()) {
            newPlainTextPassword = validatePassword();
            if ("Invalid".equals(newPlainTextPassword)) {
                proceedWithUpdate = false;
            }
        }

        if (proceedWithUpdate && validFieldsCounter >= MIN_REQUIRED_VALID_FIELDS) {
            try {
                User currentUser = UserSession.getLoggedInUser();
                User userToUpdate = new User();
                userToUpdate.setId(currentUser.getId());
                userToUpdate.setFirstName(updatedFirstname);
                userToUpdate.setLastName(updatedLastname);
                userToUpdate.setMobile(updatedMobile);

                if (!currentUser.getEmail().equalsIgnoreCase(updatedEmail)) {
                    Optional<User> existingUserByNewEmail = userDAO.findUserByEmail(updatedEmail);
                    if (existingUserByNewEmail.isPresent()) {
                        setInvalidLabel(invalidEmail, true, "Email already in use.");
                        return;
                    }
                }
                userToUpdate.setEmail(updatedEmail);

                if (newPlainTextPassword != null && !newPlainTextPassword.isEmpty()) {
                    userToUpdate.setPassword(newPlainTextPassword);
                } else {
                    userToUpdate.setPersistedPassword(currentUser.getPassword());
                }

                userToUpdate.setUserType(currentUser.getUserType());
                userToUpdate.setGrade(currentUser.getGrade());
                userToUpdate.setClassName(currentUser.getClassName());

                userDAO.updateUser(userToUpdate);
                UserSession.getInstance().setLoggedInUser(userToUpdate);
                passwordSettings.clear();
                passwordSettingsConfirm.clear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");

            } catch (SQLException e) {
                e.printStackTrace();
                if (e.getMessage().toLowerCase().contains("unique constraint failed: users.email")) {
                    setInvalidLabel(invalidEmail, true, "Email already registered by another user.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update Failed", "Database error: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Validation failed. Update cancelled.");
        }
    }

    /**
     * Prompts the user to confirm deletion and removes the account if confirmed.
     * Redirects to the login page upon successful deletion.
     *
     * @param event the delete button event
     */
    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        setCurrentUser();
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete your account?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                User currentUser = UserSession.getLoggedInUser();
                userDAO.deleteUser(currentUser.getId());
                UserSession.clearSession();
                switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "An error occurred while deleting the account.");
            }
        }
    }

    /**
     * Clears all validation labels and hides them.
     */
    private void clearAllValidationLabels() {
        setInvalidLabel(invalidFirstname, false, "");
        setInvalidLabel(invalidLastname, false, "");
        setInvalidLabel(invalidMobile, false, "");
        setInvalidLabel(invalidEmail, false, "");
        setInvalidLabel(invalidPassword, false, "");
        setInvalidLabel(invalidPasswordConfirm, false, "");
    }

    /**
     * Validates the first name input field.
     * Accepts letters, spaces, apostrophes, and hyphens, minimum 2 characters.
     *
     * @return the valid first name or "Invalid" if it doesn't pass.
     */
    private String validateFirstname() {
        String fname = firstNameSettings.getText().trim();
        if (fname.matches("[a-zA-Z\\s'-]{2,}")) return fname;
        setInvalidLabel(invalidFirstname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    /**
     * Validates the last name input field.
     * Accepts letters, spaces, apostrophes, and hyphens, minimum 2 characters.
     *
     * @return the valid last name or "Invalid" if it doesn't pass.
     */
    private String validateLastname() {
        String lname = lastNameSettings.getText().trim();
        if (lname.matches("[a-zA-Z\\s'-]{2,}")) return lname;
        setInvalidLabel(invalidLastname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    /**
     * Validates the mobile number format for Australian numbers.
     * Must begin with 04 and contain exactly 10 digits.
     *
     * @return the valid mobile number or "Invalid".
     */
    private String validateMobile() {
        String mobile = mobileSettings.getText().replaceAll(" +", "");
        if (mobile.matches("^04\\d{8}$")) return mobile;
        setInvalidLabel(invalidMobile, true, "Aus mobile: 04XXXXXXXX.");
        return "Invalid";
    }

    /**
     * Validates the email address against a regex pattern.
     * Ensures a proper format for email input.
     *
     * @return the valid email address or "Invalid".
     */
    private String validateEmail() {
        String email = emailSettings.getText().trim().toLowerCase();
        if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) return email;
        setInvalidLabel(invalidEmail, true, "Invalid email format.");
        return "Invalid";
    }

    /**
     * Validates the new password and confirmation fields.
     * Password must be at least 7 characters and match the confirmation.
     *
     * @return the password if valid, "Invalid" if not, or null if both fields are empty.
     */
    private String validatePassword() {
        String pass = passwordSettings.getText();
        String confirmPass = passwordSettingsConfirm.getText();

        // If both fields are empty, no password change
        if (pass.isEmpty() && confirmPass.isEmpty()) {
            setInvalidLabel(invalidPassword, false, "");
            setInvalidLabel(invalidPasswordConfirm, false, "");
            return null;
        }

        // If one is empty
        if (pass.isEmpty()) {
            setInvalidLabel(invalidPassword, true, "Password field empty.");
            setInvalidLabel(invalidPasswordConfirm, false, "");
            return "Invalid";
        }
        if (confirmPass.isEmpty()) {
            setInvalidLabel(invalidPassword, false, "");
            setInvalidLabel(invalidPasswordConfirm, true, "Confirmation field empty.");
            return "Invalid";
        }

        // If its too short
        if (pass.length() < 7) {
            setInvalidLabel(invalidPassword, true, "Password min 7 characters.");
            setInvalidLabel(invalidPasswordConfirm, false, "");
            return "Invalid";
        }

        // If they mismatch
        if (!pass.equals(confirmPass)) {
            setInvalidLabel(invalidPassword, false, "");
            setInvalidLabel(invalidPasswordConfirm, true, "Passwords do not match.");
            return "Invalid";
        }

        // If its all good
        setInvalidLabel(invalidPassword, false, "");
        setInvalidLabel(invalidPasswordConfirm, false, "");
        return pass;
    }


    /**
     * Updates a label's visibility and message for validation feedback.
     * Controls its visibility, text content, and layout management.
     *
     * @param label the JavaFX Label to modify
     * @param isInvalid true if the label should be shown
     * @param message the message to display
     */
    private void setInvalidLabel(Label label, boolean isInvalid, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(isInvalid);
            label.setManaged(isInvalid);
        }
    }

    /**
     * Displays an alert dialog box with the provided message, title, and type.
     *
     * @param alertType the type of alert (e.g., INFORMATION, ERROR)
     * @param title the title of the alert dialog
     * @param message the content message of the alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigates away from the Settings page and back to the Dashboard.
     *
     * @param event the ActionEvent triggered by clicking cancel
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/Dashboard.fxml");
    }

    /**
     * Increases the font size used within the settings view for accessibility.
     */
    @FXML
    private void increaseFontSize() {
        currentFontSize += 2;
        applyFontSize();
    }

    /**
     * Decreases the font size within the settings view, with a lower bound of 10px.
     */
    @FXML
    private void decreaseFontSize() {
        currentFontSize = Math.max(10, currentFontSize - 2);
        applyFontSize();
    }

    /**
     * Applies the current font size setting to the root pane’s inline CSS.
     * Updates UI components to reflect font size changes.
     */
    private void applyFontSize() {
        if (rootPane != null) {
            rootPane.setStyle("-fx-font-size: " + currentFontSize + "px;");
        }
    }
}
