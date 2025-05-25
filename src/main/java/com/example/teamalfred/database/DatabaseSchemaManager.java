package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for managing the database schema,
 * such as creating or dropping the users table.
 * IMPORTANT: Assumes the connection lifecycle is managed externally (e.g., by DatabaseConnection singleton).
 */
public class DatabaseSchemaManager {

    private static final String TABLE_NAME = "users";

    /**
     * Creates the 'users' table if it does not already exist.
     * Does NOT close the connection obtained from DatabaseConnection.
     *
     * @throws SQLException if the database connection cannot be established or the query fails.
     */

    public void initializeSchema() throws SQLException {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement())
        {

            // 1. Preserve existing users table (uses TABLE_NAME constant)
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "firstName VARCHAR NOT NULL, " +
                    "lastName VARCHAR NOT NULL, " +
                    "mobile VARCHAR NOT NULL, " +
                    "email VARCHAR NOT NULL UNIQUE, " +
                    "password VARCHAR NOT NULL, " +
                    "userType VARCHAR NOT NULL CHECK (userType IN ('teacher', 'student')), " +
                    "grade INTEGER, " +
                    "className VARCHAR)";
            stmt.execute(sql);

            // 2. Classrooms table
            String classesTable = "CREATE TABLE IF NOT EXISTS classes (" +
                    "class_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "class_name TEXT NOT NULL)";
            stmt.execute(classesTable);

            // 3. Students table
            String studentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                    "student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "first_name TEXT NOT NULL, " +
                    "last_name TEXT NOT NULL, " +
                    "email TEXT, " +
                    "class_id INTEGER, " +
                    "FOREIGN KEY (class_id) REFERENCES classes(class_id))";
            stmt.execute(studentsTable);
            // 4. Attendance table
            String attendanceTable = "CREATE TABLE IF NOT EXISTS attendance (" +
                    "student_id INTEGER, " +
                    "class_id INTEGER, " +
                    "date TEXT, " +
                    "present BOOLEAN, " +
                    "absent BOOLEAN, " +
                    "late BOOLEAN, " +
                    "excused BOOLEAN, " +
                    "notes TEXT, " +
                    "PRIMARY KEY (student_id, class_id, date), " +
                    "FOREIGN KEY (student_id) REFERENCES students(student_id), " +
                    "FOREIGN KEY (class_id) REFERENCES classes(class_id))";
            stmt.execute(attendanceTable);
        }
    }

    /**
     * Drops the 'users' table if it exists.
     * Does NOT close the connection obtained from DatabaseConnection.
     *
     * @throws SQLException if the database connection cannot be established or the query fails.
     */
    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;

        Connection conn = getConnection(); // Get the shared connection
        // Use try-with-resources ONLY for the Statement
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        // DO NOT close the connection here - it's managed by DatabaseConnection
    }

    /**
     * Drops and recreates the 'users' table.
     * Useful for resetting the schema during development or testing.
     *
     * @throws SQLException if any database operation fails.
     */
    public void resetSchema() throws SQLException {
        // These calls will use the same shared connection instance
        dropTable();
        initializeSchema();
    }

    /**
     * Retrieves the shared database connection instance.
     *
     * @return a valid SQL Connection.
     * @throws SQLException if the connection is null or cannot be established by DatabaseConnection.
     */
    private Connection getConnection() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        // getInstance() now throws SQLException if connection fails, so null check isn't strictly needed here
        // but doesn't hurt.
        if (conn == null) {
            // This case should ideally not be reached if getInstance throws exception on failure
            throw new SQLException("Database connection could not be established (returned null).");
        }
        // Check if connection is closed (optional, defensive check)
        if (conn.isClosed()) {
            throw new SQLException("Database connection is closed.");
        }
        return conn;
    }
}
