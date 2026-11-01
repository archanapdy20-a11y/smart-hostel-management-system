package com.smarthostel.service;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.RoomDAO;
import com.smarthostel.model.Room;
import com.smarthostel.model.User;
import com.smarthostel.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class RoomService {

    private final RoomDAO roomDAO = new RoomDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public boolean addRoom(Room r) throws Exception {
        validateRoom(r);
        int newId = roomDAO.addRoom(r);
        if (newId > 0) {
            r.setRoomId(newId);
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "ADD_ROOM", "rooms", newId,
                    "Added new room: " + r.getBlockName() + " - " + r.getRoomNumber() + " (" + r.getRoomType() + ")");
            return true;
        }
        return false;
    }

    public boolean updateRoom(Room r) throws Exception {
        validateRoom(r);
        boolean updated = roomDAO.updateRoom(r);
        if (updated) {
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "UPDATE_ROOM", "rooms", r.getRoomId(),
                    "Updated room: " + r.getBlockName() + " - " + r.getRoomNumber());
        }
        return updated;
    }

    public boolean deleteRoom(int roomId) throws Exception {
        Room r = roomDAO.findById(roomId);
        if (r == null) {
            throw new Exception("Room not found.");
        }
        if (r.getOccupiedCapacity() > 0) {
            throw new Exception("Cannot delete room " + r.getRoomNumber() + " while it is occupied by students. Please vacate the room first.");
        }
        boolean deleted = roomDAO.deleteRoom(roomId);
        if (deleted) {
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "DELETE_ROOM", "rooms", roomId,
                    "Deleted room: " + r.getBlockName() + " - " + r.getRoomNumber());
        }
        return deleted;
    }

    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }

    public List<Room> getAvailableRooms() throws SQLException {
        return roomDAO.getAvailableRooms();
    }

    public List<Room> getOccupiedRooms() throws SQLException {
        return roomDAO.getOccupiedRooms();
    }

    public List<Room> searchRooms(String keyword) throws SQLException {
        if (!ValidationUtil.isNotEmpty(keyword)) {
            return roomDAO.getAllRooms();
        }
        return roomDAO.searchRooms(keyword.trim());
    }

    public Room getRoomById(int roomId) throws SQLException {
        return roomDAO.findById(roomId);
    }

    public RoomDAO.RoomStats getRoomStats() throws SQLException {
        return roomDAO.getRoomStats();
    }

    private void validateRoom(Room r) {
        if (!ValidationUtil.isNotEmpty(r.getRoomNumber())) {
            throw new IllegalArgumentException("Room Number is required.");
        }
        if (!ValidationUtil.isNotEmpty(r.getBlockName())) {
            throw new IllegalArgumentException("Block Name is required.");
        }
        if (r.getTotalCapacity() <= 0) {
            throw new IllegalArgumentException("Total capacity must be at least 1.");
        }
        if (r.getMonthlyRent() == null || r.getMonthlyRent().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monthly rent must be a non-negative amount.");
        }
    }
}
