package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.Map;

public interface AttendanceDAO {

    /**
     *
     * @param studentId
     * @param classId
     * @param date
     * @param present
     * @param absent
     * @param late
     * @param excused
     * @param notes
     * @throws SQLException
     */
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

    /**
     *
     * @param classId
     * @param date
     * @return
     * @throws SQLException
     */
    Map<Integer, AttendanceRecord> getAttendanceMapForClassAndDate(int classId, String date) throws SQLException;

    /**
     *
     * @param studentId
     * @param statusColumn
     * @param month
     * @return
     * @throws SQLException
     */
    int countByStatusInMonth(int studentId, String statusColumn, java.time.YearMonth month) throws SQLException;

}