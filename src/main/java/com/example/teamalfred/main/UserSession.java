package com.example.teamalfred.main;

import com.example.teamalfred.database.User;

public class UserSession {
    private static UserSession instance;
    private static User loggedInUser = null;

    private UserSession(User user) {
        this.loggedInUser = user;
    }

    public static void initSession(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void clearSession() {
        instance = null;
    }
}
