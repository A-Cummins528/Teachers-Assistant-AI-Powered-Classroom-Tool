package com.example.teamalfred.main;

import com.example.teamalfred.database.User;

/**
 * Manages the session for the currently logged-in user using the Singleton pattern.
 * <p>
 * This class ensures there is only one active user session at a time, and provides
 * global access to session data throughout the application.
 */
public class UserSession {

    /** The singleton instance of the UserSession. */
    private static UserSession instance;

    /** The user currently logged in. */
    private static User loggedInUser = null;

    /**
     * Private constructor to prevent direct instantiation.
     * Use {@link #initSession(User)} to start a session.
     *
     * @param user The user who has logged in.
     */
    private UserSession(User user) {
        loggedInUser = user;
    }

    /**
     * Initializes the session for a logged-in user.
     * This method should be called once, right after a successful login.
     * Subsequent calls will have no effect if a session already exists.
     *
     * @param user The user object representing the logged-in user.
     */
    public static void initSession(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
    }

    /**
     * Returns the singleton instance of the active user session.
     *
     * @return The active {@code UserSession} instance, or {@code null} if no session exists.
     */
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Returns the user currently logged in.
     *
     * @return The {@code User} object representing the logged-in user, or {@code null} if no user is logged in.
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Updates the logged-in user for the session.
     * This can be used if user data changes and needs to be refreshed in the session.
     *
     * @param user The updated {@code User} object.
     */
    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Clears the current session and logs out the user.
     * This resets the session to its initial state.
     */
    public static void clearSession() {
        instance = null;
        loggedInUser = null;
    }
}
