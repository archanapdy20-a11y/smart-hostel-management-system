package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.Fee;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO {

    public int createFee(Fee f) throws SQLException {
        String sql = "INSERT INTO fees (student_id, academic_year, semester, fee_type, total_amount, " +
                     "paid_amount, pending_amount, due_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getStudentId());
            ps.setString(2, f.getAcademicYear());
            ps.setString(3, f.getSemester());
            ps.setString(4, f.getFeeType());
            ps.setBigDecimal(5, f.getTotalAmount());
            ps.setBigDecimal(6, f.getPaidAmount() != null ? f.getPaidAmount() : BigDecimal.ZERO);
            BigDecimal pending = f.getTotalAmount().subtract(f.getPaidAmount() != null ? f.getPaidAmount() : BigDecimal.ZERO);
            ps.setBigDecimal(7, pending);
            ps.setDate(8, f.getDueDate());
            ps.setString(9, pending.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : "UNPAID");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Fee getFeeById(int feeId) throws SQLException {
        String sql = getFeeSelectQuery() + " WHERE f.fee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFee(rs);
                }
            }
        }
        return null;
    }

    public List<Fee> getFeesByStudent(int studentId) throws SQLException {
        List<Fee> list = new ArrayList<>();
        String sql = getFeeSelectQuery() + " WHERE f.student_id = ? ORDER BY f.fee_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFee(rs));
                }
            }
        }
        return list;
    }

    public List<Fee> getAllFees(String statusFilter) throws SQLException {
        List<Fee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(getFeeSelectQuery());
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
            sql.append(" WHERE f.status = '").append(statusFilter).append("' ");
        }
        sql.append(" ORDER BY f.fee_id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapFee(rs));
            }
        }
        return list;
    }

    public List<Fee> searchFees(String keyword) throws SQLException {
        List<Fee> list = new ArrayList<>();
        String sql = getFeeSelectQuery() +
                     " WHERE s.full_name LIKE ? OR s.register_number LIKE ? " +
                     " OR f.academic_year LIKE ? OR f.semester LIKE ? " +
                     " ORDER BY f.fee_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFee(rs));
                }
            }
        }
        return list;
    }

    public FeeSummary getFeeSummary() throws SQLException {
        String sql = "SELECT " +
                     "COALESCE(SUM(total_amount), 0) AS total_fees, " +
                     "COALESCE(SUM(paid_amount), 0) AS collected_fees, " +
                     "COALESCE(SUM(pending_amount), 0) AS pending_fees " +
                     "FROM fees";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new FeeSummary(
                        rs.getBigDecimal("total_fees"),
                        rs.getBigDecimal("collected_fees"),
                        rs.getBigDecimal("pending_fees")
                );
            }
        }
        return new FeeSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static class FeeSummary {
        public final BigDecimal totalFees;
        public final BigDecimal collectedFees;
        public final BigDecimal pendingFees;

        public FeeSummary(BigDecimal totalFees, BigDecimal collectedFees, BigDecimal pendingFees) {
            this.totalFees = totalFees;
            this.collectedFees = collectedFees;
            this.pendingFees = pendingFees;
        }
    }

    private String getFeeSelectQuery() {
        return "SELECT f.*, s.full_name AS student_name, s.register_number " +
               "FROM fees f " +
               "JOIN students s ON f.student_id = s.student_id ";
    }

    private Fee mapFee(ResultSet rs) throws SQLException {
        Fee f = new Fee();
        f.setFeeId(rs.getInt("fee_id"));
        f.setStudentId(rs.getInt("student_id"));
        f.setAcademicYear(rs.getString("academic_year"));
        f.setSemester(rs.getString("semester"));
        f.setFeeType(rs.getString("fee_type"));
        f.setTotalAmount(rs.getBigDecimal("total_amount"));
        f.setPaidAmount(rs.getBigDecimal("paid_amount"));
        f.setPendingAmount(rs.getBigDecimal("pending_amount"));
        f.setDueDate(rs.getDate("due_date"));
        f.setStatus(rs.getString("status"));
        f.setStudentName(rs.getString("student_name"));
        f.setRegisterNumber(rs.getString("register_number"));
        return f;
    }
}
