package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.Payment;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // Records a student fee payment and updates fee balance transactionally
    public boolean recordPayment(Payment p) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.createNewConnection();
            conn.setAutoCommit(false); // Transaction begin

            // 1. Fetch fee record with pessimistic lock
            BigDecimal totalAmount, currentPaid, currentPending;
            String fetchFeeSql = "SELECT total_amount, paid_amount, pending_amount FROM fees WHERE fee_id = ? FOR UPDATE";
            try (PreparedStatement psFetch = conn.prepareStatement(fetchFeeSql)) {
                psFetch.setInt(1, p.getFeeId());
                try (ResultSet rs = psFetch.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Fee record not found for Fee ID: " + p.getFeeId());
                    }
                    totalAmount = rs.getBigDecimal("total_amount");
                    currentPaid = rs.getBigDecimal("paid_amount");
                    currentPending = rs.getBigDecimal("pending_amount");
                }
            }

            if (p.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
                throw new SQLException("Payment amount must be greater than zero!");
            }
            if (p.getAmountPaid().compareTo(currentPending) > 0) {
                throw new SQLException("Payment amount (\u20B9 " + p.getAmountPaid() + ") cannot exceed pending amount (\u20B9 " + currentPending + ")!");
            }

            // 2. Generate unique receipt number if not provided
            if (p.getReceiptNumber() == null || p.getReceiptNumber().trim().isEmpty()) {
                String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
                p.setReceiptNumber("REC-" + timestamp + "-" + (int)(Math.random() * 900 + 100));
            }

            // 3. Insert payment record
            String insertPaymentSql = "INSERT INTO payments (fee_id, student_id, amount_paid, payment_date, " +
                                      "payment_method, transaction_id, recorded_by, receipt_number, remarks) " +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psInsert = conn.prepareStatement(insertPaymentSql, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, p.getFeeId());
                psInsert.setInt(2, p.getStudentId());
                psInsert.setBigDecimal(3, p.getAmountPaid());
                psInsert.setDate(4, p.getPaymentDate() != null ? p.getPaymentDate() : new Date(System.currentTimeMillis()));
                psInsert.setString(5, p.getPaymentMethod());
                psInsert.setString(6, p.getTransactionId());
                if (p.getRecordedBy() != null && p.getRecordedBy() > 0) {
                    psInsert.setInt(7, p.getRecordedBy());
                } else {
                    psInsert.setNull(7, Types.INTEGER);
                }
                psInsert.setString(8, p.getReceiptNumber());
                psInsert.setString(9, p.getRemarks());
                psInsert.executeUpdate();
            }

            // 4. Update fee record
            BigDecimal newPaid = currentPaid.add(p.getAmountPaid());
            BigDecimal newPending = currentPending.subtract(p.getAmountPaid());
            String newStatus = newPending.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : "PARTIALLY_PAID";

            String updateFeeSql = "UPDATE fees SET paid_amount = ?, pending_amount = ?, status = ? WHERE fee_id = ?";
            try (PreparedStatement psUp = conn.prepareStatement(updateFeeSql)) {
                psUp.setBigDecimal(1, newPaid);
                psUp.setBigDecimal(2, newPending);
                psUp.setString(3, newStatus);
                psUp.setInt(4, p.getFeeId());
                psUp.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public List<Payment> getPaymentsByStudent(int studentId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = getPaymentSelectQuery() + " WHERE p.student_id = ? ORDER BY p.payment_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPayment(rs));
                }
            }
        }
        return list;
    }

    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = getPaymentSelectQuery() + " ORDER BY p.payment_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapPayment(rs));
            }
        }
        return list;
    }

    public List<Payment> getRecentPayments(int limit) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = getPaymentSelectQuery() + " ORDER BY p.payment_id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPayment(rs));
                }
            }
        }
        return list;
    }

    public List<Payment> searchPayments(String keyword) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = getPaymentSelectQuery() +
                     " WHERE s.full_name LIKE ? OR s.register_number LIKE ? " +
                     " OR p.receipt_number LIKE ? OR p.transaction_id LIKE ? " +
                     " ORDER BY p.payment_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPayment(rs));
                }
            }
        }
        return list;
    }

    private String getPaymentSelectQuery() {
        return "SELECT p.*, " +
               "s.full_name AS student_name, s.register_number, " +
               "f.semester AS fee_semester, " +
               "u.full_name AS recorded_by_name " +
               "FROM payments p " +
               "JOIN students s ON p.student_id = s.student_id " +
               "JOIN fees f ON p.fee_id = f.fee_id " +
               "LEFT JOIN users u ON p.recorded_by = u.user_id ";
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setFeeId(rs.getInt("fee_id"));
        p.setStudentId(rs.getInt("student_id"));
        p.setAmountPaid(rs.getBigDecimal("amount_paid"));
        p.setPaymentDate(rs.getDate("payment_date"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setTransactionId(rs.getString("transaction_id"));
        int recBy = rs.getInt("recorded_by");
        p.setRecordedBy(rs.wasNull() ? null : recBy);
        p.setReceiptNumber(rs.getString("receipt_number"));
        p.setRemarks(rs.getString("remarks"));

        p.setStudentName(rs.getString("student_name"));
        p.setRegisterNumber(rs.getString("register_number"));
        p.setFeeSemester(rs.getString("fee_semester"));
        p.setRecordedByName(rs.getString("recorded_by_name"));
        return p;
    }
}
