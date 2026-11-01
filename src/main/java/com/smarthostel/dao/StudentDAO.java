package com.smarthostel.dao;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public int addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (user_id, register_number, full_name, gender, department, " +
                     "academic_year, date_of_birth, phone_number, email, address, parent_name, parent_contact, admission_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (s.getUserId() != null && s.getUserId() > 0) {
                ps.setInt(1, s.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, s.getRegisterNumber());
            ps.setString(3, s.getFullName());
            ps.setString(4, s.getGender());
            ps.setString(5, s.getDepartment());
            ps.setInt(6, s.getAcademicYear());
            ps.setDate(7, s.getDateOfBirth());
            ps.setString(8, s.getPhoneNumber());
            ps.setString(9, s.getEmail());
            ps.setString(10, s.getAddress());
            ps.setString(11, s.getParentName());
            ps.setString(12, s.getParentContact());
            ps.setDate(13, s.getAdmissionDate());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET register_number = ?, full_name = ?, gender = ?, department = ?, " +
                     "academic_year = ?, date_of_birth = ?, phone_number = ?, email = ?, address = ?, " +
                     "parent_name = ?, parent_contact = ?, admission_date = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRegisterNumber());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getGender());
            ps.setString(4, s.getDepartment());
            ps.setInt(5, s.getAcademicYear());
            ps.setDate(6, s.getDateOfBirth());
            ps.setString(7, s.getPhoneNumber());
            ps.setString(8, s.getEmail());
            ps.setString(9, s.getAddress());
            ps.setString(10, s.getParentName());
            ps.setString(11, s.getParentContact());
            ps.setDate(12, s.getAdmissionDate());
            ps.setInt(13, s.getStudentId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    public Student findById(int studentId) throws SQLException {
        String sql = getStudentSelectQuery() + " WHERE s.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    public Student findByUserId(int userId) throws SQLException {
        String sql = getStudentSelectQuery() + " WHERE s.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    public Student findByRegisterNumber(String regNo) throws SQLException {
        String sql = getStudentSelectQuery() + " WHERE s.register_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = getStudentSelectQuery() + " ORDER BY s.student_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapStudent(rs));
            }
        }
        return list;
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = getStudentSelectQuery() +
                     " WHERE s.full_name LIKE ? OR s.register_number LIKE ? " +
                     " OR s.department LIKE ? OR s.phone_number LIKE ? OR s.email LIKE ? " +
                     " ORDER BY s.student_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }
        }
        return list;
    }

    public int getTotalStudentCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private String getStudentSelectQuery() {
        return "SELECT s.*, " +
               " (SELECT CONCAT(r.block_name, ' - ', r.room_number) " +
               "  FROM allocations a " +
               "  JOIN rooms r ON a.room_id = r.room_id " +
               "  WHERE a.student_id = s.student_id AND a.status = 'ACTIVE' LIMIT 1) AS allocated_room, " +
               " (SELECT f.status FROM fees f WHERE f.student_id = s.student_id ORDER BY f.fee_id DESC LIMIT 1) AS fee_status " +
               "FROM students s ";
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        int userId = rs.getInt("user_id");
        s.setUserId(rs.wasNull() ? null : userId);
        s.setRegisterNumber(rs.getString("register_number"));
        s.setFullName(rs.getString("full_name"));
        s.setGender(rs.getString("gender"));
        s.setDepartment(rs.getString("department"));
        s.setAcademicYear(rs.getInt("academic_year"));
        s.setDateOfBirth(rs.getDate("date_of_birth"));
        s.setPhoneNumber(rs.getString("phone_number"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        s.setParentName(rs.getString("parent_name"));
        s.setParentContact(rs.getString("parent_contact"));
        s.setAdmissionDate(rs.getDate("admission_date"));

        s.setAllocatedRoom(rs.getString("allocated_room") != null ? rs.getString("allocated_room") : "Not Allocated");
        s.setFeeStatus(rs.getString("fee_status") != null ? rs.getString("fee_status") : "NO_RECORD");
        return s;
    }
}
