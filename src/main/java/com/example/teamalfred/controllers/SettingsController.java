package com.example.teamalfred.controllers;

import com.example.teamalfred.database.UserDAO;
import com.example.teamalfred.database.SqliteUserDAO;
import com.example.teamalfred.database.User;
import com.example.teamalfred.controllers.MessageController;
import com.example.teamalfred.main.UserSession; // Assuming you might update session
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert; // For feedback messages
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class SettingsController {

    private final UserDAO userDAO = new SqliteUserDAO();
    private final SwitchSceneController switchScene = new SwitchSceneController();
    private User currentUser;

    @FXML private TextField firstNameSettings;
    @FXML private TextField lastNameSettings;
    @FXML private TextField mobileSettings;
    @FXML private TextField emailSettings; // Consider making this non-editable or handle uniqueness carefully
    @FXML private PasswordField passwordSettings;
    @FXML private PasswordField passwordSettingsConfirm;
    @FXML private BorderPane rootPane; // This now refers to your BorderPane root
    @FXML private Label invalidFirstname;
    @FXML private Label invalidLastname;
    @FXML private Label invalidMobile;
    @FXML private Label invalidEmail;
    @FXML private Label invalidPassword;
    @FXML private Label invalidPasswordConfirm; // Added: Ensure this fx:id exists in your FXML

    private static final int MIN_REQUIRED_VALID_FIELDS = 4; // For name, lastname, mobile, email


    private double currentFontSize = 14.0;

    // Call this to set the current user before showing the Settings page
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user == null) {
            // Handle error: no user to display settings for
            System.err.println("SettingsController: currentUser is null!");
            // Maybe redirect to login or show an error
            return;
        }
        populateFields(user);
    }
    

    // Fills in the text fields with user info
    private void populateFields(User user) {
        firstNameSettings.setText(user.getFirstName());
        lastNameSettings.setText(user.getLastName());
        mobileSettings.setText(user.getMobile());
        emailSettings.setText(user.getEmail());
        // Password fields remain empty by default for security.
        // User types new password if they want to change it.
    }

    // Runs when the user clicks 'Update'
    @FXML
    public void updateUserDetails(ActionEvent event) {
        clearAllValidationLabels();
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

        // Validate password only if user intends to change it (i.e., fields are not empty)
        String newPlainTextPassword = null;
        if (!passwordSettings.getText().isEmpty() || !passwordSettingsConfirm.getText().isEmpty()) {
            newPlainTextPassword = validatePassword();
            if ("Invalid".equals(newPlainTextPassword)) {
                proceedWithUpdate = false;
            }
        } // If both are empty, newPlainTextPassword remains null, meaning no password change

        if (proceedWithUpdate && validFieldsCounter >= MIN_REQUIRED_VALID_FIELDS) {
            try {
                User userToUpdate = new User();
                userToUpdate.setId(currentUser.getId());
                userToUpdate.setFirstName(updatedFirstname);
                userToUpdate.setLastName(updatedLastname);
                userToUpdate.setMobile(updatedMobile);

                // Email uniqueness check before setting
                if (!currentUser.getEmail().equalsIgnoreCase(updatedEmail)) {
                    Optional<User> existingUserByNewEmail = userDAO.findUserByEmail(updatedEmail);
                    if (existingUserByNewEmail.isPresent()) {
                        setInvalidLabel(invalidEmail, true, "Email already in use.");
                        return;
                    }
                }
                userToUpdate.setEmail(updatedEmail);


                // Set password
                if (newPlainTextPassword != null && !newPlainTextPassword.isEmpty()) {
                    userToUpdate.setPassword(newPlainTextPassword);
                } else {
                    userToUpdate.setPersistedPassword(currentUser.getPassword());
                }

                userToUpdate.setUserType(currentUser.getUserType());
                userToUpdate.setGrade(currentUser.getGrade());
                userToUpdate.setClassName(currentUser.getClassName());

                userDAO.updateUser(userToUpdate);
                this.currentUser = userToUpdate;
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");


            } catch (SQLException e) {
                e.printStackTrace();
                if (e.getMessage().toLowerCase().contains("unique constraint failed: users.email")) {
                    setInvalidLabel(invalidEmail, true, "Email already registered by another user.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update Failed", "Database error: " + e.getMessage());
                }
            } /*catch (IOException e) { // Only if switching scene
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the next page.");
            }*/
        } else {
            System.out.println("Validation failed. Update cancelled.");
            // Validation labels should already be showing errors.
        }
    }

    // Handles account deletion when delete button is clicked
    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete your account?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.deleteUser(currentUser.getId());
                UserSession.clearSession();
                switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "An error occurred while deleting the account.");
            }
        }
    }

    // Resets all validation error labels
    private void clearAllValidationLabels() {
        setInvalidLabel(invalidFirstname, false, "");
        setInvalidLabel(invalidLastname, false, "");
        setInvalidLabel(invalidMobile, false, "");
        setInvalidLabel(invalidEmail, false, "");
        setInvalidLabel(invalidPassword, false, "");
        setInvalidLabel(invalidPasswordConfirm, false, "");
    }

    // Clears all inputs (not currently used)
    private void clearInputs() {
        firstNameSettings.clear();
        lastNameSettings.clear();
        mobileSettings.clear();
        emailSettings.clear();
        passwordSettings.clear();
        passwordSettingsConfirm.clear();
        clearAllValidationLabels();
    }

    // Checks if the first name is valid
    private String validateFirstname() {
        String fname = firstNameSettings.getText().trim();
        if (fname.matches("[a-zA-Z\\s'-]{2,}")) {
            return fname;
        }
        setInvalidLabel(invalidFirstname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    // Checks if the last name is valid
    private String validateLastname() {
        String lname = lastNameSettings.getText().trim();
        if (lname.matches("[a-zA-Z\\s'-]{2,}")) {
            return lname;
        }
        setInvalidLabel(invalidLastname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    // Checks if the mobile number is a valid Australian number
    private String validateMobile() {
        String mobile = mobileSettings.getText().replaceAll("\s+", "");
        if (mobile.matches("^04\\d{8}$")) {
            return mobile;
        }
        setInvalidLabel(invalidMobile, true, "Aus mobile: 04XXXXXXXX.");
        return "Invalid";
    }

    // Checks if the email address is in a valid format
    private String validateEmail() {
        String email = emailSettings.getText().trim().toLowerCase();
        if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            return email;
        }
        setInvalidLabel(invalidEmail, true, "Invalid email format.");
        return "Invalid";
    }

    // Validates passwords and makes sure they match and are secure
    private String validatePassword() {
        String pass = passwordSettings.getText();
        String confirmPass = passwordSettingsConfirm.getText();

        // If both fields are empty, user does not intend to change password
        if (pass.isEmpty() && confirmPass.isEmpty()) {
            setInvalidLabel(invalidPassword, false, "");
            setInvalidLabel(invalidPasswordConfirm, false, "");
            return null;
        }

        // If one is empty and other is not, it's an error
        if (pass.isEmpty() || confirmPass.isEmpty()){
            setInvalidLabel(invalidPassword, true, "Both password fields required to change, or leave both empty.");
            setInvalidLabel(invalidPasswordConfirm, true, "");
            return "Invalid";
        }


        if (pass.length() < 7) {
            setInvalidLabel(invalidPassword, true, "Password min 7 characters.");
            setInvalidLabel(invalidPasswordConfirm, false, "");
            return "Invalid";
        }
        setInvalidLabel(invalidPassword, false, "");

        if (!pass.equals(confirmPass)) {
            setInvalidLabel(invalidPasswordConfirm, true, "Passwords do not match.");
            return "Invalid";
        }
        setInvalidLabel(invalidPasswordConfirm, false, "");

        return pass;
    }

    // Shows error messages beside input fields
    private void setInvalidLabel(Label label, boolean isInvalid, String message) {
        if (label != null) {
            label.setText(isInvalid ? message : "");
            label.setVisible(isInvalid);
        }
    }

    // Displays an alert popup with a given message
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Goes back to the Dashboard when cancel is pressed
    @FXML
    private void handleCancel(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/Dashboard.fxml");
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
        if (rootPane != null) {
            rootPane.setStyle("-fx-font-size: " + currentFontSize + "px;");
        }
    }
    @FXML
    private void handleGoToDashboard(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/Dashboard.fxml");
    }

    @FXML
    private void handleGoToSettings(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/SettingsPage.fxml");
    }

    @FXML
    private void handleGoToProfile(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/UpdateProfilePage.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        MessageController.resetSession();
        UserSession.clearSession(); // clear current user session
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }
}