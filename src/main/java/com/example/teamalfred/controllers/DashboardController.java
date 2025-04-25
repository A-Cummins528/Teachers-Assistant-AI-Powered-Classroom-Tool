package com.example.teamalfred.controllers;


import com.example.teamalfred.database.DatabaseConnection;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;


// Controller for dashboard.fxml scene
public class DashboardController {
    private Label welcomeMessageLabel;
    public DashboardController() throws IOException {
    }
    private switchSceneController switchScene = new switchSceneController();
    private Connection connection;

    public DashboardController(Connection connection) {
        this.connection = DatabaseConnection.getInstance();
    }

}
