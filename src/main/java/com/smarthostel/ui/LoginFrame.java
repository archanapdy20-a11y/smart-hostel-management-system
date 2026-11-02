package com.smarthostel.ui;

import com.smarthostel.model.User;
import com.smarthostel.service.AuthService;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("Smart Hostel Management System - Enterprise Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 620);
        setResizable(false);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.BG_MAIN);
        rootPanel.setBorder(new EmptyBorder(32, 40, 32, 40));

        JPanel cardPanel = UITheme.createCard();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        // 1. Logo & App Branding Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.BG_CARD);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComponent logoBadge = UITheme.createIconCircle(VectorIcon.Type.HOSTEL, UITheme.PRIMARY, 64, 32);
        logoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("SMART HOSTEL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(UITheme.PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enterprise Desktop System");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(logoBadge);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(titleLabel);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(subtitleLabel);

        cardPanel.add(brandPanel);
        cardPanel.add(Box.createVerticalStrut(20));

        // 2. Form Fields
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(UITheme.FONT_BODY_BOLD);
        userLbl.setForeground(UITheme.TEXT_MAIN);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = UITheme.createTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.putClientProperty("JTextField.placeholderText", "Enter your username");

        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(UITheme.FONT_BODY_BOLD);
        passLbl.setForeground(UITheme.TEXT_MAIN);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(UITheme.FONT_SMALL);
        showPasswordCheck.setForeground(UITheme.TEXT_MUTED);
        showPasswordCheck.setBackground(UITheme.BG_CARD);
        showPasswordCheck.setFocusPainted(false);
        showPasswordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022');
            }
        });

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL_BOLD);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button with icon
        loginButton = UITheme.createPrimaryButton("Sign In to System", new VectorIcon(VectorIcon.Type.CHECK, 16, Color.WHITE));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());

        // Key Listeners for Enter key
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        // Forgot password link
        JButton forgotPassBtn = new JButton("Forgot Password?");
        forgotPassBtn.setFont(UITheme.FONT_SMALL);
        forgotPassBtn.setForeground(UITheme.PRIMARY);
        forgotPassBtn.setBorderPainted(false);
        forgotPassBtn.setContentAreaFilled(false);
        forgotPassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPassBtn.addActionListener(e -> showForgotPasswordDialog());

        // Role hints
        JLabel hintLabel = new JLabel("<html><center><small style='color:#94A3B8;'>" +
                "Demo Accounts: admin/admin123 | warden/warden123<br>" +
                "accountant/account123 | student/student123" +
                "</small></center></html>");
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to card
        cardPanel.add(userLbl);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(usernameField);
        cardPanel.add(Box.createVerticalStrut(14));
        cardPanel.add(passLbl);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(showPasswordCheck);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(errorLabel);
        cardPanel.add(Box.createVerticalStrut(14));
        cardPanel.add(loginButton);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(forgotPassBtn);
        cardPanel.add(Box.createVerticalStrut(16));
        cardPanel.add(hintLabel);

        rootPanel.add(cardPanel, BorderLayout.CENTER);
        add(rootPanel);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        errorLabel.setText("Authenticating credentials...");
        loginButton.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                User user = authService.login(username, password);
                errorLabel.setText(" ");

                MainDashboardFrame mainFrame = new MainDashboardFrame();
                mainFrame.setVisible(true);

                dispose();

            } catch (Exception ex) {
                errorLabel.setText("<html>" + ex.getMessage() + "</html>");
                passwordField.setText("");
                loginButton.setEnabled(true);
            }
        });
    }

    private void showForgotPasswordDialog() {
        JDialog dialog = new JDialog(this, "Reset Account Password", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JTextField uField = UITheme.createTextField();
        JTextField emailField = UITheme.createTextField();
        JPasswordField newPass = UITheme.createPasswordField();

        form.add(new JLabel("Registered Username: *"));
        form.add(uField);
        form.add(new JLabel("Registered Email Address: *"));
        form.add(emailField);
        form.add(new JLabel("New Password (Min. 6 characters): *"));
        form.add(newPass);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton resetBtn = UITheme.createPrimaryButton("Reset Password", new VectorIcon(VectorIcon.Type.LOCK, 15, Color.WHITE));
        resetBtn.addActionListener(e -> {
            try {
                authService.resetPassword(uField.getText().trim(), emailField.getText().trim(), new String(newPass.getPassword()));
                JOptionPane.showMessageDialog(dialog, "Password reset successfully! You can now log in with your new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Reset Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(resetBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
