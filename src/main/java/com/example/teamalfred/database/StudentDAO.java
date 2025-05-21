package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.example.teamalfred.database.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class StudentDAO {
    private static final Connection conn;

    static {
        try {
            conn = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public StudentDAO() throws SQLException {
    }

    public static List<Student> getStudentsBySubject(String subject) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE subject = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, subject);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("subject")
                ));
            }
        }
        return students;
    }
}




