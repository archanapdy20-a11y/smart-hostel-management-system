package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.UserDAO;
import com.smarthostel.model.Student;
import com.smarthostel.model.User;
import com.smarthostel.service.AuthService;
import com.smarthostel.util.UITheme;
import com.smarthostel.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();
    private final Runnable onLogout;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    private JPasswordField currentPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;

    public ProfilePanel(Runnable onLogout) {
        this.onLogout = onLogout;

        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
    }

    private void initUI() {
        User user = SessionManager.getInstance().getCurrentUser();
        Student student = SessionManager.getInstance().getCurrentStudent();

        JPanel mainGrid = new JPanel(new GridLayout(1, 2, 24, 0));
        mainGrid.setBackground(UITheme.BG_MAIN);

        // ================= LEFT: Profile Details Card =================
        JPanel profileCard = UITheme.createCard();
        profileCard.setLayout(new BorderLayout(0, 16));

        JLabel pHeader = new JLabel("👤 Account Profile Information");
        pHeader.setFont(UITheme.FONT_SUBTITLE);
        pHeader.setForeground(UITheme.TEXT_MAIN);
        profileCard.add(pHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(UITheme.BG_CARD);

        JTextField usernameField = UITheme.createTextField();
        usernameField.setText(user != null ? user.getUsername() : "");
        usernameField.setEditable(false);
        usernameField.setBackground(UITheme.BG_SUBTLE);

        JTextField roleField = UITheme.createTextField();
        roleField.setText(user != null ? user.getRoleName() : "");
        roleField.setEditable(false);
        roleField.setBackground(UITheme.BG_SUBTLE);

        nameField = UITheme.createTextField();
        nameField.setText(user != null ? user.getFullName() : "");

        emailField = UITheme.createTextField();
        emailField.setText(user != null ? user.getEmail() : "");

        phoneField = UITheme.createTextField();
        phoneField.setText(user != null ? user.getPhone() : "");

        form.add(new JLabel("Username (Read-Only):"));
        form.add(usernameField);
        form.add(new JLabel("Assigned Role:"));
        form.add(roleField);

        if (student != null) {
            JTextField regField = UITheme.createTextField();
            regField.setText(student.getRegisterNumber() + " - " + student.getDepartment() + " (Year " + student.getAcademicYear() + ")");
            regField.setEditable(false);
            regField.setBackground(UITheme.BG_SUBTLE);
            form.add(new JLabel("Student Register Info:"));
            form.add(regField);
        }

        form.add(new JLabel("Full Name: *"));
        form.add(nameField);
        form.add(new JLabel("Email Address: *"));
        form.add(emailField);
        form.add(new JLabel("Contact Phone Number:"));
        form.add(phoneField);

        profileCard.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel pBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pBtnRow.setBackground(UITheme.BG_CARD);

        JButton saveProfileBtn = UITheme.createPrimaryButton("Save Profile Details");
        saveProfileBtn.addActionListener(e -> updateProfile());
        pBtnRow.add(saveProfileBtn);

        profileCard.add(pBtnRow, BorderLayout.SOUTH);
        mainGrid.add(profileCard);

        // ================= RIGHT: Change Password Card =================
        JPanel passCard = UITheme.createCard();
        passCard.setLayout(new BorderLayout(0, 16));

        JLabel passHeader = new JLabel("🔒 Change Account Password");
        passHeader.setFont(UITheme.FONT_SUBTITLE);
        passHeader.setForeground(UITheme.TEXT_MAIN);
        passCard.add(passHeader, BorderLayout.NORTH);

        JPanel passForm = new JPanel(new GridLayout(0, 1, 10, 10));
        passForm.setBackground(UITheme.BG_CARD);

        currentPassField = UITheme.createPasswordField();
        newPassField = UITheme.createPasswordField();
        confirmPassField = UITheme.createPasswordField();

        passForm.add(new JLabel("Current Password: *"));
        passForm.add(currentPassField);
        passForm.add(new JLabel("New Password (Min. 6 characters): *"));
        passForm.add(newPassField);
        passForm.add(new JLabel("Confirm New Password: *"));
        passForm.add(confirmPassField);

        JLabel noteLbl = new JLabel("<html><small style='color:#64748B;'>Your password will be securely hashed with BCrypt. Plain-text passwords are never stored.</small></html>");
        passForm.add(noteLbl);

        passCard.add(passForm, BorderLayout.CENTER);

        JPanel passBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        passBtnRow.setBackground(UITheme.BG_CARD);

        JButton updatePassBtn = UITheme.createPrimaryButton("Update Password");
        updatePassBtn.addActionListener(e -> changePassword());
        passBtnRow.add(updatePassBtn);

        passCard.add(passBtnRow, BorderLayout.SOUTH);
        mainGrid.add(passCard);

        add(mainGrid, BorderLayout.CENTER);

        // Bottom: Log Out Banner
        JPanel logoutBanner = UITheme.createCard();
        logoutBanner.setLayout(new BorderLayout());

        JLabel logoutNote = new JLabel("Signed in as " + (user != null ? user.getUsername() : "") + " (" + (user != null ? user.getRoleName() : "") + ")");
        logoutNote.setFont(UITheme.FONT_BODY);
        logoutNote.setForeground(UITheme.TEXT_MUTED);

        JButton logoutBtn = UITheme.createDangerButton("🚪 Sign Out of Desktop Application");
        logoutBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                if (onLogout != null) onLogout.run();
            }
        });

        logoutBanner.add(logoutNote, BorderLayout.WEST);
        logoutBanner.add(logoutBtn, BorderLayout.EAST);
        add(logoutBanner, BorderLayout.SOUTH);
    }

    private void updateProfile() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (!ValidationUtil.isNotEmpty(name)) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean updated = userDAO.updateProfile(user.getUserId(), name, email, phone);
            if (updated) {
                user.setFullName(name);
                user.setEmail(email);
                user.setPhone(phone);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        String current = new String(currentPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirm = new String(confirmPassField.getPassword());

        if (current.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all password fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            authService.changePassword(current, newPass);
            JOptionPane.showMessageDialog(this, "Password changed successfully! Please use your new password next time you log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            currentPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
