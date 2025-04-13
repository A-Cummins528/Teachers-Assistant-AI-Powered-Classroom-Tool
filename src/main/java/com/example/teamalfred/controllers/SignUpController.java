package com.example.teamalfred.controllers;

import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.IUserDAO;
import com.example.teamalfred.database.User;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;


// SIGN UP PAGE CONTROLLER ## DO NOT TOUCH - JOSH

public class SignUpController {

    // initial values from input elements in fxml scene

    // userDAO for database entry
    private IUserDAO userDAO;

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
        checkUserSignup(event);

    }

    // private user signup function
    private void checkUserSignup(ActionEvent event) throws IOException {
        Main m = new Main();

        // initial variables for new user info, all run through input validation methods
        String userFirstname = validateFirstname();
        String userLastname = validateLastname();
        //String userMobile = validateMobile();
        String userMobile = "123";
        String userEmail = emailSignup.getText().toString();
        String password = passwordSignup.getText().toString();

        // create new user object with user info
        createUser = new User(userFirstname, userLastname, userEmail, userMobile, password);
        // create new userDAO object
        userDAO = new DatabaseUserDAO();

        // call addUser method in databaseUserDAO and parse in the new createUser (object of new user info)
        userDAO.addUser(createUser);

        // clear input fields
        clearInputs(true);
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");


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

    // validate firstname input
    private String validateFirstname() throws IOException {
        // Check if first name input has only letters in it.
        if(firstNameSignup.getText().toString().matches("[a-zA-Z]*")) {
            // if true, name input is valid...
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
            return lastNameSignup.getText().toString();
        } else {
            // invalid input
            setInvalidLabel(invalidLastname, true);
            return "Invalid";
        }
    }

    // valid mobile input

    private String validateMobile() throws IOException {
        if(mobileSignup.getText().toString().matches("\\d*")) {
            // only digits in entry
            if(mobileSignup.getText().toString().length() == 10) {
                // valid mobile
                return mobileSignup.getText().toString();
            }
        }
        setInvalidLabel(invalidMobile, true);
        return "invalid";
    }

    // valid email input

    // TO DO

    // valid password length

    // TO DO


     // method to update label if user input fails validation
     private void setInvalidLabel(Label label, boolean set) throws IOException {
        if(set) {
            label.setText("Invalid*");
        } else {
            label.setText("");
        }
     }


    @FXML
    private void handleLoginRedirect(ActionEvent event) {

        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");

    }
//        try {
//            // Load the LogIn.fxml file in the background
//            FXMLLoader loaderc = new FXMLLoader(getClass().getResource("/com/example/teamalfred/LogIn.fxml"));
//            Parent root = loaderc.load();
//
//            // Create a new scene with the loaded FXML
//            Scene scene = new Scene(root);
//
//            // Get the current stage
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//            // Apply fade-out effect for the current scene
//            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
//            fadeOut.setFromValue(1.0);
//            fadeOut.setToValue(0.0);
//
//            fadeOut.setOnFinished(e -> {
//                // Switch the scene after the fade-out is complete
//                stage.setScene(scene);
//
//                // Apply fade-in effect for the new scene
//                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), scene.getRoot());
//                fadeIn.setFromValue(0.0);
//                fadeIn.setToValue(1.0);
//                fadeIn.play();
//            });
//
//            fadeOut.play();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle the exception appropriately
//        }
//    }
}
