package com.example.teamalfred.database;

import java.sql.*;
import java.util.Optional;

public abstract class DatabaseUserDAO implements UserDAO {
    private Connection connection;
    private static String getUserByEmail = "SELECT * FROM users WHERE email = ?";

    @Override
    public void createUser(User user) throws SQLException {

    }

    @Override
    public void updateUser(User user) throws SQLException {

    }

    @Override
    public void deleteUser(int id) throws SQLException {

    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    public DatabaseUserDAO() {
        connection = DatabaseConnection.getInstance();

        //FOR TESTING ONLY - UNCOMMENT IF DB FILE IS EMPTY
        //createTable();
        //insertSampleData();
        //deleteTable();
        //createTable();

    }

    // creates sample users === FOR TESTING ONLY
    private void insertSampleData(boolean populate) {
        try {
            Statement clearStatement = connection.createStatement();
            String clearQuery = "DELETE FROM users";
            clearStatement.execute(clearQuery);
            Statement insertStatement = connection.createStatement();
            if(populate) {
                String insertQuery = "INSERT INTO users (firstName, lastName, mobile, email, password) VALUES "
                        + "('Joshh', 'Madams', '0412345678', 'josh@madams.com', 'password123'), "
                        + "('Adam', 'Cummins', '0442115891', 'adams@cummins.com', 'password1234'),"
                        + "('Philip', 'Mouton', '0433123456', 'philip@mouton.com', 'password12345'),"
                        + "('Justin', 'Coglan', '0411555999', 'justin@coglan.com', 'password123456'), "
                        + "('Felix', 'Nguyen', '0422555999', 'felix@nguyen.com', 'password1234567')";
                insertStatement.execute(insertQuery);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to delete the users table to reset == FOR TESTING ONLY
    private void deleteTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "DROP TABLE users";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    // Creates default EMPTY user table to start the database
    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            deleteTable();

            String query = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "firstName VARCHAR NOT NULL,"
                    + "lastName VARCHAR NOT NULL,"
                    + "mobile VARCHAR NOT NULL,"
                    + "email VARCHAR NOT NULL,"
                    + "password VARCHAR NOT NULL"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//TODO: This class has too many different responsibilities, begin moving out
// schema management, testing, searching, validation, etc into other classes
// until this class can be removed