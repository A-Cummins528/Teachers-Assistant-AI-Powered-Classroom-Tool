package com.example.teamalfred.controllers;

import com.example.teamalfred.database.UserDAO;
import com.example.teamalfred.database.SqliteUserDAO;
import com.example.teamalfred.database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for the user sign-up screen.
 * Handles user input, validation, and new user registration.
 */
public class SignUpController {

    private final UserDAO userDAO = new SqliteUserDAO();
    private int masterValidationCounter;
    private final SwitchSceneController switchScene = new SwitchSceneController();

    @FXML private TextField firstNameSignup;
    @FXML private TextField lastNameSignup;
    @FXML private TextField mobileSignup;
    @FXML private TextField emailSignup;
    @FXML private PasswordField passwordSignup;
    @FXML private PasswordField passwordSignupConfirm;
    @FXML private ComboBox<String> userTypeSignup;

    // Validation Labels
    @FXML private Label invalidFirstname;
    @FXML private Label invalidLastname;
    @FXML private Label invalidMobile;
    @FXML private Label invalidEmail;
    @FXML private Label invalidPassword;
    @FXML private Label invalidPasswordConfirm;
    @FXML private BorderPane signUpRoot;

    private double currentFontSize = 14.0;

    // New Validation Labels
    @FXML private Label invalidUserType;
    @FXML private Label generalMessageLabel;

    private static final int REQUIRED_VALIDATIONS_COUNT = 6; // firstName, lastName, mobile, email, userType, password

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     * Populates the user type ComboBox.
     */
    @FXML
    public void initialize() {
        if (userTypeSignup != null) {
            userTypeSignup.getItems().addAll("Student", "Teacher");
        }
    }

    /**
     * Handles the user sign-up process when the sign-up button is clicked.
     * Validates input fields and attempts to create a new user.
     * Displays error messages for validation failures or database errors.
     * @param event The action event triggered by clicking the sign-up button.
     */
    @FXML
    public void userSignup(ActionEvent event) {
        System.out.println("Signing up!");
        masterValidationCounter = 0;
        clearAllValidationLabels();

        try {
            checkUserSignup(event);
        } catch (SQLException e) {
            System.err.println("SQL Error during sign-up process: " + e.getMessage());
            e.printStackTrace();

            if (generalMessageLabel != null) {
                generalMessageLabel.setText("Sign up failed. Database error.");
            }

            if (e.getMessage().toLowerCase().contains("unique constraint failed: users.email") ||
                    e.getMessage().toLowerCase().contains("a user with this email already exists")) {
                setInvalidLabel(invalidEmail, true, "Email already registered.");
            } else if (e.getMessage().toLowerCase().contains("check constraint failed")) {
                setInvalidLabel(invalidUserType, true, "Invalid user type. Must be Student or Teacher.");
            } else {
                if (generalMessageLabel != null) {
                    generalMessageLabel.setText("An unexpected error occurred.");
                }
                System.out.println("An unexpected database error occurred.");
            }
        } catch (IOException e) {
            System.err.println("IO Error during scene switch: " + e.getMessage());
            if (generalMessageLabel != null) {
                generalMessageLabel.setText("Error loading next page.");
            }
        }
    }

    /**
     * Performs validation of all user input fields for signup.
     * If all validations pass, creates a new user and switches to the login scene.
     * @param event The action event, used for scene switching on success.
     * @throws SQLException If a database access error occurs during user creation.
     * @throws IOException If an error occurs while switching scenes.
     */
    private void checkUserSignup(ActionEvent event) throws SQLException, IOException {
        String userFirstname = validateFirstname();
        String userLastname = validateLastname();
        String userMobile = validateMobile();
        String userEmail = validateEmail();
        String password = validatePassword();
        String userTypeString = validateUserType();

        System.out.println("Validation Counter: " + masterValidationCounter);

        if (masterValidationCounter == REQUIRED_VALIDATIONS_COUNT && !"Invalid".equals(userTypeString)) {
            System.out.println("Mastervalidation TRUE");

            User newUser = new User(userFirstname, userLastname, userEmail, userMobile, password, userTypeString);

            userDAO.createUser(newUser);
            System.out.println("User created: " + newUser.getEmail());
            clearInputs();
            switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");

        } else {
            System.out.println("Mastervalidation FALSE. Counter: " + masterValidationCounter + ", UserType: " + userTypeString);
        }
    }

