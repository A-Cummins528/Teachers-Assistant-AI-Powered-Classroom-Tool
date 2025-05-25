package com.example.teamalfred.database;

public class AttendanceRecord {
    public boolean present;
    public boolean absent;
    public boolean late;
    public boolean excused;
    public String notes;

    public AttendanceRecord(boolean present, boolean absent, boolean late, boolean excused, String notes) {
        this.present = present;
        this.absent = absent;
        this.late = late;
        this.excused = excused;
        this.notes = notes;
    }
}

