package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.Complaint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    public int submitComplaint(Complaint c) throws SQLException {
        // Auto-assign priority based on keyword if not already explicitly set
        if (c.getPriority() == null || c.getPriority().isEmpty()) {
            c.setPriority(detectPriority(c.getDescription()));
        }

        String sql = "INSERT INTO complaints (student_id, category, description, status, priority) " +
                     "VALUES (?, ?, ?, 'Pending', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getStudentId());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getPriority());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateStatus(int complaintId, String newStatus, String resolutionNotes, int resolvedBy) throws SQLException {
        String sql = "UPDATE complaints SET status = ?, resolution_notes = ?, " +
                     "resolved_by = ?, resolved_at = " +
                     ("Resolved".equalsIgnoreCase(newStatus) ? "CURRENT_TIMESTAMP" : "NULL") +
                     " WHERE complaint_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, resolutionNotes);
            if (resolvedBy > 0) {
                ps.setInt(3, resolvedBy);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, complaintId);

            return ps.executeUpdate() > 0;
        }
    }

    public List<Complaint> getComplaintsByStudent(int studentId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = getComplaintSelectQuery() + " WHERE c.student_id = ? ORDER BY c.complaint_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapComplaint(rs));
                }
            }
        }
        return list;
    }

    public List<Complaint> getAllComplaints(String statusFilter) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(getComplaintSelectQuery());
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
            sql.append(" WHERE c.status = '").append(statusFilter).append("' ");
        }
        sql.append(" ORDER BY CASE c.priority " +
                   " WHEN 'EMERGENCY' THEN 1 " +
                   " WHEN 'HIGH' THEN 2 " +
                   " WHEN 'MEDIUM' THEN 3 " +
                   " ELSE 4 END ASC, c.complaint_id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapComplaint(rs));
            }
        }
        return list;
    }

    public List<Complaint> searchComplaints(String keyword) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = getComplaintSelectQuery() +
                     " WHERE s.full_name LIKE ? OR s.register_number LIKE ? " +
                     " OR c.category LIKE ? OR c.description LIKE ? " +
                     " ORDER BY c.complaint_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapComplaint(rs));
                }
            }
        }
        return list;
    }

    public ComplaintSummary getComplaintSummary() throws SQLException {
        String sql = "SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending, " +
                     "SUM(CASE WHEN status = 'In Progress' THEN 1 ELSE 0 END) as in_progress, " +
                     "SUM(CASE WHEN status = 'Resolved' THEN 1 ELSE 0 END) as resolved " +
                     "FROM complaints";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new ComplaintSummary(
                        rs.getInt("total"),
                        rs.getInt("pending"),
                        rs.getInt("in_progress"),
                        rs.getInt("resolved")
                );
            }
        }
        return new ComplaintSummary(0, 0, 0, 0);
    }

    public static class ComplaintSummary {
        public final int total;
        public final int pending;
        public final int inProgress;
        public final int resolved;

        public ComplaintSummary(int total, int pending, int inProgress, int resolved) {
            this.total = total;
            this.pending = pending;
            this.inProgress = inProgress;
            this.resolved = resolved;
        }
    }

    private String detectPriority(String description) {
        if (description == null) return "MEDIUM";
        String lower = description.toLowerCase();
        if (lower.contains("fire") || lower.contains("smoke") || lower.contains("spark") ||
            lower.contains("short circuit") || lower.contains("gas leak") || lower.contains("emergency")) {
            return "EMERGENCY";
        }
        if (lower.contains("leak") || lower.contains("water") || lower.contains("overflow") ||
            lower.contains("lock broken") || lower.contains("no power") || lower.contains("blackout")) {
            return "HIGH";
        }
        if (lower.contains("clean") || lower.contains("bulb") || lower.contains("food") || lower.contains("fan")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String getComplaintSelectQuery() {
        return "SELECT c.*, " +
               "s.full_name AS student_name, s.register_number, " +
               "(SELECT r.room_number FROM allocations a JOIN rooms r ON a.room_id = r.room_id " +
               " WHERE a.student_id = c.student_id AND a.status = 'ACTIVE' LIMIT 1) as room_number, " +
               "u.full_name AS resolved_by_name " +
               "FROM complaints c " +
               "JOIN students s ON c.student_id = s.student_id " +
               "LEFT JOIN users u ON c.resolved_by = u.user_id ";
    }

    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getInt("complaint_id"));
        c.setStudentId(rs.getInt("student_id"));
        c.setCategory(rs.getString("category"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        c.setPriority(rs.getString("priority"));
        c.setSubmittedAt(rs.getTimestamp("submitted_at"));
        c.setResolvedAt(rs.getTimestamp("resolved_at"));
        c.setResolutionNotes(rs.getString("resolution_notes"));
        int resBy = rs.getInt("resolved_by");
        c.setResolvedBy(rs.wasNull() ? null : resBy);

        c.setStudentName(rs.getString("student_name"));
        c.setRegisterNumber(rs.getString("register_number"));
        c.setRoomNumber(rs.getString("room_number") != null ? rs.getString("room_number") : "N/A");
        c.setResolvedByName(rs.getString("resolved_by_name"));
        return c;
    }
}
