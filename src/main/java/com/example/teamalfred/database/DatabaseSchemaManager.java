package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for managing the database schema,
 * such as creating or dropping the users table.
 */
public class DatabaseSchemaManager {

    private static final String TABLE_NAME = "users";

    /**
     * Creates the 'users' table if it does not already exist.
     *
     * @throws SQLException if the database connection cannot be established or the query fails.
     */
    public void initializeSchema() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstName VARCHAR NOT NULL, " +
                "lastName VARCHAR NOT NULL, " +
                "mobile VARCHAR NOT NULL, " +
                "email VARCHAR NOT NULL, " +
                "password VARCHAR NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Drops the 'users' table if it exists.
     *
     * @throws SQLException if the database connection cannot be established or the query fails.
     */
    public void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Drops and recreates the 'users' table.
     * Useful for resetting the schema during development or testing.
     *
     * @throws SQLException if any database operation fails.
     */
    public void resetSchema() throws SQLException {
        dropTable();
        initializeSchema();
    }

    /**
     * Retrieves a database connection or throws an error if unavailable.
     *
     * @return a valid SQL Connection.
     * @throws SQLException if the connection is null or cannot be established.
     */
    private Connection getConnection() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            throw new SQLException("Database connection could not be established.");
        }
        return conn;
    }
}
