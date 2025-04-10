package com.example.teamalfred.database;

import java.sql.*;

public class DatabaseUserDAO implements IUserDAO {
    private Connection connection;
    private static String getUserByEmail = "SELECT * FROM users WHERE email = ?";

    public DatabaseUserDAO() {
        System.out.println("howdy 1");
        connection = DatabaseConnection.getInstance();
        createTable();
        insertSampleData();


    }

    // creates sample users === FOR TESTING ONLY
    private void insertSampleData() {
        try {
            Statement clearStatement = connection.createStatement();
            String clearQuery = "DELETE FROM users";
            clearStatement.execute(clearQuery);
            Statement insertStatement = connection.createStatement();
            String insertQuery = "INSERT INTO users (firstName, lastName, mobile, email, password) VALUES "
                    + "('Josh', 'Madams', '0412345678', 'josh@madams.com', 'password123'), "
                    + "('Adam', 'Cummins', '0442115891', 'adams@cummins.com', 'password1234'),"
                    + "('Philip', 'Mouton', '0433123456', 'philip@mouton.com', 'password12345'),"
                    + "('Justin', 'Coglan', '0411555999', 'justin@coglan.com', 'password123456'), "
                    + "('Felix', 'Nguyen', '0422555999', 'felix@nguyen.com', 'password1234567')";
            insertStatement.execute(insertQuery);

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

    public void deleteUser(User user) {}

    public void updateUser(User user) {}

    // Method to search for a user by email
    public User getUser(String email) {

        try {
            // sets SQL query to user email search query
            PreparedStatement statement = connection.prepareStatement(getUserByEmail);
            // inserts the specific email to be searched for into query
            statement.setString(1, email);
            // stores result of query
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                // email found in db
                // new user object storing data associated with the found email
                User user = new User(resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("mobile"), resultSet.getString("email"), resultSet.getString("password"));
                return user;
            } else {
                // user email not found in db return null value
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }


    }

    public void addUser(User user) {
        // SQL query to insert data from user object
        String insertQuery = "INSERT INTO users (firstName, lastName, mobile, email, password) VALUES "
                + "('" + user.getFirstName() + "','" + user.getLastName() + "','" + user.getMobile() + "','" + user.getEmail() + "','" + user.getPassword() + "')";

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
