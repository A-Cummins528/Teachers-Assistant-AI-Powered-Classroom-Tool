package com.example.teamalfred.controllers;

import com.example.teamalfred.database.UserDAO;
import com.example.teamalfred.database.SqliteUserDAO;
import com.example.teamalfred.database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;

public class SettingsController {

    private final UserDAO userDAO = new SqliteUserDAO();
    private int masterValidationCounter;
    private boolean masterValidation = false;

    private final SwitchSceneController switchScene = new SwitchSceneController();
    private User currentUser;

    @FXML
    private TextField firstNameSettings;
    @FXML
    private TextField lastNameSettings;
    @FXML
    private TextField mobileSettings;
    @FXML
    private TextField emailSettings;
    @FXML
    private PasswordField passwordSettings;
    @FXML
    private PasswordField passwordSettingsConfirm;
    @FXML
    private Label invalidFirstname;
    @FXML
    private Label invalidLastname;
    @FXML
    private Label invalidMobile;
    @FXML
    private Label invalidEmail;
    @FXML
    private Label invalidPassword;

    // Call this to set the current user before showing the Settings page
    public void setCurrentUser(User user) {
        this.currentUser = user;
        populateFields(user);
    }

    private void populateFields(User user) {
        firstNameSettings.setText(user.getFirstName());
        lastNameSettings.setText(user.getLastName());
        mobileSettings.setText(user.getMobile());
        emailSettings.setText(user.getEmail());
        // Password fields usually remain empty for security
    }

    @FXML
    public void updateUserDetails(ActionEvent event) throws IOException, SQLException {
        masterValidationCounter = 0;
        checkUserUpdate(event);
    }

    private void checkUserUpdate(ActionEvent event) throws IOException, SQLException {
        String updatedFirstname = validateFirstname();
        String updatedLastname = validateLastname();
        String updatedMobile = validateMobile();
        String updatedEmail = validateEmail();
        String updatedPassword = validatePassword();

        if (masterValidationCounter == 5) {
            masterValidation = true;
        }

        if (masterValidation) {
            User updatedUser = new User(updatedFirstname, updatedLastname, updatedEmail, updatedMobile, updatedPassword);
            updatedUser.setId(currentUser.getId());

            userDAO.updateUser(updatedUser);

            clearInputs();
            switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
        } else {
            System.out.println("Mastervalidation FALSE");
        }
    }

    private void clearInputs() {
        firstNameSettings.clear();
        lastNameSettings.clear();
        mobileSettings.clear();
        emailSettings.clear();
        passwordSettings.clear();
        passwordSettingsConfirm.clear();
    }

    private String validateFirstname() {
        if (firstNameSettings.getText().matches("[a-zA-Z]+")) {
            masterValidationCounter++;
            return firstNameSettings.getText();
        } else {
            setInvalidLabel(invalidFirstname, true);
            return "Invalid";
        }
    }

    private String validateLastname() {
        if (lastNameSettings.getText().matches("[a-zA-Z]+")) {
            masterValidationCounter++;
            return lastNameSettings.getText();
        } else {
            setInvalidLabel(invalidLastname, true);
            return "Invalid";
        }
    }

    private String validateMobile() {
        String cleanedInput = mobileSettings.getText().replaceAll("\\s+", "");
        if (cleanedInput.matches("\\d{10}") && cleanedInput.startsWith("04")) {
            masterValidationCounter++;
            return cleanedInput;
        }
        setInvalidLabel(invalidMobile, true);
        mobileSettings.clear();
        return "Invalid";
    }

    private String validateEmail() {
        String email_ = emailSettings.getText().trim();
        if (email_ != null && !email_.isEmpty()) {
            if (email_.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                masterValidationCounter++;
                return email_.toLowerCase();
            }
        }
        invalidEmail.setText("Invalid email");
        return "Invalid";
    }

    private String validatePassword() {
        if (passwordSettings.getText().length() >= 7 && passwordSettings.getText().equals(passwordSettingsConfirm.getText())) {
            masterValidationCounter++;
            return passwordSettings.getText();
        }
        invalidPassword.setText("Passwords must match and be at least 7 characters");
        return "Invalid";
    }

    private void setInvalidLabel(Label label, boolean set) {
        label.setText(set ? "Invalid*" : "");
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }
}


