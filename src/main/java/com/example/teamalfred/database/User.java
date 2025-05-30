package com.example.teamalfred.database;

import java.util.Objects;// Import Objects for equals and hashCode
import org.mindrot.jbcrypt.BCrypt;

/**
 * User Class, Represents a user as object within the system.
 * Passwords are stored in a hashed format using BCrypt.
 * Also used within User Session as primary token
 */
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password; // This will store the hashed password
    private String grade;    // Student's grade, null for teachers
    private String className; // e.g., "CAB302"

    // Define the UserRole enum
    public enum UserRole {
        STUDENT, TEACHER;
    }
    private UserRole userType;


    /**
     * Constructs a new User instance. Hashes the provided password.
     * The ID field is not initialised by this constructor.
     *
     * @param firstName The first name of the user. Must not be null.
     * @param lastName  The last name of the user. Must not be null.
     * @param email     The email address of the user. Should be unique and not null.
     * @param mobile    The mobile phone number of the user.
     * @param plainTextPassword  The user's plaintext password (will be hashed).
     * @param userTypeString  The type of user as a String (e.g., "student", "teacher").
     */
    public User(String firstName, String lastName, String email, String mobile, String plainTextPassword,
                String userTypeString) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setMobile(mobile);
        setPassword(plainTextPassword);
        setUserType(userTypeString);
        // 'id', 'grade', and 'class name' remain uninitialised
    }

    /**
     * Default constructor. Needed for frameworks or manual instantiation
     * before setting fields via setters.
     */
    public User() {
        // No-argument constructor
    }


    // --- Getters ---

    /**
     * Gets the unique identifier of the user.
     * @return The user's ID.
     */
    public int getId() { return id; }

    /**
     * Gets the first name of the user.
     * @return The user's first name.
     */
    public String getFirstName() { return firstName; }

    /**
     * Gets the last name of the user.
     * @return The user's last name.
     */
    public String getLastName() { return lastName; }

    /**
     * Gets the email address of the user.
     * @return The user's email address.
     */
    public String getEmail() { return email; }

    /**
     * Gets the mobile phone number of the user.
     * @return The user's mobile number.
     */
    public String getMobile() { return mobile; }

    /**
     * Gets the grade of the student. Returns null for teachers or if not set.
     * @return The student's grade, or null.
     */
    public String getGrade() { return grade; }

    /**
     * Gets the class name associated with the user (e.g., "CAB302").
     * @return The class name, or null if not set.
     */
    public String getClassName() { return className; }

    /**
     * Gets the type of the user (e.g., STUDENT, TEACHER).
     * @return The user's role as a UserRole enum.
     */
    public UserRole getUserType() { return userType; } // Returns the UserRole enum


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

    /**
     * Sets the unique identifier for the user.
     * Typically used when loading a user from a persistent store.
     * @param id The user's ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets the first name of the user.
     * @param firstName The user's first name. Cannot be null or empty.
     * @throws IllegalArgumentException if firstName is null or empty.
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty.");
        }
        this.firstName = firstName.trim();
    }

    /**
     * Sets the last name of the user.
     * @param lastName The user's last name. Cannot be null or empty.
     * @throws IllegalArgumentException if lastName is null or empty.
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty.");
        }
        this.lastName = lastName.trim();
    }

    /**
     * Sets the email address of the user.
     * @param email The user's email address. Must be a valid format and not null or empty.
     * @throws IllegalArgumentException if email is null, empty, or invalid format.
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email.trim();
    }

    /**
     * Sets the mobile phone number of the user.
     * @param phone The user's mobile number. Must be a valid format and not null or empty.
     * @throws IllegalArgumentException if mobile number is null, empty, or invalid format.
     */
    public void setMobile(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty.");
        }
        if (!phone.matches("^\\+?\\d{7,15}$")) {
            // push thru
            throw new IllegalArgumentException("Invalid mobile number format.");
        }
        this.mobile = phone.trim();
    }

    /**
     * Sets the user type from a String.
     * Converts the string to UserRole enum.
     *
     * @param userTypeString The user type as a string (e.g., "student", "teacher").
     * @throws IllegalArgumentException if the string is null, empty, or not a valid user type.
     */
    public void setUserType(String userTypeString) {
        if (userTypeString == null || userTypeString.trim().isEmpty()) {
            throw new IllegalArgumentException("User type string cannot be null or empty.");
        }
        try {
            UserRole role = UserRole.valueOf(userTypeString.trim().toUpperCase());
            setUserType(role); // Call the setter that takes UserRole enum
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user type string: '" + userTypeString + "'. Must be 'STUDENT' or 'TEACHER' (case-insensitive).", e);
        }
    }

    /**
     * Sets the user type using the UserRole enum.
     *
     * @param userType The UserRole enum value.
     * @throws IllegalArgumentException if userType is null.
     */
    public void setUserType(UserRole userType) {
        if (userType == null) {
            throw new IllegalArgumentException("User type (UserRole) cannot be null.");
        }
        this.userType = userType;
    }

    /**
     * Sets the grade for the student. Can be null.
     * @param grade The student's grade.
     */
    public void setGrade(String grade) {
        // Grade can be null or empty if not applicable (e.g., for teachers)
        // or if a student doesn't have a grade assigned yet.
        this.grade = (grade == null) ? null : grade.trim();
    }

    /**
     * Sets the class name associated with the user. Can be null.
     * @param className The class name (e.g., "CAB302").
     */
    public void setClassName(String className) {
        // Class name can be null or empty if not assigned.
        this.className = (className == null) ? null : className.trim();
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
                ", userType='" + userType + '\'' +
                ", grade='" + grade + '\'' +
                ", className='" + className + '\'' +
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
     * For a User, we decide two User objects are logically the same if they have the same id.</p>
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