package com.smarthostel.service;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.ComplaintDAO;
import com.smarthostel.model.Complaint;
import com.smarthostel.model.User;
import com.smarthostel.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class ComplaintService {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public boolean submitComplaint(Complaint c) throws Exception {
        if (c.getStudentId() <= 0) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (!ValidationUtil.isNotEmpty(c.getCategory())) {
            throw new IllegalArgumentException("Please select a valid complaint category.");
        }
        if (!ValidationUtil.isNotEmpty(c.getDescription()) || c.getDescription().trim().length() < 10) {
            throw new IllegalArgumentException("Please describe the issue in at least 10 characters.");
        }

        int newId = complaintDAO.submitComplaint(c);
        if (newId > 0) {
            c.setComplaintId(newId);
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "STUDENT",
                    "SUBMIT_COMPLAINT", "complaints", newId,
                    "Submitted " + c.getCategory() + " [Priority: " + c.getPriority() + "]");
            return true;
        }
        return false;
    }

    public boolean updateStatus(int complaintId, String newStatus, String resolutionNotes) throws Exception {
        User curr = SessionManager.getInstance().getCurrentUser();
        int resolverId = curr != null ? curr.getUserId() : 0;

        boolean updated = complaintDAO.updateStatus(complaintId, newStatus, resolutionNotes, resolverId);
        if (updated) {
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "WARDEN",
                    "UPDATE_COMPLAINT", "complaints", complaintId,
                    "Updated complaint status to '" + newStatus + "'");
        }
        return updated;
    }

    public List<Complaint> getComplaintsByStudent(int studentId) throws SQLException {
        return complaintDAO.getComplaintsByStudent(studentId);
    }

    public List<Complaint> getAllComplaints(String statusFilter) throws SQLException {
        return complaintDAO.getAllComplaints(statusFilter);
    }

    public List<Complaint> searchComplaints(String keyword) throws SQLException {
        return complaintDAO.searchComplaints(keyword);
    }

    public ComplaintDAO.ComplaintSummary getComplaintSummary() throws SQLException {
        return complaintDAO.getComplaintSummary();
    }
}
