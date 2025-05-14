package com.example.teamalfred.controllers;

import com.example.teamalfred.database.Assessment;
import com.example.teamalfred.database.SqliteAssessmentDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class AssessmentsViewController {

    @FXML private TableView<Assessment> assessmentTable;
    @FXML private TableColumn<Assessment, String> titleColumn;
    @FXML private TableColumn<Assessment, String> subjectColumn;
    @FXML private TableColumn<Assessment, String> dueDateColumn;
    @FXML private TableColumn<Assessment, String> statusColumn;

    @FXML private TextField titleField;
    @FXML private TextField subjectField;
    @FXML private TextField dueDateField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button addAssessmentBtn;
    @FXML private Button deleteAssessmentBtn;
    @FXML private TableColumn<Assessment, String> typeColumn;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> filterComboBox;


    private ObservableList<Assessment> masterData;
    private final SqliteAssessmentDAO dao = new SqliteAssessmentDAO();

    public AssessmentsViewController() throws SQLException {
        // Constructor must declare or handle SQLException because dao throws it
    }

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTitle()));
        subjectColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSubject()));
        dueDateColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDueDate()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        sortComboBox.setItems(FXCollections.observableArrayList("Sort by Date ↑", "Sort by Date ↓"));
        filterComboBox.setItems(FXCollections.observableArrayList("All", "English", "Maths", "Science", "History", "Geography", "Health", "Art", "Technology", "PE"));

        filterComboBox.setValue("All");

        refreshTable();

        sortComboBox.setOnAction(event -> applySortAndFilter());
        filterComboBox.setOnAction(event -> applySortAndFilter());

    }

    @FXML
    private void handleAddAssessment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/AddAssessmentView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Assessment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.setOnHiding(event -> refreshTable()); // Refresh on close
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Unable to open Add Assessment window.");
        }
    }

    @FXML
    private void handleDeleteAssessment() {
        Assessment selected = assessmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an assessment to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete \"" + selected.getTitle() + "\"?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.deleteAssessment(selected.getId());
                    refreshTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error deleting assessment: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleEditAssessment() {
        Assessment selected = assessmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an assessment to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/EditAssessmentView.fxml"));
            Parent root = loader.load();

            com.example.teamalfred.controllers.EditAssessmentController controller = loader.getController();
            controller.setAssessment(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Assessment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setOnHiding(e -> refreshTable());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to open edit window.");
        }
    }

    private void refreshTable() {
        try {
            List<Assessment> assessments = dao.getAllAssessments();
            masterData = FXCollections.observableArrayList(assessments);
            assessmentTable.setItems(masterData);
            applySortAndFilter(); // re-apply current filters
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading assessments: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Assessment Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void applySortAndFilter() {
        String selectedSort = sortComboBox.getValue();
        String selectedFilter = filterComboBox.getValue();

        FilteredList<Assessment> filteredList = new FilteredList<>(masterData, a -> {
            if (selectedFilter == null || selectedFilter.equals("All")) {
                return true;
            }
            return a.getSubject().equalsIgnoreCase(selectedFilter);
        });

        SortedList<Assessment> sortedList = new SortedList<>(filteredList);
        if ("Sort by Date ↑".equals(selectedSort)) {
            sortedList.setComparator(Comparator.comparing(a -> parseDate(a.getDueDate())));
        } else if ("Sort by Date ↓".equals(selectedSort)) {
            sortedList.setComparator(Comparator.comparing((Assessment a) -> parseDate(a.getDueDate())).reversed());
        }

        assessmentTable.setItems(sortedList);
    }
    private LocalDate parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return LocalDate.MIN; // fallback if invalid date
        }
    }
}
