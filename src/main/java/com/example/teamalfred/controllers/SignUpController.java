package com.example.teamalfred.controllers;

import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.UserDAO;
import com.example.teamalfred.database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;


// SIGN UP PAGE CONTROLLER ## DO NOT TOUCH - JOSH

public class SignUpController {

    // initial values from input elements in fxml scene

    // userDAO for database entry
    private UserDAO userDAO;
    // Master validation counter variable, starts at 0, if all 5 user inputs fields are validated, masterValidationCounter will equal 5.
    private int masterValidationCounter;
    // Master validation variable, only set to true once ALL user data is validated and account is being created
    private boolean masterValidation = false;

    private User createUser;
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

    private switchSceneController switchScene = new switchSceneController();
    // default connection variable for database
    private Connection connection;


    // signup controller constructor
    public SignUpController() {
        // initiate connect to database
        connection = DatabaseConnection.getInstance();
    }

    // public user signup function (linked to signup button)
    public void userSignup(ActionEvent event) throws IOException {
        // reset mastervalidationcounter to 0
        masterValidationCounter = 0;
        checkUserSignup(event);

    }

    // private user signup function
    private void checkUserSignup(ActionEvent event) throws IOException {
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
            createUser = new User(userFirstname, userLastname, userEmail, userMobile, password);
            // create new userDAO object
            userDAO = new DatabaseUserDAO();

            // call addUser method in databaseUserDAO and parse in the new createUser (object of new user info)
            userDAO.addUser(createUser);

            // clear input fields & switch to dashboard scene
            clearInputs(true);
            switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
        }
    }

    // Method to clear user data input

    private void clearInputs(boolean all) throws IOException {
        if(all) {
            // clear all inputs
            firstNameSignup.setText("");
            lastNameSignup.setText("");
            mobileSignup.setText("");
            passwordSignup.setText("");
            emailSignup.setText("");
        }

    }


    // Methods to validate user inputs
    //
    //

    // Master validation method

    // TO DO

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

    // method to handle login button function
    @FXML
    private void handleLoginRedirect(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");

    }
}
