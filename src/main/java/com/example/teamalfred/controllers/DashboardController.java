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
