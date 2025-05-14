package com.example.teamalfred.database;

public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int classId;

    public Student(int id, String firstName, String lastName, String email, int classId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.classId = classId;
    }

    // Getters and setters...

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getClassId() {
        return classId;
    }

    public int getId() {
        return id;
    }
}

