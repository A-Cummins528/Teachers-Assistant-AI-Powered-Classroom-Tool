package com.example.teamalfred.database;
import com.example.teamalfred.database.Student;
import com.example.teamalfred.database.SqliteStudentDAO;

import java.sql.SQLException;
import java.util.Random;

public class TestStudentDataGenerator {

    private static final String[] FIRST_NAMES = {
            "Alice", "Bob", "Charlie", "Diana", "Edward",
            "Fiona", "George", "Hannah", "Isaac", "Julia"
    };

    private static final String[] LAST_NAMES = {
            "Johnson", "Smith", "Nguyen", "Williams", "Brown",
            "Jones", "Garcia", "Miller", "Davis", "Wilson"
    };

    private static final Random random = new Random();

    public static void generateStudents(int count, int classId) {
        SqliteStudentDAO studentDAO = new SqliteStudentDAO();

        for (int i = 0; i < count; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@school.com";

            Student student = new Student(0, firstName, lastName, email, classId);
            try {
                studentDAO.createStudent(student);
                System.out.println("Inserted: " + student.getFullName());
            } catch (SQLException e) {
                System.err.println("Error inserting student: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        generateStudents(20, 1); // Generate 20 students for class_id = 1
    }
}
