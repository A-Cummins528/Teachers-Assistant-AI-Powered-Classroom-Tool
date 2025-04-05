package com.example.teamalfred;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/// This class is responsible for creating a connection to the SQLite database
public class DatabaseConnection {
    private static Connection instance = null;

    private DatabaseConnection() {
        String url = "jdbc:sqlite:database.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new DatabaseConnection();
        }
        return instance;
    }
}
