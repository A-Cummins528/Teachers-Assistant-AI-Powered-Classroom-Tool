package com.example.teamalfred.database;

import com.example.teamalfred.database.User;
import com.example.teamalfred.database.UserDAO;

import java.sql.Connection; // Import required JDBC classes
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class implements the UserDAO interface specifically for a SQLite database.
 * Contains SQL queries and JDBC code to interact with the user table.
 */
public class SqliteUserDAO implements UserDAO {

    // --- Constants for Column Names ---
    private static final String TABLE_NAME = "users";
    private static final String COL_ID = "id";
    private static final String COL_FIRST_NAME = "firstName";
    private static final String COL_LAST_NAME = "lastName";
    private static final String COL_MOBILE = "mobile";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_USER_TYPE = "userType";
    private static final String COL_GRADE = "grade";
    private static final String COL_CLASS_NAME = "className";


    /**
     * Creates a new user record in the database.
     * @param user The user object containing data for the new record.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public void createUser(User user) throws SQLException {
        // Exclude the grade and className columns from the INSERT statement
        String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
                TABLE_NAME, COL_FIRST_NAME, COL_LAST_NAME, COL_MOBILE, COL_EMAIL, COL_PASSWORD,
                COL_USER_TYPE);

        // Get connection for this operation
        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            throw new SQLException("Database connection could not be established.");
        }

        // Check if email is already registered
        if (findUserByEmail(user.getEmail()).isPresent()) {
            throw new SQLException("A user with this email already exists.");
        }

        // Use try-with-resources for PreparedStatement (automatically closes statement)
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameters based on the User object
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getMobile());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            // Convert userType to lowercase to match database constraint
            pstmt.setString(6, user.getUserType().name().toLowerCase());

            // Execute the insert
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing user's details in the database.
     * Assumes the user object contains the ID of the user to update.
     * @param user The user object containing updated data.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public void updateUser(User user) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE_NAME, COL_FIRST_NAME, COL_LAST_NAME, COL_MOBILE, COL_EMAIL, COL_PASSWORD,
                COL_USER_TYPE, COL_GRADE, COL_CLASS_NAME, COL_ID);

        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            throw new SQLException("Database connection could not be established.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getMobile());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getUserType().toString().toLowerCase());
            pstmt.setString(7, user.getGrade());
            pstmt.setString(8, user.getClassName());
            pstmt.setInt(9, user.getId()); // Use the ID for the WHERE clause

            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a user from the database based on their ID.
     * @param id The unique ID of the user to delete.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public void deleteUser(int id) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, COL_ID);

        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            throw new SQLException("Database connection could not be established.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Use the 'id' parameter passed to the method
            pstmt.setInt(1, id);

            pstmt.executeUpdate();
        }
    }

    /**
     * Finds a user by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the found User, or an empty Optional if no user with that email exists.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Optional<User> findUserByEmail(String email) throws SQLException {
        // Select all columns needed to reconstruct a User object
        String sql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                COL_ID, COL_FIRST_NAME, COL_LAST_NAME, COL_MOBILE, COL_EMAIL, COL_PASSWORD,
                COL_USER_TYPE, COL_GRADE, COL_CLASS_NAME, TABLE_NAME, COL_EMAIL);

        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            throw new SQLException("Database connection could not be established.");
        }


        // Use try-with-resources for PreparedStatement and ResultSet
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If a user is found, create and populate a User object
                    User user = mapResultSetToUser(rs); // Use a helper method
                    return Optional.of(user); // Return Optional containing the user
                }
            }
        }
        // If no user was found or an error occurred before returning, return empty
        return Optional.empty();
    }

    /**
     * Finds a user by their unique ID.
     * @param id The unique ID of the user to find.
     * @return An Optional containing the found User, or an empty Optional if no user with that ID exists.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Optional<User> findUserById(int id) throws SQLException {
        String sql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?",
                COL_ID, COL_FIRST_NAME, COL_LAST_NAME, COL_MOBILE, COL_EMAIL, COL_PASSWORD,
                COL_USER_TYPE, COL_GRADE, COL_CLASS_NAME, TABLE_NAME, COL_ID);

        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            throw new SQLException("Database connection could not be established.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id); // Set the ID parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs); // Use helper method
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves a list of all users from the database.
     * @return A List of all User objects; the list may be empty if there are no users.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        // Select all columns needed
        String sql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s",
                COL_ID, COL_FIRST_NAME, COL_LAST_NAME, COL_MOBILE, COL_EMAIL, COL_PASSWORD,
                COL_USER_TYPE, COL_GRADE, COL_CLASS_NAME, TABLE_NAME);

        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            throw new SQLException("Database connection could not be established.");
        }

        // Use try-with-resources for Statement and ResultSet
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop through all rows in the result set
            while (rs.next()) {
                User user = mapResultSetToUser(rs); // Use helper method
                users.add(user);
            }
        }
        return users; // Return the list (might be empty)
    }

    // --- Helper Method ---

    /**
     * Maps the current row of a ResultSet to a User object.
     * @param rs The ResultSet, positioned at a valid row.
     * @return A User object populated with data from the ResultSet.
     * @throws SQLException if a column label is invalid or data retrieval error occurs.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt(COL_ID));
        user.setFirstName(rs.getString(COL_FIRST_NAME));
        user.setLastName(rs.getString(COL_LAST_NAME));
        user.setMobile(rs.getString(COL_MOBILE));
        user.setEmail(rs.getString(COL_EMAIL));
        String hashedPasswordFromDb = rs.getString(COL_PASSWORD);
        user.setPersistedPassword(hashedPasswordFromDb);
        User.UserRole.valueOf(rs.getString(COL_USER_TYPE).toUpperCase()); // this supposedly does nothing. because you reference it as the one below elsewhere.
        user.setUserType(rs.getString(COL_USER_TYPE));
        user.setGrade(rs.getString(COL_GRADE));
        user.setClassName(rs.getString(COL_CLASS_NAME));
        return user;
    }
}