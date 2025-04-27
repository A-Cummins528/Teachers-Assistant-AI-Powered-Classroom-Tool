package com.example.teamalfred.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the {@link SqliteUserDAO} class using an IN-MEMORY SQLite database.
 * These tests interact with a temporary database that exists only during the test run.
 * Uses @BeforeEach and @AfterEach to set up the in-memory DB, reset the schema, and clean up.
 */
public class SqliteUserDAOTest {

    // Use the in-memory database URL
    private static final String TEST_DB_URL = "jdbc:sqlite::memory:";

    private DatabaseSchemaManager schemaManager;
    private UserDAO userDAO; // Use the interface type
    private User testUser;

    /**
     * Sets up the test environment before each test method.
     * Configures DatabaseConnection to use the in-memory URL.
     * Initializes the schema manager and DAO, resets the database schema,
     * and creates a standard test user object.
     *
     * @throws SQLException if database connection or schema reset fails.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        // Configure DatabaseConnection to use the in-memory database *before* any connection is made
        DatabaseConnection.setTestDatabaseUrl(TEST_DB_URL);

        // Now initialize components which will use the in-memory connection
        schemaManager = new DatabaseSchemaManager();
        userDAO = new SqliteUserDAO(); // Instantiate the implementation

        // Ensure a clean schema state within the in-memory database for each test
        schemaManager.resetSchema();

        // Create a standard user object for testing
        testUser = new User("John", "Doe", "john.doe@example.com", "0412345678", "password123");
    }

    /**
     * Cleans up the test environment after each test method.
     * Resets the DatabaseConnection class, which closes the connection
     * to the in-memory database (effectively destroying it) and removes the test URL override.
     */
    @AfterEach
    public void tearDown() {
        // Reset DatabaseConnection state (closes connection, clears test URL)
        DatabaseConnection.resetForTesting();
        // No need to explicitly drop table, as the in-memory DB is gone when connection closes
    }


    // --- Test Methods ---

    /**
     * Tests creating a new user and verifying it can be found by email.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testCreateUserAndFindByEmail() throws SQLException {
        // Act
        userDAO.createUser(testUser);

        // Assert
        Optional<User> foundUserOpt = userDAO.findUserByEmail(testUser.getEmail());
        assertTrue(foundUserOpt.isPresent(), "User should be found by email after creation.");

        User foundUser = foundUserOpt.get();
        assertEquals(testUser.getFirstName(), foundUser.getFirstName());
        assertEquals(testUser.getLastName(), foundUser.getLastName());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        assertEquals(testUser.getMobile(), foundUser.getMobile());
        assertTrue(foundUser.checkPassword("password123"));
        assertTrue(foundUser.getId() > 0, "User ID should be generated and positive.");
    }

    /**
     * Tests that creating a user with an existing email throws an SQLException.
     *
     * @throws SQLException if the first user creation fails (unexpected).
     */
    @Test
    public void testCreateUserWithDuplicateEmailThrowsException() throws SQLException {
        // Arrange
        userDAO.createUser(testUser);

        // Act & Assert
        User duplicateUser = new User("Jane", "Smith", "john.doe@example.com", "0487654321", "anotherPassword");
        Executable action = () -> userDAO.createUser(duplicateUser);
        assertThrows(SQLException.class, action, "Creating user with duplicate email should throw SQLException.");
    }

    /**
     * Tests finding a user by their ID after creation.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testFindUserById() throws SQLException {
        // Arrange
        userDAO.createUser(testUser);
        Optional<User> createdUserOpt = userDAO.findUserByEmail(testUser.getEmail());
        assertTrue(createdUserOpt.isPresent(), "User must exist to get ID for testing findById.");
        int userId = createdUserOpt.get().getId();

        // Act
        Optional<User> foundUserOpt = userDAO.findUserById(userId);

        // Assert
        assertTrue(foundUserOpt.isPresent(), "User should be found by ID.");
        assertEquals(userId, foundUserOpt.get().getId());
        assertEquals(testUser.getEmail(), foundUserOpt.get().getEmail());
    }

    /**
     * Tests that finding a non-existent user by ID returns an empty Optional.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testFindUserByIdNotFound() throws SQLException {
        // Act
        Optional<User> foundUserOpt = userDAO.findUserById(999);

        // Assert
        assertFalse(foundUserOpt.isPresent(), "Finding non-existent user by ID should return empty Optional.");
    }

    /**
     * Tests that finding a non-existent user by email returns an empty Optional.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testFindUserByEmailNotFound() throws SQLException {
        // Act
        Optional<User> foundUserOpt = userDAO.findUserByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundUserOpt.isPresent(), "Finding non-existent user by email should return empty Optional.");
    }

    /**
     * Tests updating an existing user's details.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testUpdateUser() throws SQLException {
        // Arrange
        userDAO.createUser(testUser);
        Optional<User> createdUserOpt = userDAO.findUserByEmail(testUser.getEmail());
        assertTrue(createdUserOpt.isPresent(), "User must be created to be updated.");
        User userToUpdate = createdUserOpt.get();

        userToUpdate.setFirstName("Johnny");
        userToUpdate.setMobile("+61499999999");
        userToUpdate.setPassword("newPassword456");

        // Act
        userDAO.updateUser(userToUpdate);

        // Assert
        Optional<User> updatedUserOpt = userDAO.findUserById(userToUpdate.getId());
        assertTrue(updatedUserOpt.isPresent(), "Updated user should be found by ID.");
        User updatedUser = updatedUserOpt.get();

        assertEquals("Johnny", updatedUser.getFirstName());
        assertEquals(testUser.getLastName(), updatedUser.getLastName());
        assertEquals(testUser.getEmail(), updatedUser.getEmail());
        assertEquals("+61499999999", updatedUser.getMobile());
        assertTrue(updatedUser.checkPassword("newPassword456"));
        assertFalse(updatedUser.checkPassword("password123"));
    }

    /**
     * Tests deleting a user by their ID.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testDeleteUser() throws SQLException {
        // Arrange
        userDAO.createUser(testUser);
        Optional<User> createdUserOpt = userDAO.findUserByEmail(testUser.getEmail());
        assertTrue(createdUserOpt.isPresent(), "User must exist to be deleted.");
        int userIdToDelete = createdUserOpt.get().getId();

        // Act
        userDAO.deleteUser(userIdToDelete);

        // Assert
        Optional<User> foundUserOpt = userDAO.findUserById(userIdToDelete);
        assertFalse(foundUserOpt.isPresent(), "User should not be found after deletion.");
    }

    /**
     * Tests retrieving all users from the database.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testGetAllUsers() throws SQLException {
        // Arrange
        User user1 = new User("Alice", "Smith", "alice@example.com", "+61411111111", "passA");
        User user2 = new User("Bob", "Jones", "bob@example.com", "+61422222222", "passB");
        userDAO.createUser(user1);
        userDAO.createUser(user2);

        // Act
        List<User> allUsers = userDAO.getAllUsers();

        // Assert
        assertEquals(2, allUsers.size(), "Should retrieve exactly 2 users.");
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("alice@example.com")), "List should contain Alice.");
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("bob@example.com")), "List should contain Bob.");
    }

    /**
     * Tests retrieving all users when the table is empty.
     *
     * @throws SQLException if database interaction fails.
     */
    @Test
    public void testGetAllUsersEmpty() throws SQLException {
        // Act
        List<User> allUsers = userDAO.getAllUsers();

        // Assert
        assertNotNull(allUsers, "getAllUsers should return an empty list, not null.");
        assertTrue(allUsers.isEmpty(), "User list should be empty when no users exist.");
    }
}
