package com.example.teamalfred.database;

/**
 * A simple class representing a user with a first name, last name, email, mobile, and password.
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password;

    /**
     * Constructs a new User with the specified first name, last name, email, mobile, and password.
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param email the email of the user
     * @param mobile the mobile number of the user
     * @param password the password for this user account
     */
    public User(String firstName, String lastName, String email, String mobile, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
    }

    // methods to set and get user variables

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setPassword(String password) {this.password = password; }

    public String getPassword() {return this.password;}

    public void printUserInfo() {
        System.out.println(this.firstName + this.lastName + " " + this.mobile + " " + this.email + " " + this.password);
    }
}
