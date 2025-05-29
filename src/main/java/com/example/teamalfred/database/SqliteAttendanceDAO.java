package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link AttendanceDAO} interface for SQLite.
 * Handles database operations related to attendance records.
 */
public class SqliteAttendanceDAO implements AttendanceDAO {

    /**
     * Saves or updates an attendance record for a given student and date.
     * If a record already exists for that student and date, it is replaced.
     *
     * @param studentId The ID of the student.
     * @param classId   The ID of the class.
     * @param date      The date of the attendance record (YYYY-MM-DD).
     * @param present   Whether the student was present.
     * @param absent    Whether the student was absent.
     * @param late      Whether the student was late.
     * @param excused   Whether the student's absence was excused.
     * @param notes     Any additional notes about the attendance.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void saveAttendance(int studentId, int classId, String date, boolean present, boolean absent, boolean late, boolean excused, String notes) throws SQLException {
        String sql = "INSERT OR REPLACE INTO attendance (student_id, class_id, date, present, absent, late, excused, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);
            stmt.setString(3, date);
            stmt.setBoolean(4, present);
            stmt.setBoolean(5, absent);
            stmt.setBoolean(6, late);
            stmt.setBoolean(7, excused);
            stmt.setString(8, notes);

            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a map of student IDs to their attendance records for a specific class and date.
     *
     * @param classId The ID of the class.
     * @param date    The date of attendance (YYYY-MM-DD).
     * @return A map where keys are student IDs and values are their {@link AttendanceRecord}.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Map<Integer, AttendanceRecord> getAttendanceMapForClassAndDate(int classId, String date) throws SQLException {
        String sql = "SELECT student_id, present, absent, late, excused, notes FROM attendance WHERE class_id = ? AND date = ?";
        Map<Integer, AttendanceRecord> map = new HashMap<>();

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                AttendanceRecord record = new AttendanceRecord(
                        rs.getBoolean("present"),
                        rs.getBoolean("absent"),
                        rs.getBoolean("late"),
                        rs.getBoolean("excused"),
                        rs.getString("notes")
                );
                map.put(studentId, record);
            }
        }

        return map;
    }

    /**
     * Counts the number of attendance records for a student in a given month where a specific status is true.
     * For example, counting how many times a student was absent or excused in a month.
     *
     * @param studentId    The ID of the student.
     * @param statusColumn The column name to filter by (e.g., "absent", "excused").
     * @param month        The month to check (as {@link YearMonth}).
     * @return The count of attendance records where the given status is true.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public int countByStatusInMonth(int studentId, String statusColumn, YearMonth month) throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND strftime('%Y-%m', date) = ? AND " + statusColumn + " = 1";
        String monthStr = month.toString();

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, monthStr);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
