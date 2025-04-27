package com.example.teamalfred.controllers;


import com.example.teamalfred.database.DatabaseConnection;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


// Controller for dashboard.fxml scene
public class DashboardController {
    private Label welcomeMessageLabel;
    public DashboardController() throws IOException {
    }
    private SwitchSceneController switchScene = new SwitchSceneController();
    private Connection connection;

    public DashboardController(Connection connection) throws SQLException {
        this.connection = DatabaseConnection.getInstance();
    }

}
