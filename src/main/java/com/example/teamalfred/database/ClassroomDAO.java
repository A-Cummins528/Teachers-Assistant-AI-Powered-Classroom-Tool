package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.List;

public interface ClassroomDAO {
    void createClassroom(Classroom classroom) throws SQLException;
    List<Classroom> getAllClassrooms() throws SQLException;
    Classroom getClassroomById(int classId) throws SQLException;
}
