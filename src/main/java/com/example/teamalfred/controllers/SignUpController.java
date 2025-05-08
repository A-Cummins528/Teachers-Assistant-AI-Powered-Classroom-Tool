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

import java.io.IOException;
import java.sql.SQLException;

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
    // @FXML private TextField classNameSignup; Removing for now, dont want user to be able to register for a class in signup

    // Validation Labels
    @FXML private Label invalidFirstname;
    @FXML private Label invalidLastname;
    @FXML private Label invalidMobile;
    @FXML private Label invalidEmail;
    @FXML private Label invalidPassword;
    @FXML private Label invalidPasswordConfirm;

    // New Validation Labels
    @FXML private Label invalidUserType;
    // @FXML private Label invalidClassName;
    @FXML private Label generalMessageLabel;

    // Number of core mandatory fields for validation to pass before attempting createUser
    // Adjust this if userType becomes strictly mandatory for the counter
    private static final int REQUIRED_VALIDATIONS_COUNT = 6; // firstName, lastName, mobile, email, userType, password


    @FXML
    public void initialize() {
        // Populate User Type ComboBox
        if (userTypeSignup != null) {
            userTypeSignup.getItems().addAll("Student", "Teacher");
        }
    }

    @FXML
    public void userSignup(ActionEvent event) { // Removed throws, handle SQLException locally
        System.out.println("Signing up!");
        masterValidationCounter = 0;
        clearAllValidationLabels(); // Clear previous errors

        try {
            checkUserSignup(event);
        } catch (SQLException e) {
            System.err.println("SQL Error during sign-up process: " + e.getMessage());
            e.printStackTrace(); // Important for debugging

            // Safe way to set text on label that might be null
            if (generalMessageLabel != null) {
                generalMessageLabel.setText("Sign up failed. Database error.");
            }

            if (e.getMessage().toLowerCase().contains("unique constraint failed: users.email") ||
                    e.getMessage().toLowerCase().contains("a user with this email already exists")) {
                setInvalidLabel(invalidEmail, true, "Email already registered.");
            } else if (e.getMessage().toLowerCase().contains("check constraint failed")) {
                // Specific handling for the userType constraint issue
                setInvalidLabel(invalidUserType, true, "Invalid user type. Must be Student or Teacher.");
            } else {
                // A more generic error for other SQL issues
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

    private void checkUserSignup(ActionEvent event) throws SQLException, IOException {
        String userFirstname = validateFirstname();
        String userLastname = validateLastname();
        String userMobile = validateMobile();
        String userEmail = validateEmail();
        String password = validatePassword(); // This now also checks password confirmation
        // String passwordConfirm = validatePasswordConfirm(); // Incorporated into validatePassword

        // Validate and get new fields
        String userTypeString = validateUserType(); // Returns "Student", "Teacher", or "Invalid"

        // Commenting out, as I don't want users to assign themselves a grade when they create an account
//        String grade = null;
//        if ("Student".equalsIgnoreCase(userTypeString)) {
//            grade = validateGrade(); // Only validate and get grade if user type is Student
//            // validateGrade should handle if it's "InvalidGrade" or actual value/null
//        } else {
//            gradeSignup.clear(); // Ensure grade is clear if not student
//        }

        // String className = validateClassName(); // Can be null (optional) or "InvalidClass"

        System.out.println("Validation Counter: " + masterValidationCounter);
        // Master validation passes if all core fields are valid.
        // UserType is crucial, so it must not be "Invalid".
        // ClassName is optional.
        if (masterValidationCounter == REQUIRED_VALIDATIONS_COUNT && !"Invalid".equals(userTypeString)) {

//            // Additional check: if className has specific validation that failed
//            if ("InvalidClass".equals(className)) {
//                System.out.println("Mastervalidation FALSE due to invalid class name.");
//                return; // Stop processing
//            }

            System.out.println("Mastervalidation TRUE");

            // Create User object
            User newUser = new User(userFirstname, userLastname, userEmail, userMobile, password,
                    userTypeString);

            userDAO.createUser(newUser); // This can throw SQLException (e.g., email unique constraint)
            System.out.println("User created: " + newUser.getEmail());
            clearInputs();
            switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");

        } else {
            System.out.println("Mastervalidation FALSE. Counter: " + masterValidationCounter + ", UserType: " + userTypeString);
        }
    }

    private void clearAllValidationLabels() {
        setInvalidLabel(invalidFirstname, false, "");
        setInvalidLabel(invalidLastname, false, "");
        setInvalidLabel(invalidMobile, false, "");
        setInvalidLabel(invalidEmail, false, "");
        setInvalidLabel(invalidPassword, false, "");
        setInvalidLabel(invalidPasswordConfirm, false, "");
        setInvalidLabel(invalidUserType, false, "");
        // setInvalidLabel(invalidGrade, false, "");
        // setInvalidLabel(invalidClassName, false, "");
    }

    private void clearInputs() {
        firstNameSignup.clear();
        lastNameSignup.clear();
        mobileSignup.clear();
        emailSignup.clear();
        passwordSignup.clear();
        passwordSignupConfirm.clear(); // Clear confirm password

        if (userTypeSignup != null) userTypeSignup.getSelectionModel().clearSelection();
        // if (gradeSignup != null) gradeSignup.clear();
        // if (classNameSignup != null) classNameSignup.clear();

        clearAllValidationLabels(); // Also clear labels on successful submission
    }

    // --- Validation Methods ---
    private String validateFirstname() {
        String fname = firstNameSignup.getText().trim();
        if (fname.matches("[a-zA-Z\\s'-]{2,}")) { // Min 2 chars, allows letters, space, hyphen, apostrophe
            masterValidationCounter++;
            return fname;
        }
        setInvalidLabel(invalidFirstname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    private String validateLastname() {
        String lname = lastNameSignup.getText().trim();
        if (lname.matches("[a-zA-Z\\s'-]{2,}")) { // Min 2 chars
            masterValidationCounter++;
            return lname;
        }
        setInvalidLabel(invalidLastname, true, "Min 2 letters. Chars: a-z ' -");
        return "Invalid";
    }

    private String validateMobile() {
        // Example for Australian mobiles: 04 followed by 8 digits
        String mobile = mobileSignup.getText().replaceAll("\\s+", "");
        if (mobile.matches("^04\\d{8}$")) {
            masterValidationCounter++;
            return mobile;
        }
        setInvalidLabel(invalidMobile, true, "Australian mobile: 04XXXXXXXX");
        return "Invalid";
    }

    private String validateEmail() {
        String email = emailSignup.getText().trim().toLowerCase();
        if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            masterValidationCounter++;
            return email;
        }
        setInvalidLabel(invalidEmail, true, "Invalid email format.");
        return "Invalid";
    }

    private String validatePassword() {
        String pass = passwordSignup.getText();
        String confirmPass = passwordSignupConfirm.getText();

        if (pass.length() < 7) {
            setInvalidLabel(invalidPassword, true, "Password min 7 characters.");
            return "Invalid";
        }
        // Clear specific password error if length is okay before checking confirmation
        setInvalidLabel(invalidPassword, false, "");


        if (!pass.equals(confirmPass)) {
            setInvalidLabel(invalidPasswordConfirm, true, "Passwords do not match.");
            passwordSignupConfirm.clear(); // Clear only the confirm field
            return "Invalid";
        }
        // Clear confirm password error if they match
        setInvalidLabel(invalidPasswordConfirm, false, "");

        masterValidationCounter++; // For password length
        masterValidationCounter++; // For password confirmation match
        return pass;
    }


    // --- New Validation Methods ---
    private String validateUserType() {
        String selectedType = userTypeSignup.getValue();
        if (selectedType != null && !selectedType.isEmpty()) {
            // This field is crucial but might not increment masterValidationCounter
            // if other fields are the primary gatekeepers.
            // If it should be part of the counter, uncomment:
            // masterValidationCounter++;
            return selectedType; // "Student" or "Teacher"
        }
        setInvalidLabel(invalidUserType, true, "Please select a user type.");
        return "Invalid";
    }

//    private String validateGrade() {
//        // Grade is only relevant for students.
//        // It can be optional for students, or you can make it mandatory.
//        // For now, let's say it's optional but if provided, has some format.
//        String grade = gradeSignup.getText().trim();
//        if (grade.isEmpty()) {
//            return null; // Or an empty string if your User model expects that for optional fields
//        }
//        // Example: Allows alphanumeric, spaces, hyphens. Adjust as needed. Max 10 chars.
//        if (grade.matches("^[a-zA-Z0-9\\s\\-]{1,10}$")) {
//            return grade;
//        }
//        setInvalidLabel(invalidGrade, true, "Invalid grade format (e.g., 10A, Year 12). Max 10 chars.");
//        return "InvalidGrade"; // Special string to indicate invalid input vs. empty
//    }
//
//    private String validateClassName() {
//        // Class name can also be optional or have specific formats.
//        // Example: CAB302, Programming 101. Max 20 chars.
//        String cName = classNameSignup.getText().trim();
//        if (cName.isEmpty()) {
//            return null; // Optional
//        }
//        if (cName.matches("^[a-zA-Z0-9\\s\\-()]{3,20}$")) { // Min 3, Max 20 chars
//            return cName;
//        }
//        setInvalidLabel(invalidClassName, true, "Invalid class name (e.g. CAB302). 3-20 chars.");
//        return "InvalidClass"; // Special string for invalid input
//    }

    // Helper to set validation labels
    private void setInvalidLabel(Label label, boolean isInvalid, String message) {
        if (label != null) {
            label.setText(isInvalid ? message : "");
            label.setVisible(isInvalid);
        }
    }

    @FXML
    private void handleLoginRedirect(ActionEvent event) {
        switchScene.switchScene(event, "/com/example/teamalfred/LogIn.fxml");
    }
}