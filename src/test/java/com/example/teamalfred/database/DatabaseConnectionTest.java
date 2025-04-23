package com.example.teamalfred.database;

import org.junit.jupiter.api.AfterEach; // Import AfterEach
import org.junit.jupiter.api.BeforeEach; // Import BeforeEach
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Basic tests for the {@link DatabaseConnection} class.
 * Uses @BeforeEach and @AfterEach to reset the connection state between tests.
 */
public class DatabaseConnectionTest {

    /**
     * This method runs BEFORE each test method in this class.
     * Ensures that any existing connection instance is closed, providing a clean slate.
     */
    @BeforeEach
    public void setUp() {
        DatabaseConnection.closeInstance();
    }

    /**
     * This method runs AFTER each test method in this class.
     * Ensures that any connection instance created during the test is closed.
     */
    @AfterEach
    public void tearDown() {
        DatabaseConnection.closeInstance();
    }


    // --- Test Methods ---

    /**
     * Tests that a connection object can be retrieved via getInstance().
     * Checks if the connection is not null.
     */
    @Test
    public void testConnectionCreation() {
        // (@BeforeEach ran, so DatabaseConnection.instance should be null here)
        Connection conn = DatabaseConnection.getInstance();
        assertNotNull(conn, "getInstance() should return a non-null connection.");
        // (@AfterEach will run after this)
    }

    /**
     * Tests that the connection can be closed using the closeInstance() method.
     * Declares SQLException because conn.isClosed() can throw it.
     */
    @Test
    public void testConnectionClosed() throws SQLException {
        // (@BeforeEach ran, so DatabaseConnection.instance should be null here)
        // 1. Get a connection instance first
        Connection conn = DatabaseConnection.getInstance();
        // Make sure we actually got a connection before trying to close and test it
        assertNotNull(conn, "Need a non-null connection to test closing.");

        // 2. Call the method to close the connection
        DatabaseConnection.closeInstance();

        // 3. Check if the connection object reports itself as closed
        assertEquals(true, conn.isClosed(), "Connection should be closed after calling closeInstance().");
        // (@AfterEach will run after this)
    }
}