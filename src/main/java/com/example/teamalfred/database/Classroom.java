package com.example.teamalfred.database;

public class Classroom {
    private int id;
    private String name;

    public Classroom(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Classroom(String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name; // Useful for ComboBox display
    }
}

