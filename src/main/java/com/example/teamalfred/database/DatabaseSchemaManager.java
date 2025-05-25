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
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "firstName VARCHAR NOT NULL, " +
                "lastName VARCHAR NOT NULL, " +
                "mobile VARCHAR NOT NULL, " +
                "email VARCHAR NOT NULL UNIQUE, " +
                "password VARCHAR NOT NULL, " +
                "userType VARCHAR NOT NULL CHECK (userType IN ('teacher', 'student')), " + // Teacher or Student
                "grade INTEGER, " + // Can be NULL
                "className VARCHAR" + // Can be NULL
                ")";
        String createAssessmentsTable = "CREATE TABLE IF NOT EXISTS assessments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "subject TEXT NOT NULL, " +
                "dueDate TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "type TEXT, " +
                "studentId INTEGER, " +
                "FOREIGN KEY (studentId) REFERENCES users(id) ON DELETE CASCADE" +
                ");";

        Connection conn = getConnection(); // Get the shared connection
        // Use try-with-resources ONLY for the Statement
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        // DO NOT close the connection here - it's managed by DatabaseConnection
    }

    /**
     * Drops the 'users' table if it exists.
     * Does NOT close the connection obtained from DatabaseConnection.
     *
     * @throws SQLException if the database connection cannot be established or the query fails.
     */

//    public void initializeAssessmentSchema() throws SQLException {
//        String createAssessmentsTable = "CREATE TABLE IF NOT EXISTS assessments (" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
//                "title TEXT NOT NULL, " +
//                "subject TEXT NOT NULL, " +
//                "dueDate TEXT NOT NULL, " +
//                "status TEXT NOT NULL" +
//                ");";
//
//        Connection conn = getConnection(); // Get the shared connection
//        // Use try-with-resources ONLY for the Statement
//
//        try (Statement stmt = conn.createStatement()) {
//            stmt.execute(createAssessmentsTable);
//        }
//
//        // DO NOT close the connection here - it's managed by DatabaseConnection
//    } JUSTIN FIX THIS, THIS NEEDS TO BE INITIALISE BROTHER.

    public void createStudentsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "subject TEXT NOT NULL)";

        Connection conn = getConnection(); // Get the shared connection

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

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
