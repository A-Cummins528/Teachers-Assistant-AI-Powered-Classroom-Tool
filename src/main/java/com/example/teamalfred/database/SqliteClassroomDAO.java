package com.example.teamalfred.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite implementation of the {@link ClassroomDAO} interface.
 * Provides methods for interacting with the 'classes' table in the database.
 */
public class SqliteClassroomDAO implements ClassroomDAO {

    /**
     * Inserts a new classroom into the database.
     *
     * @param classroom The {@link Classroom} object containing the name of the class to be created.
     * @throws SQLException If a database access error occurs or the SQL statement is invalid.
     */
    @Override
    public void createClassroom(Classroom classroom) throws SQLException {
        String sql = "INSERT INTO classes (class_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classroom.getClassName());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all classrooms from the database.
     *
     * @return A list of {@link Classroom} objects representing all classrooms stored in the database.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Retrieves a specific classroom from the database by its ID.
     *
     * @param classId The ID of the classroom to retrieve.
     * @return A {@link Classroom} object if found, otherwise {@code null}.
     * @throws SQLException If a database access error occurs or the query is invalid.
     */
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
