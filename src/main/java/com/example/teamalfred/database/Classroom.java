package com.example.teamalfred.database;

/**
 * Represents a Classroom entity from the database.
 * This class holds basic info about a classroom such as its unique ID and its name.
 * Used to model classroom data in the program.
 */
public class Classroom {
    // Unique identifier for the classroom (usually from DB primary key)
    private int id;
    // The name of the classroom (e.g., "Math 101", "Science Lab")
    private String name;

    /**
     * Constructor to create a Classroom object with both id and name.
     * Useful when you already know the classroom ID (e.g., loading from DB).
     *
     * @param id The unique ID of the classroom
     * @param name The name of the classroom
     */
    public Classroom(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor to create a Classroom with just the name.
     * ID may be assigned later (e.g., after inserting into DB).
     *
     * @param name The name of the classroom
     */
    public Classroom(String name) {
        this.name = name;
    }

    /**
     * Gets the ID of the classroom.
     * @return The classroom's ID as an int
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the classroom.
     * @return The classroom's name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the ID of the classroom.
     * Typically used when the ID is assigned by the database.
     *
     * @param id The ID to set for this classroom
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the name of the classroom.
     *
     * @param name The new name for this classroom
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Overridden toString method to return the classroom name.
     * This is very useful when displaying Classroom objects in UI components
     * like ComboBoxes or Lists, where the name should be shown instead of
     * the default object reference string.
     *
     * @return The classroom name as a String
     */
    @Override
    public String toString() {
        return name;
    }

    public String getClassName() {
        return name;
    }
}
