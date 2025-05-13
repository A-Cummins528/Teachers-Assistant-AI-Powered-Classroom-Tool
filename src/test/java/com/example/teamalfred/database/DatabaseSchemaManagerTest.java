package com.example.teamalfred.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the {@link DatabaseSchemaManager} class using an IN-MEMORY SQLite database.
 * Uses @BeforeEach and @AfterEach to set up the in-memory DB, reset the schema, and clean up.
 */
public class DatabaseSchemaManagerTest {

    // Use the in-memory database URL
    private static final String TEST_DB_URL = "jdbc:sqlite::memory:";

    private DatabaseSchemaManager schemaManager;
    private Connection conn; // Store connection for use within tests

    /**
     * This method runs BEFORE each test method in this class.
     * Configures DatabaseConnection for in-memory use, gets a connection,
     * initializes the schema manager, and resets the schema in the in-memory DB.
     *
     * @throws SQLException if database connection or schema reset fails.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        // Configure DatabaseConnection to use the in-memory database
        DatabaseConnection.setTestDatabaseUrl(TEST_DB_URL);

        // Get the connection (will now be the in-memory one)
        // We need to get it *after* setting the URL but *before* schemaManager uses it.
        conn = DatabaseConnection.getInstance();
        assertNotNull(conn, "Database connection must be available for tests.");

        // Initialize schema manager (will use the in-memory connection via DatabaseConnection.getInstance())
        schemaManager = new DatabaseSchemaManager();

        // Reset schema within the in-memory database
        schemaManager.resetSchema();
    }

    /**
     * This method runs AFTER each test method in this class.
     * Resets the DatabaseConnection class, closing the connection to the in-memory DB.
     */
    @AfterEach
    public void tearDown() {
        // Reset DatabaseConnection state (closes connection, clears test URL)
        DatabaseConnection.resetForTesting();
        conn = null; // Nullify the stored connection variable
        // No need to explicitly drop table
    }


    // --- Test Methods (operates on in-memory DB) ---

    /**
     * Tests that the 'users' table is created successfully by initializeSchema().
     * Queries the sqlite_master table to check for existence.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testInitializeSchemaCreatesTable() throws SQLException {
        // Arrange (Setup already called initializeSchema via resetSchema)
        boolean tableExists = false;
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='users';";

        // Act: Use the connection obtained in setUp
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            tableExists = rs.next();
        }

        // Assert
        assertTrue(tableExists, "'users' table should exist after initializeSchema() is called.");
    }

    /**
     * Tests that the 'users' table is dropped successfully by dropTable().
     * Queries the sqlite_master table to confirm it no longer exists.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testDropTableRemovesTable() throws SQLException {
        // Arrange (Setup created the table)
        // Act: Explicitly drop table using the schemaManager
        schemaManager.dropTable();

        // Assert: Query sqlite_master to check if table still exists
        boolean tableExists = true;
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='users';";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            tableExists = rs.next();
        }
        assertFalse(tableExists, "'users' table should not exist after dropTable() is called.");
    }

    /**
     * Integration test to verify the structure of the 'users' table after creation.
     * Checks column names, data types, nullability, and primary key using metadata.
     *
     * @throws SQLException if database metadata retrieval fails.
     */
    @Test
    public void testInitializeSchemaCreatesCorrectTableStructure() throws SQLException {
        // Arrange (Setup already called initializeSchema via resetSchema)
        DatabaseMetaData metaData = conn.getMetaData(); // Use connection from setUp
        Map<String, ColumnInfo> actualColumns = new HashMap<>();

        // Act: Get column information using DatabaseMetaData
        try (ResultSet columns = metaData.getColumns(null, null, "users", null)) {
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                boolean isNullable = "YES".equalsIgnoreCase(columns.getString("IS_NULLABLE"));
                actualColumns.put(columnName, new ColumnInfo(typeName, isNullable));
            }
        }

        // Act: Get primary key information
        String primaryKeyColumn = null;
        try (ResultSet pk = metaData.getPrimaryKeys(null, null, "users")) {
            if (pk.next()) {
                primaryKeyColumn = pk.getString("COLUMN_NAME");
            }
        }

        // Assert: Verify the expected columns, types, nullability, and PK
        assertEquals(9, actualColumns.size(), "Should have 9 columns.");

        // Check each column (using helper method)
        assertColumn(actualColumns, "id", "INTEGER", false); // PK is implicitly NOT NULL in SQLite
        assertColumn(actualColumns, "firstName", "VARCHAR", false);
        assertColumn(actualColumns, "lastName", "VARCHAR", false);
        assertColumn(actualColumns, "mobile", "VARCHAR", false);
        assertColumn(actualColumns, "email", "VARCHAR", false);
        assertColumn(actualColumns, "password", "VARCHAR", false);
        assertColumn(actualColumns, "userType", "VARCHAR", false);
        assertColumn(actualColumns, "grade", "INTEGER", true);
        assertColumn(actualColumns, "className", "VARCHAR", true);

        // Check primary key
        assertEquals("id", primaryKeyColumn, "Primary key should be the 'id' column.");
    }

    // --- Helper Methods/Classes ---

    /** Helper record to store column information for comparison. */
    private record ColumnInfo(String type, boolean nullable) {}

    /** Helper assertion method to check column details. */
    private void assertColumn(Map<String, ColumnInfo> actualColumns, String expectedName, String expectedType, boolean expectedNullable) {
        assertTrue(actualColumns.containsKey(expectedName), "Column '" + expectedName + "' should exist.");
        ColumnInfo info = actualColumns.get(expectedName);
        // Use contains for type check as SQLite types can vary slightly (e.g., VARCHAR vs TEXT)
        // Let's stick to assertEquals for now as the schema defines VARCHAR explicitly
        assertEquals(expectedType, info.type(), "Column '" + expectedName + "' should have type " + expectedType + ".");
        assertEquals(expectedNullable, info.nullable(), "Column '" + expectedName + "' should have nullable=" + expectedNullable + ".");
    }
}
