package com.example.teamalfred.controllers;

import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.IUserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class HelloController {

    private IUserDAO userDAO;
    public HelloController() {
        userDAO = new DatabaseUserDAO();
    }
    @FXML
    private Label welcomeText;

    @FXML
    private Button loginButton;
    @FXML
    private Label failedLogin;
    @FXML
    private TextField usernameLogin;
    @FXML
    private PasswordField password;

    public void userLogin(ActionEvent event) throws IOException {
        checkLogin();
    }

    private void checkLogin() throws IOException {
        Main m = new Main();
        if(usernameLogin.getText().toString().equals("javacoding") && password.getText().toString().equals("123")) {
            failedLogin.setText("Success!");

        }
        else if(usernameLogin.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
            failedLogin.setText("All below fields are mandatory.");
        } else {
            failedLogin.setText("Invalid credentials.");
        }
    }



}