package com.example.teamalfred.controllers;
import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.IUserDAO;
import com.example.teamalfred.database.User;
import com.example.teamalfred.controllers.SignUpController.*;
import com.example.teamalfred.controllers.switchSceneController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class SettingsController {
    // userDAO for database entry
    private IUserDAO userDAO;
    // Master validation counter variable, starts at 0, if all 5 user inputs fields are validated, masterValidationCounter will equal 5.
    private int masterValidationCounter;
    // Master validation variable, only set to true once ALL user data is validated and account is being created
    private boolean masterValidation = false;

    private Label invalidFirstname;
    @FXML
    private Label invalidLastname;
    @FXML
    private Label invalidMobile;
    @FXML
    private Label invalidEmail;
    @FXML
    private Label invalidPassword;

    private switchSceneController switchScene = new switchSceneController();
    // default connection variable for database
    private Connection connection;

    // settings controller constructor
    public SettingsController() {
        // initiate connect to database
        connection = DatabaseConnection.getInstance();
    }

    // public user update function (linked to signup button)
    public void userProfileUpdate(ActionEvent event) throws IOException {
        // reset mastervalidationcounter to 0
        masterValidationCounter = 0;
        checkProfileUpdate(event);

    }
    // private user signup function
    private void checkProfileUpdate(ActionEvent event) throws IOException {
        Main m = new Main();

        // initial variables for new user info, all run through input validation methods
        String userFirstname = validateFirstname();
        String userLastname = validateLastname();
        String userMobile = validateMobile();
        String userEmail = validateEmail();
        String password = validatePassword();
        // if masterValidationCounter == 5, all 5 input validations passed, masterValidation set to true.
        // signup can continue
        if(masterValidationCounter == 5) {
            masterValidation = true;
        }
        // Reffer to above comments
        if(masterValidation) {
            // create new user object with user inputs

            userDAO.

// Need to call the existing users DAO and use updateUser on that. Need a reference point.
            // call addUser method in databaseUserDAO and parse in the new createUser (object of new user info)
            userDAO.updateUser(updateUser, userDAO.getUser(userEmail));

            // clear input fields & switch to dashboard scene
            clearInputs(true);
            switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
        }
    }
    private boolean CheckEmailDatabase(inputEmail, userEmail){
        User user = userDAO.getUser(emailLogin.getText().toString());
        return
    }
    // validate firstname input
    private String validateFirstname() throws IOException {
        // Check if first name input has only letters in it.
        if(firstNameSignup.getText().toString().matches("[a-zA-Z]*")) {
            // if true, name input is valid...
            masterValidationCounter++;
            return firstNameSignup.getText().toString();
        } else {
            // invalid input
            setInvalidLabel(invalidFirstname, true);
            return "Invalid";
        }
    }

    // Validate lastname input
    private String validateLastname() throws IOException {
        // Check if first name input has only letters in it.
        if(lastNameSignup.getText().toString().matches("[a-zA-Z]*")) {
            // if true, name input is valid...

            masterValidationCounter++;
            return lastNameSignup.getText().toString();
        } else {
            // invalid input
            setInvalidLabel(invalidLastname, true);
            return "Invalid";
        }
    }

    // valid mobile input

    private String validateMobile() throws IOException {
        String rawInput = mobileSignup.getText().toString();
        // cleaned input incase user enters format: 0411 111 111 instead of 0411111111
        String cleanedInput = rawInput.replaceAll("\\s+", ""); // remove all spaces
        if(cleanedInput.matches("\\d*") && cleanedInput.toString().length() == 10 && cleanedInput.startsWith("04")) {
            // Mobile is only digits, mobile is 10 digits long, mobile starts with 04
            masterValidationCounter++;
            return cleanedInput.toString();
        }
        // else, mobile must be invalid.
        setInvalidLabel(invalidMobile, true);
        mobileSignup.setText("");
        return "invalid";
    }

    // valid email input

    private String validateEmail() throws IOException {
        String rawInput = emailSignup.getText().toString();
        // check if input contains @ symbol
        if(rawInput.contains("@")) {
            masterValidationCounter++;
            return rawInput;
        }
        // invalid email, set invalid email label and return invalid
        invalidEmail.setText("Invalid email");
        return "invalid";
    }


    // valid password length

    private String validatePassword() throws IOException {
        String rawInput = passwordSignup.getText().toString();
        // check if input is 7 or more characters
        if(rawInput.length() >= 7 ) {
            masterValidationCounter++;
            return rawInput;
            // password is too short, return invalid
        } else {
            invalidPassword.setText("Password too short");
            return "invalid";
        }
    }


    // method to update label if user input fails validation
    private void setInvalidLabel(Label label, boolean set) throws IOException {
        if(set) {
            label.setText("Invalid*");
        } else {
            label.setText("");
        }
    }
}

