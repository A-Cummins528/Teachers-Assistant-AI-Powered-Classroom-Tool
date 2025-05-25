package com.example.teamalfred.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteAssessmentDAO implements AssessmentDAO {

    private final Connection conn = DatabaseConnection.getInstance();

    public SqliteAssessmentDAO() throws SQLException {}

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
                a.setStudentId(rs.getInt("studentId")); // 👈 Include studentId
                list.add(a);
            }
        }
        return list;
    }

    // ✅ INSERT with studentId
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

    public void deleteAssessment(int assessmentId) throws SQLException {
        String sql = "DELETE FROM assessments WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assessmentId);
            stmt.executeUpdate();
        }
    }

    public void updateAssessment(Assessment a) throws SQLException {
        String sql = "UPDATE assessments SET title = ?, subject = ?, dueDate = ?, status = ?, type = ?, studentId = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getTitle());
            stmt.setString(2, a.getSubject());
            stmt.setString(3, a.getDueDate());
            stmt.setString(4, a.getStatus());
            stmt.setString(5, a.getType());
            stmt.setInt(6, a.getStudentId()); // 👈 Include studentId
            stmt.setInt(7, a.getId());
            stmt.executeUpdate();
        }
    }

    // ✅ NEW: Get assessments for a specific student
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
}
