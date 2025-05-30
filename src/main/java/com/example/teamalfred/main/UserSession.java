package com.example.teamalfred.main;

import com.example.teamalfred.database.User;

/**
 * Manages the session for the currently logged-in user using the Singleton pattern.
 * <p>
 * This class provides global access to the active user session throughout the application,
 * ensuring that only one session exists at a time.
 */
public class UserSession {

    /** The singleton instance of the user session. */
    private static UserSession instance;

    /** The user currently logged in. */
    private static User loggedInUser = null;

    /**
     * Constructs a new {@code UserSession} with the specified user.
     * This constructor is private to enforce the singleton pattern.
     *
     * @param user the user who has logged in
     */
    private UserSession(User user) {
        loggedInUser = user;
    }

    /**
     * Initializes the user session if one is not already active.
     * This method should be called immediately after a successful login.
     *
     * @param user the user who has logged in
     */
    public static void initSession(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
    }

    /**
     * Returns the singleton instance of the active user session.
     *
     * @return the active {@code UserSession} instance, or {@code null} if no session is active
     */
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the {@code User} object representing the logged-in user, or {@code null} if no user is logged in
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Updates the logged-in user for the current session.
     * This can be used to refresh session data if the user's state changes.
     *
     * @param user the updated {@code User} object
     */
    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Clears the current user session, typically called during logout.
     * This method resets the session and removes all associated user data.
     */
    public static void clearSession() {
        instance = null;
        loggedInUser = null;
    }
}