    /**
     * Clears all validation error messages from the UI.
     */
    private void clearAllValidationLabels() {
        setInvalidLabel(invalidFirstname, false, "");
        setInvalidLabel(invalidLastname, false, "");
        setInvalidLabel(invalidMobile, false, "");
        setInvalidLabel(invalidEmail, false, "");
        setInvalidLabel(invalidPassword, false, "");
        setInvalidLabel(invalidPasswordConfirm, false, "");
        setInvalidLabel(invalidUserType, false, "");
    }

    /**
     * Clears all input fields and validation labels on the sign-up form.
     */
    private void clearInputs() {
        firstNameSignup.clear();
        lastNameSignup.clear();
        mobileSignup.clear();
        emailSignup.clear();
        passwordSignup.clear();
        passwordSignupConfirm.clear();

        if (userTypeSignup != null) userTypeSignup.getSelectionModel().clearSelection();

        clearAllValidationLabels();
    }

    /**
     * Validates the first name input field.
     * Updates validation counter and displays an error if invalid.
     * @return The valid first name, or "Invalid" if validation fails.
     */
    private String validateFirstname() {
        String fname = firstNameSignup.getText().trim();
        if (fname.matches("[a-zA-Z\\s'-]{2,}")) {
            masterValidationCounter++;
            return fname;
        }
        setInvalidLabel(invalidFirstname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    /**
     * Validates the last name input field.
     * Updates validation counter and displays an error if invalid.
     * @return The valid last name, or "Invalid" if validation fails.
     */
    private String validateLastname() {
        String lname = lastNameSignup.getText().trim();
        if (lname.matches("[a-zA-Z\\s'-]{2,}")) {
            masterValidationCounter++;
            return lname;
        }
        setInvalidLabel(invalidLastname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    /**
     * Validates the mobile number input field against an Australian mobile format.
     * Updates validation counter and displays an error if invalid.
     * @return The valid mobile number, or "Invalid" if validation fails.
     */
    private String validateMobile() {
        String mobile = mobileSignup.getText().replaceAll("\\s+", "");
        if (mobile.matches("^04\\d{8}$")) { // Example for Australian mobiles
            masterValidationCounter++;
            return mobile;
        }
        setInvalidLabel(invalidMobile, true, "Australian mobile: 04XXXXXXXX");
        return "Invalid";
    }

    /**
     * Validates the email input field.
     * Updates validation counter and displays an error if invalid.
     * @return The valid email address (lowercased), or "Invalid" if validation fails.
     */
    private String validateEmail() {
        String email = emailSignup.getText().trim().toLowerCase();
        if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            masterValidationCounter++;
            return email;
        }
        setInvalidLabel(invalidEmail, true, "Invalid email format.");
        return "Invalid";
    }

    /**
     * Validates the password and password confirmation fields.
     * Checks for minimum length and ensures passwords match.
     * Updates validation counter (once for length, once for match) and displays errors if invalid.
     * @return The valid password, or "Invalid" if validation fails.
     */
    private String validatePassword() {
        String pass = passwordSignup.getText();
        String confirmPass = passwordSignupConfirm.getText();

        if (pass.length() < 7) {
            setInvalidLabel(invalidPassword, true, "Password min 7 characters.");
            return "Invalid";
        }
        setInvalidLabel(invalidPassword, false, "");

        if (!pass.equals(confirmPass)) {
            setInvalidLabel(invalidPasswordConfirm, true, "Passwords do not match.");
            passwordSignupConfirm.clear();
            return "Invalid";
        }
        setInvalidLabel(invalidPasswordConfirm, false, "");

        masterValidationCounter++; // For password length
        masterValidationCounter++; // For password confirmation match
        return pass;
    }

    /**
     * Validates the user type selection from the ComboBox.
     * Displays an error if no type is selected.
     * @return The selected user type ("Student" or "Teacher"), or "Invalid" if not selected.
     */
    private String validateUserType() {
        String selectedType = userTypeSignup.getValue();
        if (selectedType != null && !selectedType.isEmpty()) {
            return selectedType;
        }
        setInvalidLabel(invalidUserType, true, "Please select a user type.");
        return "Invalid";
    }

    /**
     * Helper method to set the text and visibility of a validation Label.
     * @param label The Label to update.
     * @param isInvalid True if an error message should be displayed, false to clear.
     * @param message The error message to display if isInvalid is true.
     */
    private void setInvalidLabel(Label label, boolean isInvalid, String message) {
        if (label != null) {
            label.setText(isInvalid ? message : "");
            label.setVisible(isInvalid);
        }
    }

    /**
     * Handles the action of redirecting the user to the login screen.
     * @param event The action event triggered by a UI element (e.g., a button).
     */
    @FXML
    private void handleLoginRedirect(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }
}