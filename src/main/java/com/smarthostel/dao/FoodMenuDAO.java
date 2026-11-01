package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.FoodMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodMenuDAO {

    public boolean saveOrUpdateMenu(FoodMenu m) throws SQLException {
        String sql = "INSERT INTO food_menu (menu_date, day_of_week, meal_type, menu_items, special_notes, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE day_of_week = VALUES(day_of_week), " +
                     "menu_items = VALUES(menu_items), special_notes = VALUES(special_notes)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, m.getMenuDate());
            ps.setString(2, m.getDayOfWeek());
            ps.setString(3, m.getMealType());
            ps.setString(4, m.getMenuItems());
            ps.setString(5, m.getSpecialNotes());
            if (m.getCreatedBy() != null && m.getCreatedBy() > 0) {
                ps.setInt(6, m.getCreatedBy());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteMenu(int menuId) throws SQLException {
        String sql = "DELETE FROM food_menu WHERE menu_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<FoodMenu> getMenuByDate(Date date) throws SQLException {
        List<FoodMenu> list = new ArrayList<>();
        String sql = getMenuSelectQuery() + " WHERE m.menu_date = ? ORDER BY " +
                     "CASE m.meal_type WHEN 'Breakfast' THEN 1 WHEN 'Lunch' THEN 2 WHEN 'Dinner' THEN 3 END";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMenu(rs));
                }
            }
        }
        return list;
    }

    public List<FoodMenu> getTodayMenu() throws SQLException {
        return getMenuByDate(new Date(System.currentTimeMillis()));
    }

    public List<FoodMenu> getWeeklyMenu(Date startDate) throws SQLException {
        List<FoodMenu> list = new ArrayList<>();
        String sql = getMenuSelectQuery() + " WHERE m.menu_date BETWEEN ? AND DATE_ADD(?, INTERVAL 6 DAY) " +
                     "ORDER BY m.menu_date ASC, CASE m.meal_type WHEN 'Breakfast' THEN 1 WHEN 'Lunch' THEN 2 WHEN 'Dinner' THEN 3 END";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, startDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMenu(rs));
                }
            }
        }
        return list;
    }

    public List<FoodMenu> getAllMenus() throws SQLException {
        List<FoodMenu> list = new ArrayList<>();
        String sql = getMenuSelectQuery() + " ORDER BY m.menu_date DESC, m.meal_type ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapMenu(rs));
            }
        }
        return list;
    }

    private String getMenuSelectQuery() {
        return "SELECT m.*, u.full_name AS created_by_name " +
               "FROM food_menu m " +
               "LEFT JOIN users u ON m.created_by = u.user_id ";
    }

    private FoodMenu mapMenu(ResultSet rs) throws SQLException {
        FoodMenu m = new FoodMenu();
        m.setMenuId(rs.getInt("menu_id"));
        m.setMenuDate(rs.getDate("menu_date"));
        m.setDayOfWeek(rs.getString("day_of_week"));
        m.setMealType(rs.getString("meal_type"));
        m.setMenuItems(rs.getString("menu_items"));
        m.setSpecialNotes(rs.getString("special_notes"));
        int cBy = rs.getInt("created_by");
        m.setCreatedBy(rs.wasNull() ? null : cBy);
        m.setCreatedByName(rs.getString("created_by_name"));
        return m;
    }
}
