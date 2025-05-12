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
    @FXML private TextField emailSettings;
    @FXML private PasswordField passwordSettings;
    @FXML private PasswordField passwordSettingsConfirm;

    @FXML private Label invalidFirstname;
    @FXML private Label invalidLastname;
    @FXML private Label invalidMobile;
    @FXML private Label invalidEmail;
    @FXML private Label invalidPassword;
    @FXML private Label invalidPasswordConfirm;

    private static final int MIN_REQUIRED_VALID_FIELDS = 4;

    // Sets the current user and loads their data into the fields
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user == null) {
            System.err.println("SettingsController: currentUser is null!");
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

        String newPlainTextPassword = null;
        if (!passwordSettings.getText().isEmpty() || !passwordSettingsConfirm.getText().isEmpty()) {
            newPlainTextPassword = validatePassword();
            if ("Invalid".equals(newPlainTextPassword)) {
                proceedWithUpdate = false;
            }
        }

        if (proceedWithUpdate && validFieldsCounter >= MIN_REQUIRED_VALID_FIELDS) {
            try {
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
                this.currentUser = userToUpdate;
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

        if (pass.isEmpty() && confirmPass.isEmpty()) {
            setInvalidLabel(invalidPassword, false, "");
            setInvalidLabel(invalidPasswordConfirm, false, "");
            return null;
        }

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
}