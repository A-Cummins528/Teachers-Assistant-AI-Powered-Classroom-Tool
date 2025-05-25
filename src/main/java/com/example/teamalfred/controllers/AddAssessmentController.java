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

public class AddAssessmentController {

    @FXML private TextField titleField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> subjectComboBox;

    private final SqliteAssessmentDAO dao;
    private Student selectedStudent;

    {
        try {
            dao = new SqliteAssessmentDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("Report", "Exam", "Quiz"));
        subjectComboBox.setItems(FXCollections.observableArrayList(
                "English", "Maths", "Science", "History", "Geography", "Health", "Art", "Technology", "PE"
        ));
    }

    @FXML
    private void handleSubmit() {
        String title = titleField.getText().trim();
        String type = typeComboBox.getValue();
        String subject = subjectComboBox.getValue();
        LocalDate due = dueDatePicker.getValue();

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
            // Determine status based on due date
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

            // Format date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDueDate = due.format(formatter);

            // Create and insert assessment
            Assessment newAssessment = new Assessment(title, subject, formattedDueDate, status, type);
            newAssessment.setStudentId(selectedStudent.getId());
            dao.insertAssessment(newAssessment);

            // Close the popup
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    public void setStudent(Student student) {
        this.selectedStudent = student;
    }
}
