package com.example.teamalfred.controllers;

import com.example.teamalfred.Main;
import com.example.teamalfred.database.DatabaseConnection;
import com.example.teamalfred.database.DatabaseUserDAO;
import com.example.teamalfred.database.IUserDAO;
import com.example.teamalfred.database.User;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class SignUpController {
    private IUserDAO userDAO;

    private User createUser;
    @FXML
    private TextField firstNameSignup;
    @FXML
    private TextField lastNameSignup;
    @FXML
    private TextField mobileSignup;
    @FXML
    private TextField emailSignup;
    @FXML
    private PasswordField passwordSignup;
    @FXML
    private PasswordField passwordSignupConfirm;
    @FXML
    private TextField username;
    @FXML
    private Label invalidFirstname;
    @FXML
    private Label invalidLastname;
    @FXML
    private Label invalidMobile;
    @FXML
    private Label invalidEmail;
    @FXML
    private Label invalidPassword;


    private Connection connection;



    public SignUpController() {
        connection = DatabaseConnection.getInstance();
        System.out.println("Signup controller...");
    }

    public void userSignup(ActionEvent event) throws IOException {
        checkUserSignup();

    }

    private void checkUserSignup() throws IOException {
        Main m = new Main();
        String userFirstname = validateFirstname();
        String userLastname = validateLastname();
        String userMobile = validateMobile();
        String userEmail = emailSignup.getText().toString();
        String password = passwordSignup.getText().toString();
        createUser = new User(userFirstname, userLastname, userEmail, userMobile, password);
        createUser.printUserInfo();
        userDAO = new DatabaseUserDAO();
        userDAO.addUser(createUser);

    }


    // Methods to validate user inputs
    //
    //

    // validate firstname input
    private String validateFirstname() throws IOException {
        // Check if first name input has only letters in it.
        if(firstNameSignup.getText().toString().matches("[a-zA-Z]*")) {
            // if true, name input is valid...
            return firstNameSignup.getText().toString();
        } else {
            // invalid input
            setInvalidLabel(invalidFirstname, true);
            return "Invalid";
        }
     }

     // Validate lastname input
    private String validateLastname() throws IOException {
        // Check if first name input has only letters in it.
        if(lastNameSignup.getText().toString().matches("[a-zA-Z]*")) {
            // if true, name input is valid...
            return lastNameSignup.getText().toString();
        } else {
            // invalid input
            setInvalidLabel(invalidLastname, true);
            return "Invalid";
        }
    }

    // valid mobile input

    private String validateMobile() throws IOException {
        if(mobileSignup.getText().toString().matches("\\d*")) {
            // only digits in entry
            if(mobileSignup.getText().toString().length() == 10) {
                // valid mobile
                return mobileSignup.getText().toString();
            }
        }
        setInvalidLabel(invalidMobile, true);
        return "invalid";
    }

    // valid email input

    // TO DO

    // valid password length

    // TO DO


     // method to update label if user input fails validation
     private void setInvalidLabel(Label label, boolean set) throws IOException {
        if(set) {
            label.setText("Invalid*");
        } else {
            label.setText("");
        }
     }



    @FXML
    private void handleLoginRedirect(ActionEvent event) {
        try {
            // Load the Signup.fxml file

            FXMLLoader loaderc = new FXMLLoader(getClass().getResource("/com/example/teamalfred/LogIn.fxml"));
            Parent root = loaderc.load();

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
