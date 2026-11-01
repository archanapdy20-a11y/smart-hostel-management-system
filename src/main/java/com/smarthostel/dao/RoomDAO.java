package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public int addRoom(Room r) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, block_name, floor_number, room_type, total_capacity, occupied_capacity, monthly_rent, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getBlockName());
            ps.setInt(3, r.getFloorNumber());
            ps.setString(4, r.getRoomType());
            ps.setInt(5, r.getTotalCapacity());
            ps.setInt(6, r.getOccupiedCapacity());
            ps.setBigDecimal(7, r.getMonthlyRent());
            ps.setString(8, r.getStatus() != null ? r.getStatus() : "AVAILABLE");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateRoom(Room r) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, block_name = ?, floor_number = ?, " +
                     "room_type = ?, total_capacity = ?, monthly_rent = ?, status = ? " +
                     "WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getBlockName());
            ps.setInt(3, r.getFloorNumber());
            ps.setString(4, r.getRoomType());
            ps.setInt(5, r.getTotalCapacity());
            ps.setBigDecimal(6, r.getMonthlyRent());
            ps.setString(7, r.getStatus());
            ps.setInt(8, r.getRoomId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ? AND occupied_capacity = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        }
    }

    public Room findById(int roomId) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoom(rs);
                }
            }
        }
        return null;
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY block_name ASC, floor_number ASC, room_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRoom(rs));
            }
        }
        return list;
    }

    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE (total_capacity - occupied_capacity) > 0 " +
                     "AND status != 'UNDER_MAINTENANCE' ORDER BY block_name, room_number";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRoom(rs));
            }
        }
        return list;
    }

    public List<Room> getOccupiedRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE occupied_capacity > 0 ORDER BY block_name, room_number";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRoom(rs));
            }
        }
        return list;
    }

    public List<Room> searchRooms(String keyword) throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_number LIKE ? OR block_name LIKE ? OR room_type LIKE ? " +
                     "ORDER BY block_name, room_number";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRoom(rs));
                }
            }
        }
        return list;
    }

    // Updates room occupancy count delta (+1 or -1) within active transaction
    public boolean updateOccupancy(Connection conn, int roomId, int delta) throws SQLException {
        String updateSql = "UPDATE rooms SET occupied_capacity = occupied_capacity + ?, " +
                           "status = CASE " +
                           "  WHEN (occupied_capacity + ?) >= total_capacity THEN 'OCCUPIED' " +
                           "  ELSE 'AVAILABLE' " +
                           "END " +
                           "WHERE room_id = ? AND (occupied_capacity + ?) BETWEEN 0 AND total_capacity";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, delta);
            ps.setInt(2, delta);
            ps.setInt(3, roomId);
            ps.setInt(4, delta);
            return ps.executeUpdate() > 0;
        }
    }

    public RoomStats getRoomStats() throws SQLException {
        String sql = "SELECT COUNT(*) as total_rooms, " +
                     "COALESCE(SUM(total_capacity), 0) as total_beds, " +
                     "COALESCE(SUM(occupied_capacity), 0) as occupied_beds, " +
                     "COALESCE(SUM(total_capacity - occupied_capacity), 0) as available_beds " +
                     "FROM rooms";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new RoomStats(
                        rs.getInt("total_rooms"),
                        rs.getInt("total_beds"),
                        rs.getInt("occupied_beds"),
                        rs.getInt("available_beds")
                );
            }
        }
        return new RoomStats(0, 0, 0, 0);
    }

    public static class RoomStats {
        public final int totalRooms;
        public final int totalBeds;
        public final int occupiedBeds;
        public final int availableBeds;

        public RoomStats(int totalRooms, int totalBeds, int occupiedBeds, int availableBeds) {
            this.totalRooms = totalRooms;
            this.totalBeds = totalBeds;
            this.occupiedBeds = occupiedBeds;
            this.availableBeds = availableBeds;
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setBlockName(rs.getString("block_name"));
        r.setFloorNumber(rs.getInt("floor_number"));
        r.setRoomType(rs.getString("room_type"));
        r.setTotalCapacity(rs.getInt("total_capacity"));
        r.setOccupiedCapacity(rs.getInt("occupied_capacity"));
        r.setAvailableBeds(r.getTotalCapacity() - r.getOccupiedCapacity());
        r.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}
