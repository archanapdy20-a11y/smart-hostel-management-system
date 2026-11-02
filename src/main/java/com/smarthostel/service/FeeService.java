package com.smarthostel.service;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.FeeDAO;
import com.smarthostel.dao.PaymentDAO;
import com.smarthostel.model.Fee;
import com.smarthostel.model.Payment;
import com.smarthostel.model.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class FeeService {

    private final FeeDAO feeDAO = new FeeDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public boolean createFee(Fee f) throws Exception {
        if (f.getStudentId() <= 0) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (f.getTotalAmount() == null || f.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total fee amount must be greater than zero.");
        }

        int newId = feeDAO.createFee(f);
        if (newId > 0) {
            f.setFeeId(newId);
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "CREATE_FEE", "fees", newId,
                    "Generated fee invoice of \u20B9 " + f.getTotalAmount() + " for Student ID " + f.getStudentId());
            return true;
        }
        return false;
    }

    // Records payment: only Admin or Accountant authorized
    public boolean recordPayment(Payment p) throws Exception {
        User curr = SessionManager.getInstance().getCurrentUser();
        if (curr == null || (!"ADMIN".equalsIgnoreCase(curr.getRoleName()) && !"ACCOUNTANT".equalsIgnoreCase(curr.getRoleName()))) {
            throw new SecurityException("Access Denied: Only authorized Accountant or Administrator can record payments.");
        }

        p.setRecordedBy(curr.getUserId());
        boolean success = paymentDAO.recordPayment(p);
        if (success) {
            auditLogDAO.logAction(curr.getUserId(), curr.getUsername(),
                    "FEE_PAYMENT", "payments", p.getPaymentId(),
                    "Recorded payment of \u20B9 " + p.getAmountPaid() + " via " + p.getPaymentMethod() +
                    " [Receipt: " + p.getReceiptNumber() + "] for Student ID " + p.getStudentId());
        }
        return success;
    }

    public List<Fee> getFeesByStudent(int studentId) throws SQLException {
        return feeDAO.getFeesByStudent(studentId);
    }

    public List<Fee> getAllFees(String statusFilter) throws SQLException {
        return feeDAO.getAllFees(statusFilter);
    }

    public List<Fee> searchFees(String keyword) throws SQLException {
        return feeDAO.searchFees(keyword);
    }

    public FeeDAO.FeeSummary getFeeSummary() throws SQLException {
        return feeDAO.getFeeSummary();
    }

    public List<Payment> getPaymentsByStudent(int studentId) throws SQLException {
        return paymentDAO.getPaymentsByStudent(studentId);
    }

    public List<Payment> getAllPayments() throws SQLException {
        return paymentDAO.getAllPayments();
    }

    public List<Payment> getRecentPayments(int limit) throws SQLException {
        return paymentDAO.getRecentPayments(limit);
    }

    public List<Payment> searchPayments(String keyword) throws SQLException {
        return paymentDAO.searchPayments(keyword);
    }
}
