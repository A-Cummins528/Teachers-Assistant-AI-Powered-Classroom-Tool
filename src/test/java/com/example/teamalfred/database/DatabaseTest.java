package com.example.teamalfred.database;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the {@link DatabaseConnection} class functionality.
 */
public class DatabaseTest {

    /**
     * Verifies that {@link DatabaseConnection#getInstance()} returns a non-null Connection object.
     * This acts as a basic integration check to ensure the database connection can be initiated.
     */
    @Test
    public void testGetInstance_ShouldReturnNonNullConnection() {
        Connection conn = DatabaseConnection.getInstance();
        assertNotNull(conn, "DatabaseConnection.getInstance() should return a non-null connection.");
    }
}

// TODO: Add more tests:
//  Error handling if the DB connection fails (e.g., what happens if getInstance() throws an exception?)
//  Add valid user (mocks) -> Test the DAO/Service layer method for adding users, mocking DB interaction
//  Add invalid user -> Test DAO/Service layer handles invalid input gracefully