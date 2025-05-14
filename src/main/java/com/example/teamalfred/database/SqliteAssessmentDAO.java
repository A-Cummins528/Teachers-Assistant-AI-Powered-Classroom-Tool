package com.example.teamalfred.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteAssessmentDAO implements AssessmentDAO {

    private final Connection conn = DatabaseConnection.getInstance();

    public SqliteAssessmentDAO() throws SQLException {
    }

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
                list.add(a);
            }
        }
        return list;
    }
    public void insertAssessment(Assessment a) throws SQLException {
        String sql = "INSERT INTO assessments (title, subject, dueDate, status, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getTitle());
            stmt.setString(2, a.getSubject());
            stmt.setString(3, a.getDueDate());
            stmt.setString(4, a.getStatus());
            stmt.setString(5, a.getType());
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
        String sql = "UPDATE assessments SET title = ?, subject = ?, dueDate = ?, status = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getTitle());
            stmt.setString(2, a.getSubject());
            stmt.setString(3, a.getDueDate());
            stmt.setString(4, a.getStatus());
            stmt.setString(5, a.getType());
            stmt.setInt(6, a.getId());
            stmt.executeUpdate();
        }
    }
}
