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

public class AddAssessmentToSubjectController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> subjectComboBox;
    @FXML private DatePicker dueDatePicker;

    private final SqliteAssessmentDAO dao;

    public AddAssessmentToSubjectController() throws SQLException {
        dao = new SqliteAssessmentDAO();
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

        if (title.isEmpty() || type == null || subject == null || due == null) {
            showAlert("Please fill in all fields.");
            return;
        }

        String status;
        LocalDate today = LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(due, today);

        if (today.isBefore(due)) status = "Due";
        else if (daysLate <= 2) status = "Overdue";
        else status = "Closed";

        String formattedDate = due.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try {
            dao.insertAssessmentForSubject(title, subject, formattedDate, status, type);

            // Close the window
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
}
