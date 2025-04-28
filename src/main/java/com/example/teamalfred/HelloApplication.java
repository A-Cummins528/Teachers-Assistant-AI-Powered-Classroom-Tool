package com.example.teamalfred;

import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.database.DatabaseSchemaManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {

    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            // 1. Establish DB connection
            DatabaseConnection.getInstance();

            // 2. Initialize schema (create table if missing)
            DatabaseSchemaManager schemaManager = new DatabaseSchemaManager();
            schemaManager.initializeSchema();

            // 3. Load login scene
            stg = stage;
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LogIn.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            // Handle DB errors
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            // Optional: Show an error dialog and exit the app
        }
    }
}
