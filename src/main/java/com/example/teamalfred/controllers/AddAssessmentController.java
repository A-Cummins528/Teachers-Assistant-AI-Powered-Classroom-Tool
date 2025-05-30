package com.example.teamalfred.controllers;

import com.example.teamalfred.database.Assessment;
import com.example.teamalfred.database.SqliteAssessmentDAO;
import com.example.teamalfred.database.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Controller for adding a new assessment to a specific student.
 * Handles user input, form validation, and database insertion.
 */
public class AddAssessmentController {

    // UI components for input fields
    @FXML private TextField titleField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> subjectComboBox;

    // DAO to interact with the assessment database
    private final SqliteAssessmentDAO dao;

    // The student to whom the new assessment will be assigned
    private Student selectedStudent;

    // Static block to handle DAO instantiation with exception
    {
        try {
            dao = new SqliteAssessmentDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes combo boxes with predefined values for type and subject.
     */
    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("Report", "Exam", "Quiz"));
        subjectComboBox.setItems(FXCollections.observableArrayList(
                "English", "Maths", "Science", "History", "Geography", "Health", "Art", "Technology", "PE"
        ));
    }

    /**
     * Handles the submission of the assessment form. Validates inputs,
     * calculates status, and inserts the assessment into the database.
     */
    @FXML
    private void handleSubmit() {
        String title = titleField.getText().trim();
        String type = typeComboBox.getValue();
        String subject = subjectComboBox.getValue();
        LocalDate due = dueDatePicker.getValue();

        // Validate the student and all fields
        if (selectedStudent == null) {
            showAlert("No student selected for this assessment.");
            return;
        }
        if (title.isEmpty()) {
            showAlert("Please enter a title.");
            return;
        }
        if (subject == null) {
            showAlert("Please select a subject.");
            return;
        }
        if (type == null) {
            showAlert("Please select a type.");
            return;
        }
        if (due == null) {
            showAlert("Please select a due date.");
            return;
        }

        try {
            // Determine the assessment status based on the due date
            LocalDate today = LocalDate.now();
            long daysLate = ChronoUnit.DAYS.between(due, today);
            String status;

            if (today.isBefore(due)) {
                status = "Due";
            } else if (daysLate <= 2) {
                status = "Overdue";
            } else {
                status = "Closed";
            }

            // Format the due date as a string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDueDate = due.format(formatter);

            // Create assessment object and insert into DB
            Assessment newAssessment = new Assessment(title, subject, formattedDueDate, status, type);
            newAssessment.setStudentId(selectedStudent.getId());
            dao.insertAssessment(newAssessment);

            // Close the popup window after successful submission
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
        }
    }

    /**
     * Displays an alert dialog with a specific message.
     * @param msg Message to display in the alert
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    /**
     * Sets the student who will be assigned the new assessment.
     * @param student the student object
     */
    public void setStudent(Student student) {
        this.selectedStudent = student;
    }
}
