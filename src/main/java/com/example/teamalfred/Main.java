package com.example.teamalfred;
import com.example.teamalfred.database.DatabaseConnection;

import java.sql.Connection;


public class Main {

    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getInstance();
    }
}
