package com.example.teamalfred.database;

import java.util.Objects; // Import Objects for equals and hashCode

/**
 * Represents a user within the system.
 * This class stores basic user information including identification,
 * name, contact details, and credentials.
 *
 * Note: The ID is not set via the constructor and should be assigned
 * separately, typically after persistence (e.g., database insertion).
 */
public class User {


    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password; // TODO: store as a hashed password

    /**
     * Constructs a new User instance.
     * The ID field is not initialised by this constructor.
     *
     * @param firstName The first name of the user. Must not be null.
     * @param lastName  The last name of the user. Must not be null.
     * @param email     The email address of the user. Should be unique and not null.
     * @param mobile    The mobile phone number of the user.
     * @param password  The user's password (plaintext). Consider security implications.
     */
    public User(String firstName, String lastName, String email, String mobile, String password) {
        // TODO: add validation for non-null parameters here
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        // 'id' remains uninitialised, default is 0
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique identifier for the user.
     *
     * @return The user's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     * This is typically called after the user is saved to a database.
     *
     * @param id The unique ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's email address.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        // TODO: Add email format validation
        this.email = email;
    }

    /**
     * Gets the user's mobile phone number.
     *
     * @return The mobile number.
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets the user's mobile phone number.
     *
     * @param mobile The mobile number to set.
     */
    public void setMobile(String mobile) {
        // TODO: Add mobile number format validation if needed
        this.mobile = mobile;
    }

    /**
     * Gets the user's password.
     *
     * @return The user's plaintext password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The plaintext password to set.
     */
    public void setPassword(String password) {
        // TODO: Hash the password here instead of storing plaintext.
        this.password = password;
    }

    // --- Utility Methods ---

    /**
     * Returns the user's full name, combining first and last name.
     *
     * @return The full name (e.g., "John Doe").
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Provides a string representation of the User object, primarily for logging or debugging.
     * Excludes sensitive information like the password.
     *
     * @return A string representation of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                // Password is intentionally excluded for security
                '}';
    }

    /**
     * Generates a hash code for the User object.
     * Based on the ID if set and non-zero, otherwise based on the email.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        // If ID is set and non-zero, use it for hashing
        if (id != 0) {
            return Objects.hash(id);
        }
        // Fallback to email if ID is not set or zero
        return Objects.hash(email);
    }
}
// TODO: Holds the data for  asingle user (firstName, lastName, etc). Keep as is.