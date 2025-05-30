package com.example.teamalfred.controllers;

import com.example.teamalfred.database.SqliteAssessmentDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Controller for adding a new assessment to an entire subject.
 * Allows user to specify assessment details and insert it into the database.
 */
public class AddAssessmentToSubjectController {

    // UI fields for assessment input
    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> subjectComboBox;
    @FXML private DatePicker dueDatePicker;

    // DAO for database operations
    private final SqliteAssessmentDAO dao;

    /**
     * Constructor that initializes the assessment DAO and handles SQL exceptions.
     * @throws SQLException if database connection fails
     */
    public AddAssessmentToSubjectController() throws SQLException {
        dao = new SqliteAssessmentDAO();
    }

    /**
     * Initializes combo box values for type and subject.
     */
    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("Report", "Exam", "Quiz"));
        subjectComboBox.setItems(FXCollections.observableArrayList(
                "English", "Maths", "Science", "History", "Geography", "Health", "Art", "Technology", "PE"
        ));
    }

    /**
     * Handles the submit button action. Validates input, calculates assessment status,
     * formats the date, and attempts to insert the assessment into the database.
     */
    @FXML
    private void handleSubmit() {
        String title = titleField.getText().trim();
        String type = typeComboBox.getValue();
        String subject = subjectComboBox.getValue();
        LocalDate due = dueDatePicker.getValue();

        // Validate input fields
        if (title.isEmpty() || type == null || subject == null || due == null) {
            showAlert("Please fill in all fields.");
            return;
        }

        // Determine status based on due date
        String status;
        LocalDate today = LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(due, today);

        if (today.isBefore(due)) status = "Due";
        else if (daysLate <= 2) status = "Overdue";
        else status = "Closed";

        // Format due date for storage
        String formattedDate = due.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try {
            // Insert the assessment into the database
            dao.insertAssessmentForSubject(title, subject, formattedDate, status, type);

            // Close the window after successful insertion
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
        }
    }

    /**
     * Displays an alert dialog with a specified message.
     * @param msg the message to display
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
}
