package com.smarthostel.config;

import com.smarthostel.model.Student;
import com.smarthostel.model.User;

import java.time.LocalDateTime;

// Desktop session manager maintaining authenticated user state
public class SessionManager {

    private static SessionManager instance;

    private User currentUser;
    private Student currentStudent;
    private LocalDateTime loginTime;

    private SessionManager() {
        // Singleton
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(User user, Student student) {
        this.currentUser = user;
        this.currentStudent = student;
        this.loginTime = LocalDateTime.now();
        System.out.println("Session started for user: " + user.getUsername() + " [" + user.getRoleName() + "]");
    }

    public void endSession() {
        if (this.currentUser != null) {
            System.out.println("Ending session for user: " + this.currentUser.getUsername());
        }
        this.currentUser = null;
        this.currentStudent = null;
        this.loginTime = null;
    }

    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public String getCurrentRole() {
        return currentUser != null ? currentUser.getRoleName() : null;
    }

    public boolean hasRole(String role) {
        return currentUser != null && currentUser.getRoleName().equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isWarden() {
        return hasRole("WARDEN");
    }

    public boolean isAccountant() {
        return hasRole("ACCOUNTANT");
    }

    public boolean isStudent() {
        return hasRole("STUDENT");
    }
}
