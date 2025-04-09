package com.example.teamalfred.controller;

import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.IUserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    private IUserDAO userDAO;
    public HelloController() {
        userDAO = new DatabaseUserDAO();
    }
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void userLogin() {
        // Todo: Code to handle the login button press
        System.out.println("Login button clicked!");
    }

}