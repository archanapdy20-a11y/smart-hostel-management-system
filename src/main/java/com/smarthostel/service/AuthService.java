package com.smarthostel.service;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.StudentDAO;
import com.smarthostel.dao.UserDAO;
import com.smarthostel.model.Student;
import com.smarthostel.model.User;
import com.smarthostel.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public AuthService() {
        bootstrapAdminIfNeeded();
    }

    // Authenticates credentials and initializes session
    public User login(String username, String plainPassword) throws Exception {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Username and Password cannot be empty.");
        }

        User user = userDAO.authenticate(username.trim(), plainPassword);
        if (user == null) {
            throw new Exception("Invalid username or password. Please verify your credentials.");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new Exception("Your account is currently " + user.getStatus() + ". Please contact the hostel administrator.");
        }

        // If user is a student, fetch the student profile
        Student student = null;
        if ("STUDENT".equalsIgnoreCase(user.getRoleName())) {
            student = studentDAO.findByUserId(user.getUserId());
            if (student == null) {
                // Try searching by email
                for (Student s : studentDAO.getAllStudents()) {
                    if (s.getEmail().equalsIgnoreCase(user.getEmail())) {
                        student = s;
                        break;
                    }
                }
            }
        }

        // Initialize desktop session
        SessionManager.getInstance().startSession(user, student);

        // Audit log
        auditLogDAO.logAction(user.getUserId(), user.getUsername(), "LOGIN", "users", user.getUserId(),
                "User logged in successfully with role: " + user.getRoleName());

        return user;
    }

    public void logout() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            auditLogDAO.logAction(user.getUserId(), user.getUsername(), "LOGOUT", "users", user.getUserId(),
                    "User logged out of desktop application.");
        }
        SessionManager.getInstance().endSession();
    }

    // Verifies username and email to reset password
    public boolean resetPassword(String username, String email, String newPassword) throws Exception {
        if (username == null || username.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and registered email are required.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null || !user.getEmail().equalsIgnoreCase(email.trim())) {
            throw new Exception("No account matches the provided username and registered email.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(user.getUserId(), newHash);
        if (updated) {
            auditLogDAO.logAction(user.getUserId(), user.getUsername(), "PASSWORD_RESET", "users", user.getUserId(),
                    "Password was securely reset via Forgot Password workflow.");
        }
        return updated;
    }

    // Changes password for currently authenticated user
    public boolean changePassword(String currentPassword, String newPassword) throws Exception {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("No active user session.");
        }
        if (!PasswordUtil.checkPassword(currentPassword, user.getPasswordHash())) {
            throw new Exception("Current password verification failed. Please try again.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new Exception("New password must be at least 6 characters.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(user.getUserId(), newHash);
        if (updated) {
            user.setPasswordHash(newHash);
            auditLogDAO.logAction(user.getUserId(), user.getUsername(), "PASSWORD_CHANGE", "users", user.getUserId(),
                    "User changed their password.");
        }
        return updated;
    }

    // Bootstraps initial admin and roles if tables are empty
    private void bootstrapAdminIfNeeded() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check roles
            String checkRoles = "SELECT COUNT(*) FROM roles";
            try (PreparedStatement ps = conn.prepareStatement(checkRoles);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String seedRoles = "INSERT INTO roles (role_id, role_name, description) VALUES " +
                                       "(1, 'ADMIN', 'System Administrator'), " +
                                       "(2, 'WARDEN', 'Hostel Warden'), " +
                                       "(3, 'ACCOUNTANT', 'Hostel Accountant'), " +
                                       "(4, 'STUDENT', 'Hostel Student')";
                    try (PreparedStatement psSeed = conn.prepareStatement(seedRoles)) {
                        psSeed.executeUpdate();
                    }
                }
            }

            // Check users
            String checkUsers = "SELECT COUNT(*) FROM users";
            try (PreparedStatement ps = conn.prepareStatement(checkUsers);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Seed initial admin
                    String adminHash = PasswordUtil.hashPassword("admin123");
                    User admin = new User(0, "admin", adminHash, 1, "ADMIN",
                            "System Administrator", "admin@smarthostel.edu", "9876543210", "ACTIVE");
                    userDAO.createUser(admin);

                    // Seed initial warden
                    String wardenHash = PasswordUtil.hashPassword("warden123");
                    User warden = new User(0, "warden", wardenHash, 2, "WARDEN",
                            "Dr. Robert Vance", "warden@smarthostel.edu", "9876543211", "ACTIVE");
                    userDAO.createUser(warden);

                    // Seed initial accountant
                    String acctHash = PasswordUtil.hashPassword("account123");
                    User accountant = new User(0, "accountant", acctHash, 3, "ACCOUNTANT",
                            "Sarah Jenkins", "accounts@smarthostel.edu", "9876543212", "ACTIVE");
                    userDAO.createUser(accountant);

                    // Seed initial student user
                    String studentHash = PasswordUtil.hashPassword("student123");
                    User studentUser = new User(0, "student", studentHash, 4, "STUDENT",
                            "Alex Morgan", "alex.morgan@smarthostel.edu", "9876543213", "ACTIVE");
                    userDAO.createUser(studentUser);

                    System.out.println("Default administrative and staff accounts seeded automatically.");
                }
            }
        } catch (Exception e) {
            System.err.println("Database bootstrap check: " + e.getMessage());
        }
    }
}
