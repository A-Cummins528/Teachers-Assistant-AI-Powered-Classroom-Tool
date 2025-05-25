package com.example.teamalfred.controllers;

import com.example.teamalfred.database.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalyticsController {

    @FXML private Label totalStudentsLabel;
    @FXML private Label totalAssignmentsLabel;
    @FXML private Label englishStudentsLabel;
    @FXML private Label mathsStudentsLabel;
    @FXML private Label scienceStudentsLabel;
    @FXML private Label englishAssignmentsLabel;
    @FXML private Label mathsAssignmentsLabel;
    @FXML private Label scienceAssignmentsLabel;

    public void initialize() {
        try (Connection conn = DatabaseConnection.getInstance()) {
            updateLabel(totalStudentsLabel, "SELECT COUNT(*) FROM students");
            updateLabel(totalAssignmentsLabel, "SELECT COUNT(*) FROM assessments");

            updateLabel(englishStudentsLabel, "SELECT COUNT(*) FROM students WHERE subject = 'English'");
            updateLabel(mathsStudentsLabel, "SELECT COUNT(*) FROM students WHERE subject = 'Maths'");
            updateLabel(scienceStudentsLabel, "SELECT COUNT(*) FROM students WHERE subject = 'Science'");

            updateLabel(englishAssignmentsLabel, "SELECT COUNT(*) FROM assessments WHERE subject = 'English'");
            updateLabel(mathsAssignmentsLabel, "SELECT COUNT(*) FROM assessments WHERE subject = 'Maths'");
            updateLabel(scienceAssignmentsLabel, "SELECT COUNT(*) FROM assessments WHERE subject = 'Science'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLabel(Label label, String query) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                label.setText(String.valueOf(rs.getInt(1)));
            }
        }
    }

    public class StatEntry {
        private final SimpleStringProperty category;
        private final SimpleStringProperty value;

        public StatEntry(String category, String value) {
            this.category = new SimpleStringProperty(category);
            this.value = new SimpleStringProperty(value);
        }

        public String getCategory() {
            return category.get();
        }

        public String getValue() {
            return value.get();
        }

        public SimpleStringProperty categoryProperty() {
            return category;
        }

        public SimpleStringProperty valueProperty() {
            return value;
        }
    }
}
