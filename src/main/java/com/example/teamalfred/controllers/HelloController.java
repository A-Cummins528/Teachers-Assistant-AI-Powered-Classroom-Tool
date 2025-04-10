package com.example.teamalfred.controllers;

import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.IUserDAO;
import com.example.teamalfred.database.User;
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

//Main Controller - Josh
// DO NOT TOUCH

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
    private TextField emailLogin;
    @FXML
    private PasswordField password;

    public void userLogin(ActionEvent event) throws IOException {
        boolean login = checkLogin();
    }

    // Check Login method - Josh
    private boolean checkLogin() throws IOException {
        Main m = new Main();
        // get user inputs
        if (emailLogin.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
            // if either input empty, login failed, returns false/
            failedLogin.setText("All below fields are mandatory.");
        } else {
            // Search db for user email, if email not found, null is returned
            User user = userDAO.getUser(emailLogin.getText().toString());
            // if user email found
            if (user != null) {
                // check if input password is equal to password of email in database
                if (user.getPassword().equals(password.getText().toString())) {
                    failedLogin.setText("Success!");
                    return true;
                }
            } else {
                System.out.println("User not found :(");
               // System.out.println(user.getEmail());
               // System.out.println(user.getPassword());
            }
            // Either email not found or password didn't match email.

        }
        failedLogin.setText("Invalid Credentials");
        resetInputs();
        return false;
    }

    // method to reset input values

    private void resetInputs() throws IOException{
        // resets both input boxes in the event of a failed login
        emailLogin.setText("");
        password.setText("");

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