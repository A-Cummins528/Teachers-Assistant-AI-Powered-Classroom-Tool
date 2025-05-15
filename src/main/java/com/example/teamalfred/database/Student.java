package com.example.teamalfred.database;

public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;

    public Student(int id, String firstName, String lastName, String email, String subject) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subject = subject;
    }

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

    public String getSubject() {
        return subject;
    }

    public int getId() {
        return id;
    }
}


