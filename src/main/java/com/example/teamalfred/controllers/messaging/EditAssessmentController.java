package com.example.teamalfred.controllers;

import com.example.teamalfred.database.Assessment;
import com.example.teamalfred.database.SqliteAssessmentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class EditAssessmentController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> subjectComboBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> typeComboBox;

    private Assessment assessment;
    private SqliteAssessmentDAO dao;

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;

        // Pre-fill form
        titleField.setText(assessment.getTitle());
        subjectComboBox.setValue(assessment.getSubject());
        dueDatePicker.setValue(LocalDate.parse(assessment.getDueDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        typeComboBox.setValue(assessment.getType());
    }

    @FXML
    public void initialize() {
        subjectComboBox.getItems().addAll("English", "Maths", "Science", "History", "Geography", "Health", "Art", "Technology", "PE");
        typeComboBox.getItems().addAll("Report", "Exam", "Quiz");

        try {
            dao = new SqliteAssessmentDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        String title = titleField.getText().trim();
        String subject = subjectComboBox.getValue();
        LocalDate due = dueDatePicker.getValue();
        String type = typeComboBox.getValue();

        if (title.isEmpty() || subject == null || due == null || type == null) {
            showAlert("Please fill in all fields.");
            return;
        }

        String formattedDue = due.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Update the model
        assessment.setTitle(title);
        assessment.setSubject(subject);
        assessment.setDueDate(formattedDue);
        assessment.setType(type);

        try {
            dao.updateAssessment(assessment);
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to update assessment.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
}
