package com.example.teamalfred.database;

import java.sql.SQLException;
import java.util.List;

public interface AssessmentDAO {
    List<Assessment> getAllAssessments() throws SQLException;
}