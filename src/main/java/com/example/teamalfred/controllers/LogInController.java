package com.example.teamalfred.controllers;

import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.UserDAO;
import com.example.teamalfred.database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

//Main Controller - Josh
// DO NOT TOUCH

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LogInController {

    // initial variable declerations
    private UserDAO userDAO;
    public LogInController() {
        userDAO = new DatabaseUserDAO() {
            @Override
            public Optional<User> findUserById(int id) throws SQLException {
                return Optional.empty();
            }

            @Override
            public List<User> getAllUsers() throws SQLException {
                return List.of();
            }
        };
    }
    @FXML
    private Label welcomeText;
    @FXML
    private Button loginButton;
    @FXML
    private Label failedLogin;
    @FXML
    private TextField emailLogin;
    @FXML
    private PasswordField password;
    // initial switch scene object created
    private switchSceneController switchScene = new switchSceneController();

    // call button function
    public void userLogin(ActionEvent event) throws IOException {
        boolean login = checkLogin(event);
    }

    // Check Login method - Josh
    private boolean checkLogin(ActionEvent event) throws IOException {
        Main m = new Main();
        // get user inputs
        if (emailLogin.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
            // if either input empty, login failed, returns false/
            failedLogin.setText("All below fields are mandatory.");
        } else {
            // Search db for user email, if email not found, null is returned
            User user = userDAO.findUserByEmail(emailLogin.getText().toString());
            // if user email found
            if (user != null) {
                // check if input password is equal to password of email in database
                if (user.getPassword().equals(password.getText().toString())) {
                    failedLogin.setText("Success!");
                    switchScene.switchScene(event, "/com/example/teamalfred/Dashboard.fxml");
                    return true;
                }

            }
            // Either email not found or password didn't match email.

        }
        // login failed
        failedLogin.setText("Invalid Credentials");
        resetInputs();
        return false;
    }

    // method to reset input values

    private void resetInputs() throws IOException{
        // resets both input boxes in the event of a failed login
        emailLogin.setText("");
        password.setText("");

    }

    // function to handle signup button click action
    @FXML

    private void handleSignUp(ActionEvent event) {
        switchScene.switchScene(event,"/com/example/teamalfred/SignUp.fxml");

    }



}