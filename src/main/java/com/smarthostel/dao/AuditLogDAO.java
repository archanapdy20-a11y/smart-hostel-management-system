package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public void logAction(Integer userId, String username, String actionType,
                          String tableAffected, Integer recordId, String details) {
        String sql = "INSERT INTO audit_logs (user_id, username, action_type, table_affected, record_id, details) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null && userId > 0) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, username != null ? username : "SYSTEM");
            ps.setString(3, actionType);
            ps.setString(4, tableAffected);
            if (recordId != null && recordId > 0) {
                ps.setInt(5, recordId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Audit logging warning: " + e.getMessage());
        }
    }

    public List<AuditLog> getRecentLogs(int limit) throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog l = new AuditLog();
                    l.setLogId(rs.getInt("log_id"));
                    int uid = rs.getInt("user_id");
                    l.setUserId(rs.wasNull() ? null : uid);
                    l.setUsername(rs.getString("username"));
                    l.setActionType(rs.getString("action_type"));
                    l.setTableAffected(rs.getString("table_affected"));
                    int recId = rs.getInt("record_id");
                    l.setRecordId(rs.wasNull() ? null : recId);
                    l.setDetails(rs.getString("details"));
                    l.setTimestamp(rs.getTimestamp("timestamp"));
                    list.add(l);
                }
            }
        }
        return list;
    }
}
