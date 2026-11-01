package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.Allocation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllocationDAO {

    private final RoomDAO roomDAO = new RoomDAO();

    // Allocates a room to a student using a database transaction
    public boolean allocateRoom(Allocation alloc) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.createNewConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Verify student doesn't already have an active allocation
            String checkStudentSql = "SELECT allocation_id FROM allocations WHERE student_id = ? AND status = 'ACTIVE'";
            try (PreparedStatement psCheck = conn.prepareStatement(checkStudentSql)) {
                psCheck.setInt(1, alloc.getStudentId());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        throw new SQLException("Student already has an active room allocation!");
                    }
                }
            }

            // 2. Lock and check room availability with SELECT ... FOR UPDATE
            String checkRoomSql = "SELECT total_capacity, occupied_capacity, status FROM rooms WHERE room_id = ? FOR UPDATE";
            try (PreparedStatement psRoom = conn.prepareStatement(checkRoomSql)) {
                psRoom.setInt(1, alloc.getRoomId());
                try (ResultSet rs = psRoom.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Selected room not found!");
                    }
                    int total = rs.getInt("total_capacity");
                    int occupied = rs.getInt("occupied_capacity");
                    String status = rs.getString("status");

                    if ("UNDER_MAINTENANCE".equalsIgnoreCase(status)) {
                        throw new SQLException("Room is currently under maintenance!");
                    }
                    if (occupied >= total) {
                        throw new SQLException("Room is already at full capacity! Cannot allocate.");
                    }
                }
            }

            // 3. Insert allocation record
            String insertSql = "INSERT INTO allocations (student_id, room_id, allocation_date, status, allocated_by, notes) " +
                               "VALUES (?, ?, ?, 'ACTIVE', ?, ?)";
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, alloc.getStudentId());
                psInsert.setInt(2, alloc.getRoomId());
                psInsert.setDate(3, alloc.getAllocationDate() != null ? alloc.getAllocationDate() : new Date(System.currentTimeMillis()));
                if (alloc.getAllocatedBy() != null && alloc.getAllocatedBy() > 0) {
                    psInsert.setInt(4, alloc.getAllocatedBy());
                } else {
                    psInsert.setNull(4, Types.INTEGER);
                }
                psInsert.setString(5, alloc.getNotes());
                psInsert.executeUpdate();
            }

            // 4. Update room occupancy count (+1)
            boolean occupancyUpdated = roomDAO.updateOccupancy(conn, alloc.getRoomId(), 1);
            if (!occupancyUpdated) {
                throw new SQLException("Failed to update room occupancy count.");
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

    // Vacates an active room allocation using a database transaction
    public boolean vacateRoom(int allocationId, String notes) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.createNewConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Get room ID for this active allocation
            int roomId = -1;
            String fetchSql = "SELECT room_id, status FROM allocations WHERE allocation_id = ? FOR UPDATE";
            try (PreparedStatement psFetch = conn.prepareStatement(fetchSql)) {
                psFetch.setInt(1, allocationId);
                try (ResultSet rs = psFetch.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Allocation record not found!");
                    }
                    if (!"ACTIVE".equalsIgnoreCase(rs.getString("status"))) {
                        throw new SQLException("Allocation is already vacated or cancelled.");
                    }
                    roomId = rs.getInt("room_id");
                }
            }

            // 2. Mark allocation as VACATED with current date
            String updateAllocSql = "UPDATE allocations SET status = 'VACATED', vacate_date = CURRENT_DATE, notes = ? " +
                                    "WHERE allocation_id = ?";
            try (PreparedStatement psUp = conn.prepareStatement(updateAllocSql)) {
                psUp.setString(1, notes);
                psUp.setInt(2, allocationId);
                psUp.executeUpdate();
            }

            // 3. Decrement room occupancy (-1)
            boolean occupancyUpdated = roomDAO.updateOccupancy(conn, roomId, -1);
            if (!occupancyUpdated) {
                throw new SQLException("Failed to decrement room occupancy.");
            }

            conn.commit();
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

    public Allocation getActiveAllocationByStudent(int studentId) throws SQLException {
        String sql = getAllocationSelectQuery() + " WHERE a.student_id = ? AND a.status = 'ACTIVE' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAllocation(rs);
                }
            }
        }
        return null;
    }

    public List<Allocation> getAllocations(boolean activeOnly) throws SQLException {
        List<Allocation> list = new ArrayList<>();
        String sql = getAllocationSelectQuery() +
                     (activeOnly ? " WHERE a.status = 'ACTIVE' " : "") +
                     " ORDER BY a.allocation_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAllocation(rs));
            }
        }
        return list;
    }

    public List<Allocation> searchAllocations(String keyword) throws SQLException {
        List<Allocation> list = new ArrayList<>();
        String sql = getAllocationSelectQuery() +
                     " WHERE s.full_name LIKE ? OR s.register_number LIKE ? " +
                     " OR r.room_number LIKE ? OR r.block_name LIKE ? " +
                     " ORDER BY a.allocation_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAllocation(rs));
                }
            }
        }
        return list;
    }

    private String getAllocationSelectQuery() {
        return "SELECT a.*, " +
               "s.full_name AS student_name, s.register_number, " +
               "r.room_number, r.block_name, r.room_type, " +
               "u.full_name AS allocated_by_name " +
               "FROM allocations a " +
               "JOIN students s ON a.student_id = s.student_id " +
               "JOIN rooms r ON a.room_id = r.room_id " +
               "LEFT JOIN users u ON a.allocated_by = u.user_id ";
    }

    private Allocation mapAllocation(ResultSet rs) throws SQLException {
        Allocation a = new Allocation();
        a.setAllocationId(rs.getInt("allocation_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setRoomId(rs.getInt("room_id"));
        a.setAllocationDate(rs.getDate("allocation_date"));
        a.setVacateDate(rs.getDate("vacate_date"));
        a.setStatus(rs.getString("status"));
        int allocBy = rs.getInt("allocated_by");
        a.setAllocatedBy(rs.wasNull() ? null : allocBy);
        a.setNotes(rs.getString("notes"));

        a.setStudentName(rs.getString("student_name"));
        a.setRegisterNumber(rs.getString("register_number"));
        a.setRoomNumber(rs.getString("room_number"));
        a.setBlockName(rs.getString("block_name"));
        a.setRoomType(rs.getString("room_type"));
        a.setAllocatedByName(rs.getString("allocated_by_name"));
        return a;
    }
}
