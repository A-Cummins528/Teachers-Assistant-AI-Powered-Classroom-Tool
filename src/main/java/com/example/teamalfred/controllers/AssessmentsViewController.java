package com.example.teamalfred.controllers;

import com.example.teamalfred.database.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller class for the Assessments View.
 * Handles UI interactions related to assessments and students.
 */
public class AssessmentsViewController {

    // UI fields for assessment details input
    @FXML private TextField titleField;
    @FXML private TextField subjectField;
    @FXML private TextField dueDateField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button addAssessmentBtn;
    @FXML private Button deleteAssessmentBtn;

    // UI for subject and student display
    @FXML private ListView<String> subjectListView;
    @FXML private Label studentsLabel;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    // Table to display assessments
    @FXML private TableView<Assessment> assessmentsTable;
    @FXML private TableColumn<Assessment, String> assessmentTitleColumn;
    @FXML private TableColumn<Assessment, String> assessmentSubjectColumn;
    @FXML private TableColumn<Assessment, String> assessmentDueColumn;
    @FXML private TableColumn<Assessment, String> assessmentStatusColumn;

    // Table column to add assessments to individual students
    @FXML private TableColumn<Student, Void> addAssessmentColumn;

    // Data lists
    private ObservableList<Student> studentsInSubject;
    private ObservableList<Assessment> masterData;

    // DAO instances to interact with database
    private final SqliteAssessmentDAO dao = new SqliteAssessmentDAO();
    private final StudentDAO studentDAO = new SqliteStudentDAO();

    /**
     * Constructor that handles SQLException thrown by DAO initializations.
     */
    public AssessmentsViewController() throws SQLException {}

    /**
     * Initializes UI components and sets up listeners.
     */
    @FXML
    public void initialize() {
        ObservableList<String> subjects = FXCollections.observableArrayList(
                "English", "Maths", "Science", "History", "Geography", "Health", "Art", "Technology", "PE"
        );

        subjectListView.setItems(subjects);

        studentNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        assessmentTitleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));
        assessmentSubjectColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSubject()));
        assessmentDueColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDueDate()));
        assessmentStatusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        subjectListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadStudentsForSubject(newVal);
            }
        });

        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadAssessmentsForStudent(newSelection.getId());
            }
        });

        // Creates a button inside each row to add assessment to specific student
        addAssessmentColumn.setCellFactory(col -> new TableCell<Student, Void>() {
            private final Button addBtn = new Button("Add Assessment");

            {
                addBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    openAddAssessmentPopupForStudent(student);
                });
                addBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addBtn);
            }
        });
    }

    /**
     * Opens a modal popup to add assessments to a selected subject.
     */
    @FXML
    private void handleAddAssessmentToSubject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/AddAssessmentToSubjectView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Assessment to Subject");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Could not open popup.");
        }
    }

    /**
     * Deletes all assessments for a selected subject after user confirmation.
     */
    @FXML
    private void handleDeleteAssessmentsBySubject() {
        String selectedSubject = subjectListView.getSelectionModel().getSelectedItem();

        if (selectedSubject == null) {
            showAlert("Please select a subject first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete all assessments for subject: " + selectedSubject + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.deleteAssessmentsBySubject(selectedSubject);
                    showAlert("All assessments for '" + selectedSubject + "' have been deleted.");
                    assessmentsTable.getItems().clear();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error deleting assessments: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Deletes the selected assessment after user confirmation.
     */
    @FXML
    private void handleDeleteSelectedAssessment() {
        Assessment selected = assessmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an assessment to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText("Are you sure you want to delete this assessment?");
        confirm.setContentText("Title: " + selected.getTitle());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dao.deleteAssessment(selected.getId());
                    assessmentsTable.getItems().remove(selected);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error deleting assessment: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Displays an alert with the given message.
     * @param message Message to show in alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Assessment Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Parses a string to a LocalDate using dd/MM/yyyy format.
     * @param dateStr The date string
     * @return A LocalDate object
     */
    private LocalDate parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return LocalDate.MIN; // fallback if invalid
        }
    }

    /**
     * Loads students associated with a specific subject.
     * @param subject The selected subject
     */
    private void loadStudentsForSubject(String subject) {
        try {
            List<Student> students = studentDAO.getStudentsBySubject(subject);
            ObservableList<Student> studentData = FXCollections.observableArrayList(students);
            studentsTable.setItems(studentData);
            studentsLabel.setText("Students enrolled in " + subject + ":");
        } catch (SQLException e) {
            e.printStackTrace();
            studentsLabel.setText("⚠ Error loading students.");
        }
    }

    /**
     * Loads assessments assigned to a given student.
     * @param studentId The ID of the student
     */
    private void loadAssessmentsForStudent(int studentId) {
        try {
            List<Assessment> assessments = dao.getAssessmentsByStudentId(studentId);
            assessmentsTable.setItems(FXCollections.observableArrayList(assessments));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a popup window to add an assessment for a specific student.
     * @param student The student to add the assessment for
     */
    private void openAddAssessmentPopupForStudent(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/AddAssessmentView.fxml"));
            Parent root = loader.load();

            AddAssessmentController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Add Assessment for " + student.getFirstName());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadAssessmentsForStudent(student.getId());

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Add Assessment window.");
            alert.showAndWait();
        }
    }
}
