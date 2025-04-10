package com.example.teamalfred.controllers;

import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.IUserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            System.out.println("Handling signup...");
            // Load the Signup.fxml file
            System.out.println(getClass().getResource("/com/example/teamalfred/LogIn.fxml"));

            FXMLLoader loaderb = new FXMLLoader(getClass().getResource("/com/example/teamalfred/SignUp.fxml"));
            Parent root = loaderb.load();

            // Create a new scene with the loaded FXML
            Scene scene = new Scene(root);

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }



}