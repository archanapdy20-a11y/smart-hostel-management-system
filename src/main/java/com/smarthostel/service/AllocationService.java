package com.smarthostel.service;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AllocationDAO;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.model.Allocation;
import com.smarthostel.model.User;

import java.sql.SQLException;
import java.util.List;

public class AllocationService {

    private final AllocationDAO allocationDAO = new AllocationDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public boolean allocateRoom(Allocation alloc) throws Exception {
        User curr = SessionManager.getInstance().getCurrentUser();
        if (curr != null) {
            alloc.setAllocatedBy(curr.getUserId());
        }

        boolean success = allocationDAO.allocateRoom(alloc);
        if (success) {
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "ALLOCATE_ROOM", "allocations", alloc.getAllocationId(),
                    "Allocated Room ID " + alloc.getRoomId() + " to Student ID " + alloc.getStudentId());
        }
        return success;
    }

    public boolean vacateRoom(int allocationId, String notes) throws Exception {
        boolean success = allocationDAO.vacateRoom(allocationId, notes);
        if (success) {
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "VACATE_ROOM", "allocations", allocationId,
                    "Vacated Allocation ID: " + allocationId + ". Reason: " + notes);
        }
        return success;
    }

    public Allocation getActiveAllocationByStudent(int studentId) throws SQLException {
        return allocationDAO.getActiveAllocationByStudent(studentId);
    }

    public List<Allocation> getAllocations(boolean activeOnly) throws SQLException {
        return allocationDAO.getAllocations(activeOnly);
    }

    public List<Allocation> searchAllocations(String keyword) throws SQLException {
        return allocationDAO.searchAllocations(keyword);
    }
}
