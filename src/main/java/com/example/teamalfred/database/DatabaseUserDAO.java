package com.example.teamalfred.database;

import java.sql.*;

public class DatabaseUserDAO implements UserDAO {
    private Connection connection;
    private static String getUserByEmail = "SELECT * FROM users WHERE email = ?";

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
    // Implementation based on practical. Needs testing -Phil
    //    public void deleteUser(User user) {
    //        try {
    //            PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
    //            statement.setInt(1, user.getId());
    //            statement.executeUpdate();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    public void deleteUser(User user) {}
    // Update User Implementation based on practical. Working on testing. -Phil
    //    public void updateUser(User user) {
    //        try {
    //            PreparedStatement statement = connection.prepareStatement("UPDATE users SET firstName = ?, lastName = ?, mobile = ?, email = ? WHERE id = ?");
    //            statement.setString(1, user.getFirstName());
    //            statement.setString(2, user.getLastName());
    //            statement.setString(3, user.getMobile());
    //            statement.setString(4, user.getEmail());
    //            statement.setInt(5, user.getId());
    //            statement.executeUpdate();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    public void updateUser(User user) {}
    // Method to search for a user by email
    public User findUserByEmail(String email) {

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

    // add user to database method
    public void createUser(User user) {
        // insert user into database
        try {
            String insertQuery = "INSERT INTO users (firstName, lastName, mobile, email, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            System.out.println(user.getFirstName());
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getMobile());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.execute();
        } catch (SQLException e) {
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