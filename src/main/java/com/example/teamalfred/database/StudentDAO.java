package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for Student Data Access Object (DAO) operations.
 * Provides abstraction for CRUD operations and specific queries for students.
 */
public interface StudentDAO {

    /**
     * Creates a new student record in the database.
     * @param student The student object containing data for the new record.
     * @throws SQLException if a database access error occurs.
     */
    void createStudent(Student student) throws SQLException;

    /**
     * Updates an existing student's details in the database.
     * Assumes the student object contains the ID of the student to update.
     * @param student The student object containing updated data.
     * @throws SQLException if a database access error occurs.
     */
    void updateStudent(Student student) throws SQLException;

    /**
     * Deletes a student from the database based on their ID.
     * @param id The unique ID of the student to delete.
     * @throws SQLException if a database access error occurs.
     */
    void deleteStudent(int id) throws SQLException;

    /**
     * Finds a student by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the found Student, or an empty Optional if no student with that email exists.
     * @throws SQLException if a database access error occurs.
     */
    Optional<Student> findStudentByEmail(String email) throws SQLException;

    /**
     * Finds a student by their unique ID.
     * @param id The unique ID of the student to find.
     * @return An Optional containing the found Student, or an empty Optional if no student with that ID exists.
     * @throws SQLException if a database access error occurs.
     */
    Optional<Student> findStudentById(int id) throws SQLException;

    /**
     * Retrieves a list of all students from the database.
     * @return A List of all Student objects; the list may be empty if there are no students.
     * @throws SQLException if a database access error occurs.
     */
    List<Student> getAllStudents() throws SQLException;

    /**
     * Retrieves a list of students associated with a specific subject.
     * @param subject The subject name to filter students by.
     * @return A list of students enrolled in the given subject.
     * @throws SQLException if a database access error occurs.
     */
    List<Student> getStudentsBySubject(String subject) throws SQLException;

    /**
     * Retrieves a list of students associated with a specific class ID.
     * @param classId The class ID to filter students by.
     * @return A list of students enrolled in the given class.
     * @throws SQLException if a database access error occurs.
     */
    List<Student> getStudentsByClassId(int classId) throws SQLException;
}
