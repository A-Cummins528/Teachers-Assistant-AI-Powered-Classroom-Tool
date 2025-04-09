package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseUserDAO implements IUserDAO {
    private Connection connection;

    public DatabaseUserDAO() {
        System.out.println("howdy 1");
        connection = DatabaseConnection.getInstance();
        createTable();
        insertSampleData();


    }

    private void insertSampleData() {
        try {
            Statement clearStatement = connection.createStatement();
            String clearQuery = "DELETE FROM users";
            clearStatement.execute(clearQuery);
            Statement insertStatement = connection.createStatement();
            String insertQuery = "INSERT INTO users (firstName, lastName, mobile, email, password, username) VALUES "
                    + "('Josh', 'Madams', '0412345678', 'josh@madams.com', 'password123', 'username1'), "
                    + "('Adam', 'Cummins', '0442115891', 'adams@cummins.com', 'password1234', 'username2'),"
                    + "('Philip', 'Mouton', '0433123456', 'philip@mouton.com', 'password12345', 'username3'),"
                    + "('Justin', 'Coglan', '0411555999', 'justin@coglan.com', 'password123456', 'username4'), "
                    + "('Felix', 'Nguyen', '0422555999', 'felix@nguyen.com', 'password1234567', 'username5')";
            insertStatement.execute(insertQuery);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void deleteTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "DROP TABLE users";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                    + "password VARCHAR NOT NULL,"
                    + "username VARCHAR NOT NULL UNIQUE"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
