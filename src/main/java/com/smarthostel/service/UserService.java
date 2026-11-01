package com.smarthostel.service;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.UserDAO;
import com.smarthostel.model.Role;
import com.smarthostel.model.User;
import com.smarthostel.util.PasswordUtil;
import com.smarthostel.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

// User and Staff Management Service Layer
public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public List<User> searchUsers(String query) throws SQLException {
        if (!ValidationUtil.isNotEmpty(query)) {
            return userDAO.getAllUsers();
        }
        return userDAO.searchUsers(query.trim());
    }

    public List<Role> getAllRoles() throws SQLException {
        return userDAO.getAllRoles();
    }

    // Creates new staff account (Admin exclusive)
    public boolean createStaffUser(User user, String plainPassword) throws Exception {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRoleName())) {
            throw new SecurityException("Access Denied: Only Administrator has permission to create staff accounts.");
        }

        // Validations
        if (!ValidationUtil.isNotEmpty(user.getUsername())) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (!ValidationUtil.isNotEmpty(user.getFullName())) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (user.getRoleId() <= 0) {
            throw new IllegalArgumentException("Please select a valid role.");
        }

        // Check if username already exists
        if (userDAO.findByUsername(user.getUsername().trim()) != null) {
            throw new Exception("Username '" + user.getUsername() + "' is already taken! Please choose another.");
        }

        // Hash password securely with BCrypt
        String passwordHash = PasswordUtil.hashPassword(plainPassword);
        user.setPasswordHash(passwordHash);

        int newUserId = userDAO.createUser(user);
        if (newUserId > 0) {
            user.setUserId(newUserId);
            auditLogDAO.logAction(currentUser.getUserId(), currentUser.getUsername(),
                    "CREATE_USER", "users", newUserId,
                    "Created new " + user.getRoleName() + " account for " + user.getUsername() + " (" + user.getFullName() + ")");
            return true;
        }
        return false;
    }

    // Updates user details and role (Admin exclusive)
    public boolean updateUser(User user) throws Exception {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRoleName())) {
            throw new SecurityException("Access Denied: Only Administrator has permission to modify user accounts.");
        }

        if (!ValidationUtil.isNotEmpty(user.getFullName())) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }

        boolean updated = userDAO.updateUser(user.getUserId(), user.getFullName(), user.getEmail(),
                user.getPhone(), user.getRoleId(), user.getStatus());
        if (updated) {
            auditLogDAO.logAction(currentUser.getUserId(), currentUser.getUsername(),
                    "UPDATE_USER", "users", user.getUserId(),
                    "Updated account details for user ID " + user.getUserId() + " (" + user.getUsername() + ")");
        }
        return updated;
    }

    // Resets user password (Admin exclusive)
    public boolean resetUserPassword(int userId, String newPassword) throws Exception {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRoleName())) {
            throw new SecurityException("Access Denied: Only Administrator can reset account passwords.");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }

        User targetUser = userDAO.findById(userId);
        if (targetUser == null) {
            throw new Exception("User not found.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(userId, newHash);
        if (updated) {
            auditLogDAO.logAction(currentUser.getUserId(), currentUser.getUsername(),
                    "ADMIN_RESET_PASSWORD", "users", userId,
                    "Administrator reset password for user: " + targetUser.getUsername());
        }
        return updated;
    }

    // Deletes user account with self-deletion protection
    public boolean deleteUser(int userId) throws Exception {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRoleName())) {
            throw new SecurityException("Access Denied: Only Administrator can delete user accounts.");
        }

        if (currentUser.getUserId() == userId) {
            throw new Exception("Security Constraint: You cannot delete your own currently logged-in Administrator account!");
        }

        User targetUser = userDAO.findById(userId);
        if (targetUser == null) {
            throw new Exception("User not found.");
        }

        boolean deleted = userDAO.deleteUser(userId);
        if (deleted) {
            auditLogDAO.logAction(currentUser.getUserId(), currentUser.getUsername(),
                    "DELETE_USER", "users", userId,
                    "Deleted user account: " + targetUser.getUsername() + " (" + targetUser.getRoleName() + ")");
        }
        return deleted;
    }
}

