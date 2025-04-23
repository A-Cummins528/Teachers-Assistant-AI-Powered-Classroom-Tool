package com.example.teamalfred;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class HelloApplication extends Application {

    private static Stage stg;
    @Override
    // Start method to begin database and login scene
    public void start(Stage stage) throws IOException {
        // create initial scene
        stg = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LogIn.fxml"));
        // default scene (login page)
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }



    // main function for program entry point.
    public static void main(String[] args) {
        launch();
    }
}