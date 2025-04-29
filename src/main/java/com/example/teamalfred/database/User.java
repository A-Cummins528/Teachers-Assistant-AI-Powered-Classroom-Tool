package com.example.teamalfred.database;

import java.util.Objects; // Import Objects for equals and hashCode
import org.mindrot.jbcrypt.BCrypt;

/**
 * Represents a user within the system.
 * Passwords are stored in a hashed format using BCrypt.
 */
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password; // This will store the hashed password

    /**
     * Constructs a new User instance. Hashes the provided password.
     * The ID field is not initialised by this constructor.
     *
     * @param firstName The first name of the user. Must not be null.
     * @param lastName  The last name of the user. Must not be null.
     * @param email     The email address of the user. Should be unique and not null.
     * @param mobile    The mobile phone number of the user.
     * @param plainTextPassword  The user's plaintext password (will be hashed).
     */
    public User(String firstName, String lastName, String email, String mobile, String plainTextPassword) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setMobile(mobile);
        setPassword(plainTextPassword); // Hash the password immediately upon creation
        // 'id' remains uninitialised
    }

    /**
     * Default constructor. Needed for frameworks or manual instantiation
     * before setting fields via setters.
     */
    public User() {
        // No-argument constructor
    }




    // --- Getters ---

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }

    /**
     * Gets the stored password hash.
     * Note: This returns the HASH, not the original plaintext password.
     *
     * @return The BCrypt password hash string.
     */
    public String getPassword() {
        return password;
    }



    // --- Setters ---

    public void setId(int id) { this.id = id; }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty.");
        }
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty.");
        }
        this.lastName = lastName.trim();
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email.trim();
    }

    public void setMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty.");
        }
        if (!mobile.matches("^\\+?\\d{7,15}$")) {
            throw new IllegalArgumentException("Invalid mobile number format.");
        }
        this.mobile = mobile.trim();
    }


    /**
     * Hashes the provided plaintext password using BCrypt and stores the hash.
     *
     * @param plainTextPassword The plaintext password to hash and store.
     */
    public void setPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            // Password must not be null
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        // Hash the password using BCrypt with a generated salt
        // BCrypt.gensalt() generates a salt; hashpw combines password and salt
        this.password = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Sets the password field directly with a pre-hashed value.
     * This is typically used only when loading user data from the database.
     * Avoid calling this with plaintext passwords.
     *
     * @param hashedPassword The already hashed password string from the database.
     */
    public void setPersistedPassword(String hashedPassword) {
        // Directly set the internal field with the value from the DB
        this.password = hashedPassword;
    }




    // --- Password Checking ---

    /**
     * Checks if the provided plaintext password matches the stored hash.
     *
     * @param plainTextPasswordToCheck The plaintext password attempt (e.g., from a login form).
     * @return true if the provided password matches the stored hash, false otherwise.
     */
    public boolean checkPassword(String plainTextPasswordToCheck) {
        if (plainTextPasswordToCheck == null || this.password == null) {
            return false; // Cannot check against nulls
        }
        // BCrypt.checkpw compares the plaintext against the stored hash
        // It extracts the salt from this.password automatically
        return BCrypt.checkpw(plainTextPasswordToCheck, this.password);
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
     * This method returns an integer representation (a hash code) of the object's state.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * This method defines what it means for two distinct objects (in this case, two User objects)
     * to be considered "logically equivalent". By default, Java's equals (inherited from the Object class)
     * only returns true if two variables point to the exact same object in memory.
     * <p> We override equals() to provide our own definition based on the object's state (its fields).
     *  For a User, we decide two User objects are logically the same if they have the same id.</p>
     *
     * @param o A User object
     * @return True if both User IDs are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Assumes non-zero ID means it's a valid, comparable user
        return id != 0 && id == user.id;
    }
}