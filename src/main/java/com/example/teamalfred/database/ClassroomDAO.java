package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object (DAO) interface for performing CRUD operations on classrooms.
 * Defines methods to interact with the {@code classes} table in the database.
 */
public interface ClassroomDAO {

    /**
     * Inserts a new classroom into the database.
     *
     * @param classroom The {@link Classroom} object to be inserted.
     * @throws SQLException If a database access error occurs.
     */
    void createClassroom(Classroom classroom) throws SQLException;

    /**
     * Retrieves all classrooms from the database.
     *
     * @return A list of all {@link Classroom} objects.
     * @throws SQLException If a database access error occurs.
     */
    List<Classroom> getAllClassrooms() throws SQLException;

    /**
     * Retrieves a specific classroom by its ID.
     *
     * @param classId The ID of the classroom to retrieve.
     * @return The corresponding {@link Classroom} object, or {@code null} if not found.
     * @throws SQLException If a database access error occurs.
     */
    Classroom getClassroomById(int classId) throws SQLException;
}
