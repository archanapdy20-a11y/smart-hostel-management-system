package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public int createNotification(Notification n) throws SQLException {
        String sql = "INSERT INTO notifications (recipient_role, recipient_user_id, title, message) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getRecipientRole());
            if (n.getRecipientUserId() != null && n.getRecipientUserId() > 0) {
                ps.setInt(2, n.getRecipientUserId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, n.getTitle());
            ps.setString(4, n.getMessage());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Notification> getNotificationsForUser(String role, Integer userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications " +
                     "WHERE recipient_role = 'ALL' " +
                     "   OR recipient_role = ? " +
                     "   OR (recipient_user_id IS NOT NULL AND recipient_user_id = ?) " +
                     "ORDER BY created_at DESC LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role != null ? role : "");
            if (userId != null) {
                ps.setInt(2, userId);
            } else {
                ps.setInt(2, -1);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setNotificationId(rs.getInt("notification_id"));
                    n.setRecipientRole(rs.getString("recipient_role"));
                    int uid = rs.getInt("recipient_user_id");
                    n.setRecipientUserId(rs.wasNull() ? null : uid);
                    n.setTitle(rs.getString("title"));
                    n.setMessage(rs.getString("message"));
                    n.setRead(rs.getBoolean("is_read"));
                    n.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(n);
                }
            }
        }
        return list;
    }
}
