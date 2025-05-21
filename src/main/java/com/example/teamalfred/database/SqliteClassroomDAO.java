package com.example.teamalfred.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteClassroomDAO implements ClassroomDAO {

    @Override
    public void createClassroom(Classroom classroom) throws SQLException {
        String sql = "INSERT INTO classes (class_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classroom.getClassName());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Classroom> getAllClassrooms() throws SQLException {
        List<Classroom> classrooms = new ArrayList<>();
        String sql = "SELECT class_id, class_name FROM classes";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("class_id");
                String name = rs.getString("class_name");
                classrooms.add(new Classroom(id, name));
            }
        }
        return classrooms;
    }


    @Override
    public Classroom getClassroomById(int classId) throws SQLException {
        String sql = "SELECT * FROM classes WHERE class_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Classroom(
                        rs.getInt("class_id"),
                        rs.getString("class_name")
                );
            }
        }
        return null;
    }
}
