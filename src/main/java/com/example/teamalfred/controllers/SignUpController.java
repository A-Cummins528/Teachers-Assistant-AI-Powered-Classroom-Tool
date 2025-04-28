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

public class SignUpController {

    private final UserDAO userDAO = new SqliteUserDAO();
    private int masterValidationCounter;
    private boolean masterValidation = false;

    private User createUser;
    private final SwitchSceneController switchScene = new SwitchSceneController();

    @FXML
    private TextField firstNameSignup;
    @FXML
    private TextField lastNameSignup;
    @FXML
    private TextField mobileSignup;
    @FXML
    private TextField emailSignup;
    @FXML
    private PasswordField passwordSignup;
    @FXML
    private PasswordField passwordSignupConfirm;
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

    @FXML
    public void userSignup(ActionEvent event) throws IOException, SQLException {
        masterValidationCounter = 0;
        checkUserSignup(event);
    }

    private void checkUserSignup(ActionEvent event) throws IOException, SQLException {
        String userFirstname = validateFirstname();
        String userLastname = validateLastname();
        String userMobile = validateMobile();
        String userEmail = validateEmail();
        String password = validatePassword();

        if (masterValidationCounter == 5) {
            masterValidation = true;
        }

        if (masterValidation) {
            createUser = new User(userFirstname, userLastname, userEmail, userMobile, password);
            userDAO.createUser(createUser);

            clearInputs();
            switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
        }
    }

    private void clearInputs() {
        firstNameSignup.clear();
        lastNameSignup.clear();
        mobileSignup.clear();
        passwordSignup.clear();
        emailSignup.clear();
    }

    private String validateFirstname() {
        if (firstNameSignup.getText().matches("[a-zA-Z]+")) {
            masterValidationCounter++;
            return firstNameSignup.getText();
        } else {
            setInvalidLabel(invalidFirstname, true);
            return "Invalid";
        }
    }

    private String validateLastname() {
        if (lastNameSignup.getText().matches("[a-zA-Z]+")) {
            masterValidationCounter++;
            return lastNameSignup.getText();
        } else {
            setInvalidLabel(invalidLastname, true);
            return "Invalid";
        }
    }

    private String validateMobile() {
        String cleanedInput = mobileSignup.getText().replaceAll("\\s+", "");
        if (cleanedInput.matches("\\d{10}") && cleanedInput.startsWith("04")) {
            masterValidationCounter++;
            return cleanedInput;
        }
        setInvalidLabel(invalidMobile, true);
        mobileSignup.clear();
        return "Invalid";
    }

    private String validateEmail() {
        if (emailSignup.getText().contains("@")) {
            masterValidationCounter++;
            return emailSignup.getText().toLowerCase();
        }
        invalidEmail.setText("Invalid email");
        return "Invalid";
    }

    private String validatePassword() {
        if (passwordSignup.getText().length() >= 7) {
            masterValidationCounter++;
            return passwordSignup.getText();
        }
        invalidPassword.setText("Password too short");
        return "Invalid";
    }

    private void setInvalidLabel(Label label, boolean set) {
        label.setText(set ? "Invalid*" : "");
    }

    @FXML
    private void handleLoginRedirect(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }
}
//TODO: Need to prevent signup with duplicate email addresses. Exception is thrown as it should, and unit test passes, but it needs to be handled here too.
// It would be good to go back and review the unit tests and make sure we are validating input here to match expected behaviour.