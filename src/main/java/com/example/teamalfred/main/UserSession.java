package com.example.teamalfred.main;

import com.example.teamalfred.database.User;

/**
 * This class is used to store and access the currently logged-in user throughout the app.
 * It's kind of like a global session manager.
 *
 * We use the Singleton pattern here to make sure there's only ever one active session at a time.
 */
public class UserSession {

    // Singleton instance - there should only ever be one session at once
    private static UserSession instance;

    // The user that is currently logged in
    private static User loggedInUser = null;

    /**
     * Private constructor so that no one can create a session directly from outside.
     * We use initSession() to start a session properly.
     *
     * @param user The user that has just logged in.
     */
    private UserSession(User user) {
        // Store the logged-in user
        this.loggedInUser = user;
    }

    /**
     * Starts the session if it's not already started.
     * Should be called right after a successful login.
     *
     * @param user The user object representing the logged-in user.
     */
    public static void initSession(User user) {
        if (instance == null) {
            // Only set the session if it hasn't been started already
            instance = new UserSession(user);
        }
    }

    /**
     * Returns the current UserSession instance (if you need to check something about it).
     *
     * @return The active UserSession (can be null if no one is logged in).
     */
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Returns the user that is currently logged in.
     * This is super handy for checking who the current user is in any part of the app.
     *
     * @return The logged-in user (User object).
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Clears the current session, usually called during logout.
     * This will effectively "log out" the user.
     */
    public static void clearSession() {
        instance = null;
        loggedInUser = null;
    }
}
