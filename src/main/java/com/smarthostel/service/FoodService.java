package com.smarthostel.service;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.FoodFeedbackDAO;
import com.smarthostel.dao.FoodMenuDAO;
import com.smarthostel.model.FoodFeedback;
import com.smarthostel.model.FoodMenu;
import com.smarthostel.model.User;
import com.smarthostel.util.ValidationUtil;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class FoodService {

    private final FoodMenuDAO menuDAO = new FoodMenuDAO();
    private final FoodFeedbackDAO feedbackDAO = new FoodFeedbackDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    // ================= FOOD MENU =================
    public boolean saveMenu(FoodMenu m) throws Exception {
        User curr = SessionManager.getInstance().getCurrentUser();
        if (curr != null && !"ADMIN".equalsIgnoreCase(curr.getRoleName())) {
            throw new SecurityException("Access Denied: Only Administrator can modify the food and mess menu.");
        }

        if (m.getMenuDate() == null) {
            throw new IllegalArgumentException("Menu Date is required.");
        }
        if (!ValidationUtil.isNotEmpty(m.getMenuItems())) {
            throw new IllegalArgumentException("Menu items list cannot be empty.");
        }

        if (curr != null) {
            m.setCreatedBy(curr.getUserId());
        }

        boolean saved = menuDAO.saveOrUpdateMenu(m);
        if (saved) {
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "ADMIN",
                    "SAVE_FOOD_MENU", "food_menu", 0,
                    "Saved " + m.getMealType() + " menu for " + m.getMenuDate());
        }
        return saved;
    }

    public boolean deleteMenu(int menuId) throws Exception {
        User curr = SessionManager.getInstance().getCurrentUser();
        if (curr != null && !"ADMIN".equalsIgnoreCase(curr.getRoleName())) {
            throw new SecurityException("Access Denied: Only Administrator can delete food menu records.");
        }
        return menuDAO.deleteMenu(menuId);
    }

    public List<FoodMenu> getTodayMenu() throws SQLException {
        return menuDAO.getTodayMenu();
    }

    public List<FoodMenu> getWeeklyMenu(Date startDate) throws SQLException {
        return menuDAO.getWeeklyMenu(startDate);
    }

    public List<FoodMenu> getAllMenus() throws SQLException {
        return menuDAO.getAllMenus();
    }

    // ================= FOOD FEEDBACK =================
    public boolean submitFeedback(FoodFeedback fb) throws Exception {
        if (fb.getStudentId() <= 0) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (fb.getMenuDate() == null) {
            throw new IllegalArgumentException("Date is required.");
        }
        if (fb.getRating() < 1 || fb.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }

        boolean submitted = feedbackDAO.submitFeedback(fb);
        if (submitted) {
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "STUDENT",
                    "FOOD_FEEDBACK", "food_feedback", fb.getFeedbackId(),
                    "Submitted " + fb.getRating() + "-star feedback for " + fb.getMealType());
        }
        return submitted;
    }

    public List<FoodFeedback> getAllFeedback() throws SQLException {
        return feedbackDAO.getAllFeedback();
    }

    public List<FoodFeedback> getFeedbackByStudent(int studentId) throws SQLException {
        return feedbackDAO.getFeedbackByStudent(studentId);
    }

    public double getAverageRating() throws SQLException {
        return feedbackDAO.getAverageRating();
    }
}
