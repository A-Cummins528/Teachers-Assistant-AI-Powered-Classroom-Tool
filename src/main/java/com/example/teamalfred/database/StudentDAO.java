package com.example.teamalfred.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.example.teamalfred.database.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface StudentDAO {
    void createStudent(Student student) throws SQLException;
    List<Student> getStudentsByClassId(int classId) throws SQLException;
    List<Student> getAllStudents() throws SQLException;
}


