package com.example.teamalfred.controllers;


import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


// Controller for dashboard.fxml scene
public class DashboardController {
    private Label welcomeMessageLabel;
    public DashboardController() throws IOException {
        System.out.println("Dashboard started");
        welcomeMessageLabel.setText("");

    }
}
