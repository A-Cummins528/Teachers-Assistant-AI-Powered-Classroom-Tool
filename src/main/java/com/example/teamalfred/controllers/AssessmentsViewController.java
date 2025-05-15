package com.example.teamalfred.controllers;

import com.example.teamalfred.database.Assessment;
import com.example.teamalfred.database.SqliteAssessmentDAO;
import com.example.teamalfred.database.Student;
import com.example.teamalfred.database.StudentDAO;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class AssessmentsViewController {


    @FXML private TextField titleField;
    @FXML private TextField subjectField;
    @FXML private TextField dueDateField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button addAssessmentBtn;
    @FXML private Button deleteAssessmentBtn;

    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ListView<String> subjectListView;
    @FXML private Label studentsLabel;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    @FXML private TableView<Assessment> assessmentsTable;
    @FXML private TableColumn<Assessment, String> assessmentTitleColumn;
    @FXML private TableColumn<Assessment, String> assessmentSubjectColumn;
    @FXML private TableColumn<Assessment, String> assessmentDueColumn;
    @FXML private TableColumn<Assessment, String> assessmentStatusColumn;


    @FXML private TableColumn<Student, Void> addAssessmentColumn;



    private ObservableList<Student> studentsInSubject;
    private ObservableList<Assessment> masterData;
    private final SqliteAssessmentDAO dao = new SqliteAssessmentDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public AssessmentsViewController() throws SQLException {
        // Constructor must declare or handle SQLException because dao throws it
    }

    @FXML
    public void initialize() {

        sortComboBox.setItems(FXCollections.observableArrayList("Sort by Date ↑", "Sort by Date ↓"));
        filterComboBox.setItems(FXCollections.observableArrayList("All", "English", "Maths", "Science", "History", "Geography", "Health", "Art", "Technology", "PE"));
        filterComboBox.setValue("All");

        //refreshTable();

//        sortComboBox.setOnAction(event -> applySortAndFilter());
//        filterComboBox.setOnAction(event -> applySortAndFilter());

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

        addAssessmentColumn.setCellFactory(col -> new TableCell<Student, Void>() {
            private final Button addBtn = new Button("Add Assessment");

            {
                addBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    openAddAssessmentPopupForStudent(student); // 🔗 Connects to popup
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




//    @FXML
//    private void handleAddAssessment() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/AddAssessmentView.fxml"));
//            Parent root = loader.load();
//
//            Stage stage = new Stage();
//            stage.setTitle("Add New Assessment");
//            stage.setScene(new Scene(root));
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.setResizable(false);
//
//            stage.setOnHiding(event -> refreshTable()); // Refresh on close
//            stage.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Unable to open Add Assessment window.");
//        }
//    }

//    @FXML
//    private void handleDeleteAssessment() {
//        Assessment selected = assessmentTable.getSelectionModel().getSelectedItem();
//        if (selected == null) {
//            showAlert("Please select an assessment to delete.");
//            return;
//        }
//
//        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
//        confirm.setTitle("Confirm Delete");
//        confirm.setHeaderText(null);
//        confirm.setContentText("Are you sure you want to delete \"" + selected.getTitle() + "\"?");
//        confirm.showAndWait().ifPresent(response -> {
//            if (response == ButtonType.OK) {
//                try {
//                    dao.deleteAssessment(selected.getId());
//                    refreshTable();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    showAlert("Error deleting assessment: " + e.getMessage());
//                }
//            }
//        });
//    }

//    @FXML
//    private void handleEditAssessment() {
//        Assessment selected = assessmentTable.getSelectionModel().getSelectedItem();
//        if (selected == null) {
//            showAlert("Please select an assessment to edit.");
//            return;
//        }
//
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/EditAssessmentView.fxml"));
//            Parent root = loader.load();
//
//            com.example.teamalfred.controllers.EditAssessmentController controller = loader.getController();
//            controller.setAssessment(selected);
//
//            Stage stage = new Stage();
//            stage.setTitle("Edit Assessment");
//            stage.setScene(new Scene(root));
//            stage.initModality(Modality.APPLICATION_MODAL);
//
//            stage.setOnHiding(e -> refreshTable());
//            stage.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert("Failed to open edit window.");
//        }
//    }

//    private void refreshTable() {
//        try {
//            List<Assessment> assessments = dao.getAllAssessments();
//            masterData = FXCollections.observableArrayList(assessments);
//            assessmentTable.setItems(masterData);
//            applySortAndFilter(); // re-apply current filters
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("Error loading assessments: " + e.getMessage());
//        }
//    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Assessment Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
//    private void applySortAndFilter() {
//        String selectedSort = sortComboBox.getValue();
//        String selectedFilter = filterComboBox.getValue();
//
//        FilteredList<Assessment> filteredList = new FilteredList<>(masterData, a -> {
//            if (selectedFilter == null || selectedFilter.equals("All")) {
//                return true;
//            }
//            return a.getSubject().equalsIgnoreCase(selectedFilter);
//        });
//
//        SortedList<Assessment> sortedList = new SortedList<>(filteredList);
//        if ("Sort by Date ↑".equals(selectedSort)) {
//            sortedList.setComparator(Comparator.comparing(a -> parseDate(a.getDueDate())));
//        } else if ("Sort by Date ↓".equals(selectedSort)) {
//            sortedList.setComparator(Comparator.comparing((Assessment a) -> parseDate(a.getDueDate())).reversed());
//        }
//
//        assessmentTable.setItems(sortedList);
//    }
    private LocalDate parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return LocalDate.MIN; // fallback if invalid date
        }
    }

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

    private void loadAssessmentsForStudent(int studentId) {
        try {
            List<Assessment> assessments = dao.getAssessmentsByStudentId(studentId);
            assessmentsTable.setItems(FXCollections.observableArrayList(assessments));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAddAssessmentPopupForStudent(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/teamalfred/AddAssessmentView.fxml"));
            Parent root = loader.load();

            AddAssessmentController controller = loader.getController();
            controller.setStudent(student); // Pass the selected student

            Stage stage = new Stage();
            stage.setTitle("Add Assessment for " + student.getFirstName());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh assessments table for selected student after closing
            loadAssessmentsForStudent(student.getId());

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Add Assessment window.");
            alert.showAndWait();
        }
    }
}
