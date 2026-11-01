package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.User;
import com.smarthostel.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User findById(int userId) throws SQLException {
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User authenticate(String username, String plainPassword) throws SQLException {
        User user = findByUsername(username);
        if (user != null && "ACTIVE".equalsIgnoreCase(user.getStatus())) {
            if (PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    public int createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role_id, full_name, email, phone, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setInt(3, user.getRoleId());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getStatus() != null ? user.getStatus() : "ACTIVE");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updatePassword(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProfile(int userId, String fullName, String email, String phone) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "ORDER BY u.user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        }
        return list;
    }

    public List<User> searchUsers(String query) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ? OR r.role_name LIKE ? " +
                     "ORDER BY u.user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
        }
        return list;
    }

    public boolean updateUser(int userId, String fullName, String email, String phone, int roleId, String status) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, role_id = ?, status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, roleId);
            ps.setString(5, status);
            ps.setInt(6, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<com.smarthostel.model.Role> getAllRoles() throws SQLException {
        List<com.smarthostel.model.Role> list = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY role_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new com.smarthostel.model.Role(rs.getInt("role_id"), rs.getString("role_name"), rs.getString("description")));
            }
        }
        return list;
    }

    public List<User> listStaffUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE r.role_name IN ('ADMIN', 'WARDEN', 'ACCOUNTANT') " +
                     "ORDER BY u.user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        }
        return list;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRoleId(rs.getInt("role_id"));
        u.setRoleName(rs.getString("role_name"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
