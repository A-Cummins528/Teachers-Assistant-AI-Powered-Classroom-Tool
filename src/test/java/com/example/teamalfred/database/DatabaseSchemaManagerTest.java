package com.example.teamalfred.database;

import org.junit.jupiter.api.AfterEach; // Import AfterEach
import org.junit.jupiter.api.BeforeEach; // Import BeforeEach
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic tests for the {@link DatabaseSchemaManager} class.
 * Uses @BeforeEach and @AfterEach to reset the database schema between tests.
 */
public class DatabaseSchemaManagerTest {

    private DatabaseSchemaManager schemaManager;

    /**
     * This method runs BEFORE each test method in this class.
     * Ensures that the database is reset to a clean state.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        schemaManager = new DatabaseSchemaManager();
        schemaManager.resetSchema();
    }

    /**
     * This method runs AFTER each test method in this class.
     * Ensures that the 'users' table is dropped after each test to avoid side effects.
     */
    @AfterEach
    public void tearDown() throws SQLException {
        schemaManager.dropTable();
    }


    // --- Test Methods ---

    /**
     * Tests that the 'users' table is created successfully by createTable().
     * Queries the sqlite_master table to check for existence.
     */
    @Test
    public void testInitializeSchema() throws SQLException {
        // Arrange
        Connection conn = DatabaseConnection.getInstance();
        Statement stmt = conn.createStatement();

        // Act
        ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='users';"
        );

        // Assert
        assertTrue(rs.next(), "'users' table should exist after createTable() is called.");
    }

    /**
     * Tests that the 'users' table is dropped successfully by dropTable().
     * Queries the sqlite_master table to confirm it no longer exists.
     */
    @Test
    public void testDropTable() throws SQLException {
        // Arrange
        schemaManager.dropTable(); // Explicitly drop table here
        Connection conn = DatabaseConnection.getInstance();
        Statement stmt = conn.createStatement();

        // Act
        ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='users';"
        );

        // Assert
        assertFalse(rs.next(), "'users' table should not exist after dropTable() is called.");
    }
}
