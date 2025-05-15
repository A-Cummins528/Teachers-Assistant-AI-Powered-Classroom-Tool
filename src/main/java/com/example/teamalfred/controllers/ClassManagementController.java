package com.example.teamalfred.controllers;

import com.example.teamalfred.database.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.net.URL;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ClassManagementController implements Initializable {

    private static final String FILTER_ALL = "All";
    private static final String FILTER_ABSENT_ONLY = "Absent Only";

    private User currentUser;

    private final ObservableList<StudentAttendance> attendanceData = FXCollections.observableArrayList();
    private final FilteredList<StudentAttendance> filteredData = new FilteredList<>(attendanceData);

    private final AttendanceDAO attendanceDAO = new SqliteAttendanceDAO();
    private final StudentDAO studentDAO = new SqliteStudentDAO();
    private final ClassroomDAO classroomDAO = new SqliteClassroomDAO();

    @FXML private Label headerLabel;
    @FXML private DatePicker attendanceDatePicker;
    @FXML private ComboBox<Classroom> classSelector;
    @FXML private ComboBox<String> filterSelector;
    @FXML private TableView<StudentAttendance> attendanceTable;
    @FXML private TableColumn<StudentAttendance, String> studentNameColumn;
    @FXML private TableColumn<StudentAttendance, Boolean> presentColumn;
    @FXML private TableColumn<StudentAttendance, Boolean> absentColumn;
    @FXML private TableColumn<StudentAttendance, Boolean> lateColumn;
    @FXML private TableColumn<StudentAttendance, Boolean> excusedColumn;
    @FXML private TableColumn<StudentAttendance, String> notesColumn;
    @FXML private Button saveAttendanceButton;
    @FXML private Button clearFormButton;
    @FXML private Button exportReportButton;
    @FXML private TextField firstNameField, lastNameField, emailField;
    @FXML private Button addStudentButton, removeStudentButton;
    @FXML private ListView<Student> studentListView;

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (headerLabel != null) {
            headerLabel.setText("Welcome, " + user.getFirstName());
        }
    }

    @FXML
    private void handleAddStudent() {
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        Classroom selectedClass = classSelector.getValue();

        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || selectedClass == null) {
            showAlert("Please fill all fields and select a class.");
            return;
        }

        try {
            Student student = new Student(first, last, email, selectedClass.getId());
            new SqliteStudentDAO().createStudent(student);
            loadStudentsForSelectedClass();  // refresh UI
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to add student.");
        }
    }

    @FXML
    private void handleRemoveStudent() {
        Student selected = studentListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No student selected.");
            return;
        }

        try {
            new SqliteStudentDAO().deleteStudent(selected.getId());
            loadStudentsForSelectedClass();  // refresh UI
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to remove student.");
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:class_management_test.db");
        setupAttendanceTableColumns();
        setupClassSelector();
        setupFilterSelector();
        setupRowClickHandler();
        attendanceTable.setEditable(true);
        attendanceTable.setItems(filteredData);
    }

    private void setupAttendanceTableColumns() {
        studentNameColumn.setCellValueFactory(data -> data.getValue().studentNameProperty());
        presentColumn.setCellValueFactory(data -> data.getValue().presentProperty());
        absentColumn.setCellValueFactory(data -> data.getValue().absentProperty());
        lateColumn.setCellValueFactory(data -> data.getValue().lateProperty());
        excusedColumn.setCellValueFactory(data -> data.getValue().excusedProperty());
        notesColumn.setCellValueFactory(data -> data.getValue().notesProperty());

        presentColumn.setCellFactory(tc -> editableCheckbox());
        absentColumn.setCellFactory(tc -> editableCheckbox());
        lateColumn.setCellFactory(tc -> editableCheckbox());
        excusedColumn.setCellFactory(tc -> editableCheckbox());
    }

    private CheckBoxTableCell<StudentAttendance, Boolean> editableCheckbox() {
        CheckBoxTableCell<StudentAttendance, Boolean> cell = new CheckBoxTableCell<>();
        cell.setEditable(true);
        return cell;
    }

    private void setupClassSelector() {
        try {
            List<Classroom> classrooms = classroomDAO.getAllClassrooms();
            classSelector.setItems(FXCollections.observableArrayList(classrooms));
            if (!classrooms.isEmpty()) {
                classSelector.getSelectionModel().selectFirst();
                loadStudentsForSelectedClass();
            }
            classSelector.setOnAction(e -> loadStudentsForSelectedClass());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupFilterSelector() {
        filterSelector.setItems(FXCollections.observableArrayList(FILTER_ALL, FILTER_ABSENT_ONLY));
        filterSelector.getSelectionModel().selectFirst();
        filterSelector.setOnAction(e -> applyFilter());
    }

    private void setupRowClickHandler() {
        attendanceTable.setRowFactory(tv -> {
            TableRow<StudentAttendance> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    StudentAttendance student = row.getItem();
                    showStudentMonthlyStats(student.getStudentId(), student.studentNameProperty().get());
                }
            });
            return row;
        });
    }

    private void applyFilter() {
        String selected = filterSelector.getSelectionModel().getSelectedItem();
        if (FILTER_ABSENT_ONLY.equals(selected)) {
            filteredData.setPredicate(student -> student.absentProperty().get());
        } else {
            filteredData.setPredicate(null);
        }
    }

    private void loadStudentsForSelectedClass() {
        Classroom selectedClass = classSelector.getValue();
        if (selectedClass == null) return;

        try {
            String date = attendanceDatePicker.getValue() != null ? attendanceDatePicker.getValue().toString() : "";
            Map<Integer, AttendanceRecord> attendanceMap = attendanceDAO.getAttendanceMapForClassAndDate(selectedClass.getId(), date);

            List<Student> students = studentDAO.getStudentsByClassId(selectedClass.getId());
            attendanceData.clear();
            for (Student s : students) {
                AttendanceRecord record = attendanceMap.getOrDefault(s.getId(), new AttendanceRecord(false, false, false, false, ""));
                attendanceData.add(new StudentAttendance(s.getId(), s.getFullName(), record.present, record.absent, record.late, record.excused, record.notes));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveAttendance() {
        Classroom selectedClass = classSelector.getValue();
        String date = attendanceDatePicker.getValue() != null ? attendanceDatePicker.getValue().toString() : null;
        if (selectedClass == null || date == null) {
            System.out.println("Date or class not selected.");
            return;
        }

        try {
            for (StudentAttendance sa : attendanceData) {
                attendanceDAO.saveAttendance(
                        sa.getStudentId(),
                        selectedClass.getId(),
                        date,
                        sa.presentProperty().get(),
                        sa.absentProperty().get(),
                        sa.lateProperty().get(),
                        sa.excusedProperty().get(),
                        sa.notesProperty().get()
                );
            }
            System.out.println("Attendance saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showStudentMonthlyStats(int studentId, String studentName) {
        try {
            YearMonth currentMonth = YearMonth.now();
            int absentCount = attendanceDAO.countByStatusInMonth(studentId, "absent", currentMonth);
            int excusedCount = attendanceDAO.countByStatusInMonth(studentId, "excused", currentMonth);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Attendance Summary");
            alert.setHeaderText("For " + studentName);
            alert.setContentText("This month:\nAbsent days: " + absentCount + "\nExcused days: " + excusedCount);
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class StudentAttendance {
        private final int studentId;
        private final SimpleStringProperty studentName = new SimpleStringProperty();
        private final SimpleBooleanProperty present = new SimpleBooleanProperty();
        private final SimpleBooleanProperty absent = new SimpleBooleanProperty();
        private final SimpleBooleanProperty late = new SimpleBooleanProperty();
        private final SimpleBooleanProperty excused = new SimpleBooleanProperty();
        private final SimpleStringProperty notes = new SimpleStringProperty("");

        public StudentAttendance(int studentId, String name, boolean present, boolean absent, boolean late, boolean excused, String notes) {
            this.studentId = studentId;
            this.studentName.set(name);
            this.present.set(present);
            this.absent.set(absent);
            this.late.set(late);
            this.excused.set(excused);
            this.notes.set(notes);

            this.present.addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    this.absent.set(false);
                    this.excused.set(false);
                }
            });
            this.absent.addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    this.present.set(false);
                    this.excused.set(false);
                }
            });
            this.excused.addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    this.present.set(false);
                    this.absent.set(false);
                }
            });
        }

        public int getStudentId() { return studentId; }
        public StringProperty studentNameProperty() { return studentName; }
        public BooleanProperty presentProperty() { return present; }
        public BooleanProperty absentProperty() { return absent; }
        public BooleanProperty lateProperty() { return late; }
        public BooleanProperty excusedProperty() { return excused; }
        public StringProperty notesProperty() { return notes; }
    }
}
