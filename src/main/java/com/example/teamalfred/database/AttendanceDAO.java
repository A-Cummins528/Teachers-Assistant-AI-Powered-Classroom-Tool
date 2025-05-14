package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.Map;

public interface AttendanceDAO {

    void saveAttendance(
            int studentId,
            int classId,
            String date,
            boolean present,
            boolean absent,
            boolean late,
            boolean excused,
            String notes
    ) throws SQLException;
    Map<Integer, AttendanceRecord> getAttendanceMapForClassAndDate(int classId, String date) throws SQLException;

    int countByStatusInMonth(int studentId, String statusColumn, java.time.YearMonth month) throws SQLException;

}