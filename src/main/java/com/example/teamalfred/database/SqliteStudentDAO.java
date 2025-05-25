package com.example.teamalfred.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the StudentDAO interface for SQLite database.
 * It handles the CRUD operations related to students using SQL queries.
 * Basically, it talks to the database and converts data to Student objects and vice versa.
 */
public class SqliteStudentDAO implements StudentDAO {

    /**
     * Inserts a new student into the database.
     * Uses a prepared statement to avoid SQL injection and to set parameters safely.
     *
     * @param student The student object containing data to insert
     * @throws SQLException If there is any SQL error during insertion
     */
    @Override
    public void createStudent(Student student) throws SQLException {
        // SQL command to insert student details into the students table
        String sql = "INSERT INTO students (first_name, last_name, email, class_id) VALUES (?, ?, ?, ?)";

        // Try-with-resources to auto-close PreparedStatement and get DB connection from singleton
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            // Set the parameters for the prepared statement in order
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setInt(4, student.getClassId());

            // Execute the update (INSERT)
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all students who belong to a specific class by class ID.
     * Runs a SELECT query with a WHERE filter on class_id.
     *
     * @param classId The class ID to filter students by
     * @return A list of Student objects who belong to the given class
     * @throws SQLException If there is a database access error
     */
    @Override
    public List<Student> getStudentsByClassId(int classId) throws SQLException {
        String sql = "SELECT * FROM students WHERE class_id = ?";
        List<Student> students = new ArrayList<>();

        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            // Set the classId parameter to filter students
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            // Loop through the result set and create Student objects for each row
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),      // Student unique ID
                        rs.getString("first_name"),   // Student first name
                        rs.getString("last_name"),    // Student last name
                        rs.getString("email"),        // Student email
                        rs.getInt("class_id")         // Student's class ID
                ));
            }
        }
        return students; // Return the list of students found
    }

    /**
     * Retrieves all students in the database without any filtering.
     * Useful for admin views or bulk operations.
     *
     * @return A list containing all Student objects in the students table
     * @throws SQLException If there is a problem querying the database
     */
    @Override
    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();

        // Using a simple Statement here since no parameters are needed
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop over all rows and create Student objects
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getInt("class_id")
                ));
            }
        }
        return students; // Return the complete student list
    }
}
