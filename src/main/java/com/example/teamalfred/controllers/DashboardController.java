package com.example.teamalfred.controllers;

import com.example.teamalfred.database.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label displayUserName;

    private User user; // Store the user if needed later

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            displayUserName.setText("Hey there, " + user.getFirstName());
        }
    }

    @FXML
    private void initialize() {
        // Any additional initialization here
    }
}