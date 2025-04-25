package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for User Data Access Object (DAO) operations.
 * Lists what can be done with User data, decoupling the application
 * from the specific database implementation.
 */
public interface UserDAO {

    /**
     * Creates a new user record in the database.
     * @param user The user object containing data for the new record.
     * @throws SQLException if a database access error occurs.
     */
    void createUser(User user) throws SQLException;

    /**
     * Updates an existing user's details in the database.
     * Assumes the user object contains the ID of the user to update.
     * @param user The user object containing updated data.
     * @throws SQLException if a database access error occurs.
     */
    void updateUser(User user) throws SQLException;

    /**
     * Deletes a user from the database based on their ID.
     * @param id The unique ID of the user to delete.
     * @throws SQLException if a database access error occurs.
     */
    void deleteUser(int id) throws SQLException;

    /**
     * Finds a user by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the found User, or an empty Optional if no user with that email exists.
     * @throws SQLException if a database access error occurs.
     */
    Optional<User> findUserByEmail(String email) throws SQLException;

    /**
     * Finds a user by their unique ID.
     * @param id The unique ID of the user to find.
     * @return An Optional containing the found User, or an empty Optional if no user with that ID exists.
     * @throws SQLException if a database access error occurs.
     */
    Optional<User> findUserById(int id) throws SQLException;

    /**
     * Retrieves a list of all users from the database.
     * @return A List of all User objects; the list may be empty if there are no users.
     * @throws SQLException if a database access error occurs.
     */
    List<User> getAllUsers() throws SQLException;
}