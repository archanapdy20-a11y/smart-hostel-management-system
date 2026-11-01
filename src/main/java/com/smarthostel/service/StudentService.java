package com.smarthostel.service;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.StudentDAO;
import com.smarthostel.dao.UserDAO;
import com.smarthostel.model.Student;
import com.smarthostel.model.User;
import com.smarthostel.util.PasswordUtil;
import com.smarthostel.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public boolean registerStudent(Student s, boolean createLoginAccount) throws Exception {
        validateStudent(s);

        // Check duplicate register number
        if (studentDAO.findByRegisterNumber(s.getRegisterNumber()) != null) {
            throw new Exception("Student with Register Number '" + s.getRegisterNumber() + "' already exists!");
        }

        // Optionally create student user login account
        if (createLoginAccount) {
            String defaultUsername = s.getRegisterNumber().toLowerCase().replace("-", ".");
            if (userDAO.findByUsername(defaultUsername) == null) {
                String defaultPasswordHash = PasswordUtil.hashPassword("student123");
                User u = new User(0, defaultUsername, defaultPasswordHash, 4, "STUDENT",
                        s.getFullName(), s.getEmail(), s.getPhoneNumber(), "ACTIVE");
                int newUserId = userDAO.createUser(u);
                s.setUserId(newUserId);
            }
        }

        int newId = studentDAO.addStudent(s);
        if (newId > 0) {
            s.setStudentId(newId);
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "ADD_STUDENT", "students", newId,
                    "Registered new student: " + s.getFullName() + " (" + s.getRegisterNumber() + ")");
            return true;
        }
        return false;
    }

    public boolean updateStudent(Student s) throws Exception {
        validateStudent(s);
        boolean updated = studentDAO.updateStudent(s);
        if (updated) {
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "UPDATE_STUDENT", "students", s.getStudentId(),
                    "Updated student profile: " + s.getFullName() + " (" + s.getRegisterNumber() + ")");
        }
        return updated;
    }

    public boolean deleteStudent(int studentId) throws Exception {
        Student s = studentDAO.findById(studentId);
        if (s == null) {
            throw new Exception("Student not found.");
        }
        boolean deleted = studentDAO.deleteStudent(studentId);
        if (deleted) {
            User curr = SessionManager.getInstance().getCurrentUser();
            auditLogDAO.logAction(curr != null ? curr.getUserId() : null,
                    curr != null ? curr.getUsername() : "SYSTEM",
                    "DELETE_STUDENT", "students", studentId,
                    "Deleted student: " + s.getFullName() + " (" + s.getRegisterNumber() + ")");
        }
        return deleted;
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        if (!ValidationUtil.isNotEmpty(keyword)) {
            return studentDAO.getAllStudents();
        }
        return studentDAO.searchStudents(keyword.trim());
    }

    public Student getStudentById(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    public Student getStudentByUserId(int userId) throws SQLException {
        return studentDAO.findByUserId(userId);
    }

    private void validateStudent(Student s) throws IllegalArgumentException {
        if (!ValidationUtil.isNotEmpty(s.getRegisterNumber())) {
            throw new IllegalArgumentException("Register Number is required.");
        }
        if (!ValidationUtil.isNotEmpty(s.getFullName())) {
            throw new IllegalArgumentException("Student Name is required.");
        }
        if (!ValidationUtil.isValidEmail(s.getEmail())) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (!ValidationUtil.isValidPhone(s.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number must contain 10-15 digits.");
        }
        if (!ValidationUtil.isValidPhone(s.getParentContact())) {
            throw new IllegalArgumentException("Parent contact number must contain 10-15 digits.");
        }
        if (s.getAcademicYear() < 1 || s.getAcademicYear() > 5) {
            throw new IllegalArgumentException("Academic Year must be between 1 and 5.");
        }
    }
}
