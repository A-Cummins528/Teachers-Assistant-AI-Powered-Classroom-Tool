package com.example.teamalfred.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SQLite implementation of the {@link StudentDAO} interface for interacting with the 'students' table.
 * Provides CRUD operations and query methods for retrieving student records by class, subject, email, or ID.
 */
public class SqliteStudentDAO implements StudentDAO {

    /**
     * Inserts a new student into the database.
     *
     * @param student The {@link Student} object containing the student's data.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void createStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, email, class_id, subject) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setInt(4, student.getClassId());
            stmt.setString(5, student.getSubject() != null ? student.getSubject() : "General");
            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing student's information in the database.
     *
     * @param student The {@link Student} object containing updated data.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, email = ?, class_id = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setInt(4, student.getClassId());
            stmt.setInt(5, student.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a student from the database by their ID.
     *
     * @param id The ID of the student to delete.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Finds a student in the database by their email.
     *
     * @param email The email address of the student.
     * @return An {@link Optional} containing the {@link Student} if found, otherwise empty.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Optional<Student> findStudentByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToStudent(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a student in the database by their ID.
     *
     * @param id The ID of the student.
     * @return An {@link Optional} containing the {@link Student} if found, otherwise empty.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Optional<Student> findStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToStudent(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves all students from the database.
     *
     * @return A list of all {@link Student} records.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseConnection.getInstance();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
        }
        return students;
    }

    /**
     * Retrieves students associated with a specific subject.
     *
     * @param subject The subject to filter by.
     * @return A list of {@link Student} objects enrolled in the subject.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Student> getStudentsBySubject(String subject) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE subject = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
        }
        return students;
    }

    /**
     * Retrieves students associated with a specific class ID.
     *
     * @param classId The class ID to filter by.
     * @return A list of {@link Student} objects in the specified class.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Student> getStudentsByClassId(int classId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
        }
        return students;
    }

    /**
     * Maps a row from a {@link ResultSet} to a {@link Student} object.
     *
     * @param rs The {@link ResultSet} to map.
     * @return A populated {@link Student} object.
     * @throws SQLException If a column read error occurs.
     */
    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getInt("class_id"),
                rs.getString("subject")
        );
    }
}
