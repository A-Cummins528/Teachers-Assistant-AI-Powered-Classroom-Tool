package com.example.teamalfred.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite implementation of the {@link AssessmentDAO} interface.
 * Provides CRUD operations for assessments stored in the database.
 */
public class SqliteAssessmentDAO implements AssessmentDAO {

    private final Connection conn = DatabaseConnection.getInstance();

    /**
     * Constructs a new {@code SqliteAssessmentDAO}.
     *
     * @throws SQLException If the database connection cannot be established.
     */
    public SqliteAssessmentDAO() throws SQLException {}

    /**
     * Retrieves all assessments from the database.
     *
     * @return A list of all {@link Assessment} records.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Assessment> getAllAssessments() throws SQLException {
        List<Assessment> list = new ArrayList<>();
        String sql = "SELECT * FROM assessments";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Assessment a = new Assessment(
                        rs.getString("title"),
                        rs.getString("subject"),
                        rs.getString("dueDate"),
                        rs.getString("status"),
                        rs.getString("type")
                );
                a.setId(rs.getInt("id"));
                a.setStudentId(rs.getInt("studentId"));
                list.add(a);
            }
        }
        return list;
    }

    /**
     * Inserts a new assessment into the database, including the student ID.
     *
     * @param a The {@link Assessment} object to insert.
     * @throws SQLException If a database access error occurs.
     */
    public void insertAssessment(Assessment a) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO assessments (title, subject, dueDate, status, type, studentId) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, a.getTitle());
            stmt.setString(2, a.getSubject());
            stmt.setString(3, a.getDueDate());
            stmt.setString(4, a.getStatus());
            stmt.setString(5, a.getType());
            stmt.setInt(6, a.getStudentId());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes an assessment by its ID.
     *
     * @param assessmentId The ID of the assessment to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteAssessment(int assessmentId) throws SQLException {
        String sql = "DELETE FROM assessments WHERE id = ?";

        // ✅ Get a fresh connection every time
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assessmentId);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing assessment in the database.
     *
     * @param a The {@link Assessment} object with updated fields.
     * @throws SQLException If a database access error occurs.
     */
    public void updateAssessment(Assessment a) throws SQLException {
        String sql = "UPDATE assessments SET title = ?, subject = ?, dueDate = ?, status = ?, type = ?, studentId = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getTitle());
            stmt.setString(2, a.getSubject());
            stmt.setString(3, a.getDueDate());
            stmt.setString(4, a.getStatus());
            stmt.setString(5, a.getType());
            stmt.setInt(6, a.getStudentId());
            stmt.setInt(7, a.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all assessments for a specific student by their ID.
     *
     * @param studentId The ID of the student.
     * @return A list of {@link Assessment} objects assigned to the student.
     * @throws SQLException If a database access error occurs.
     */
    public List<Assessment> getAssessmentsByStudentId(int studentId) throws SQLException {
        List<Assessment> list = new ArrayList<>();
        String sql = "SELECT * FROM assessments WHERE studentId = ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Assessment a = new Assessment(
                            rs.getString("title"),
                            rs.getString("subject"),
                            rs.getString("dueDate"),
                            rs.getString("status"),
                            rs.getString("type")
                    );
                    a.setId(rs.getInt("id"));
                    a.setStudentId(studentId);
                    list.add(a);
                }
            }
        }

        return list;
    }

    /**
     * Inserts the same assessment for all students enrolled in a specific subject.
     *
     * @param title    The title of the assessment.
     * @param subject  The subject associated with the assessment.
     * @param dueDate  The due date of the assessment.
     * @param status   The current status (e.g., "Pending", "Completed").
     * @param type     The type of assessment (e.g., "Quiz", "Assignment").
     * @throws SQLException If a database access error occurs.
     */
    public void insertAssessmentForSubject(String title, String subject, String dueDate, String status, String type) throws SQLException {
        String getStudentsSql = "SELECT student_id FROM students WHERE subject = ?";
        String insertSql = "INSERT INTO assessments (title, subject, dueDate, status, type, studentId) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                PreparedStatement getStudentsStmt = conn.prepareStatement(getStudentsSql);
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)
        ) {
            getStudentsStmt.setString(1, subject);
            ResultSet rs = getStudentsStmt.executeQuery();

            while (rs.next()) {
                insertStmt.setString(1, title);
                insertStmt.setString(2, subject);
                insertStmt.setString(3, dueDate);
                insertStmt.setString(4, status);
                insertStmt.setString(5, type);
                insertStmt.setInt(6, rs.getInt("student_id"));

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
        }
    }

    public void deleteAssessmentsBySubject(String subject) throws SQLException {
        String sql = "DELETE FROM assessments WHERE subject = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subject);
            stmt.executeUpdate();
        }
    }
}
