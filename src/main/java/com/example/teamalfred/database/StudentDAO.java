package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.List;

public interface StudentDAO {
    void createStudent(Student student) throws SQLException;
    List<Student> getStudentsByClassId(int classId) throws SQLException;
    List<Student> getAllStudents() throws SQLException;
}

