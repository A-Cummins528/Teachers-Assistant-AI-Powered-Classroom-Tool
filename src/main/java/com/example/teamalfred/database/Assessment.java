package com.example.teamalfred.database;

public class Assessment {
    private int id;
    private String title;
    private String subject;
    private String dueDate;
    private String status;
    private String type;

    public Assessment(String title, String subject, String dueDate, String status, String type) {
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.status = status;
        this.type = type;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
