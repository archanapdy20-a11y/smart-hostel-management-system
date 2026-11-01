package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.FoodFeedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodFeedbackDAO {

    public boolean submitFeedback(FoodFeedback fb) throws SQLException {
        // Enforce duplicate feedback prevention rule
        if (hasSubmittedFeedback(fb.getStudentId(), fb.getMenuDate(), fb.getMealType())) {
            throw new SQLException("You have already submitted feedback for " + fb.getMealType() + " on " + fb.getMenuDate() + "!");
        }

        String sql = "INSERT INTO food_feedback (student_id, menu_date, meal_type, rating, comments) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, fb.getStudentId());
            ps.setDate(2, fb.getMenuDate());
            ps.setString(3, fb.getMealType());
            ps.setInt(4, fb.getRating());
            ps.setString(5, fb.getComments());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    fb.setFeedbackId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasSubmittedFeedback(int studentId, Date date, String mealType) throws SQLException {
        String sql = "SELECT feedback_id FROM food_feedback WHERE student_id = ? AND menu_date = ? AND meal_type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, date);
            ps.setString(3, mealType);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<FoodFeedback> getAllFeedback() throws SQLException {
        List<FoodFeedback> list = new ArrayList<>();
        String sql = getFeedbackSelectQuery() + " ORDER BY fb.submitted_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapFeedback(rs));
            }
        }
        return list;
    }

    public List<FoodFeedback> getFeedbackByStudent(int studentId) throws SQLException {
        List<FoodFeedback> list = new ArrayList<>();
        String sql = getFeedbackSelectQuery() + " WHERE fb.student_id = ? ORDER BY fb.submitted_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFeedback(rs));
                }
            }
        }
        return list;
    }

    public double getAverageRating() throws SQLException {
        String sql = "SELECT COALESCE(AVG(rating), 0) FROM food_feedback";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Math.round(rs.getDouble(1) * 10.0) / 10.0;
            }
        }
        return 0.0;
    }

    private String getFeedbackSelectQuery() {
        return "SELECT fb.*, s.full_name AS student_name, s.register_number " +
               "FROM food_feedback fb " +
               "JOIN students s ON fb.student_id = s.student_id ";
    }

    private FoodFeedback mapFeedback(ResultSet rs) throws SQLException {
        FoodFeedback fb = new FoodFeedback();
        fb.setFeedbackId(rs.getInt("feedback_id"));
        fb.setStudentId(rs.getInt("student_id"));
        fb.setMenuDate(rs.getDate("menu_date"));
        fb.setMealType(rs.getString("meal_type"));
        fb.setRating(rs.getInt("rating"));
        fb.setComments(rs.getString("comments"));
        fb.setSubmittedAt(rs.getTimestamp("submitted_at"));
        fb.setStudentName(rs.getString("student_name"));
        fb.setRegisterNumber(rs.getString("register_number"));
        return fb;
    }
}
