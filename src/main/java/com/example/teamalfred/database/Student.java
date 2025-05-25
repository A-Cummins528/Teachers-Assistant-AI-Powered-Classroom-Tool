package com.example.teamalfred.database;

/**
 * This class represents a student in the system.
 * It's used as a data model to store info like name, email, and what class they're in.
 */
public class Student {
    private int id;             // Unique ID for the student (like a primary key in the database)
    private String firstName;   // First name of the student
    private String lastName;    // Last name of the student
    private String email;       // Contact email
    private int classId;        // ID of the class/group this student is part of
    private String subject;
    /**
     * Constructor to create a Student object with all relevant data.
     *
     * @param id        Unique student ID
     * @param firstName Student's first name
     * @param lastName  Student's last name
     * @param email     Student's email address
     * @param classId   ID of the class this student belongs to
     * @param subject   Subject of the Student
     */
    public Student(int id, String firstName, String lastName, String email, int classId, String subject) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subject = subject;
        this.classId = classId;
    }
    public Student(String firstName, String lastName, String email, int classId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.classId = classId;
    }


    public Student(int studentId, String firstName, String lastName, String email, int classId) {
    }

    /**
     * Gets the full name of the student by combining first and last name.
     *
     * @return The student's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * @return The student's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return The student's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return The student's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return The ID of the class this student belongs to
     */
    public int getClassId() {
        return classId;
    }
    /**
     * @return Subject of the Student
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return The student's unique ID
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}


