package com.smarthostel.ui;

import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.model.User;
import com.smarthostel.service.AuthService;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Enterprise split-screen login frame with role quick-fill and security workflows
public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JButton loginButton;
    private JLabel errorLabel;
    private JLabel dbStatusLabel;

    public LoginFrame() {
        setTitle("Smart Hostel Management System - Portal Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setResizable(false);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        rootPanel.setBackground(UITheme.BG_MAIN);

        // 1. Left Hero Branding Panel
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), getHeight(), new Color(30, 27, 75));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        heroPanel.setLayout(new BorderLayout());
        heroPanel.setBorder(new EmptyBorder(40, 36, 36, 36));

        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        // Hostel Logo Badge & Titles
        JLabel logoBadge = new JLabel(new VectorIcon(VectorIcon.Type.HOSTEL, 36, new Color(199, 210, 254)));
        JLabel heroTitle = new JLabel("SMART HOSTEL");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heroTitle.setForeground(Color.WHITE);

        JLabel heroSubtitle = new JLabel("Integrated Campus Management System");
        heroSubtitle.setFont(UITheme.FONT_BODY);
        heroSubtitle.setForeground(new Color(148, 163, 184));

        heroContent.add(logoBadge);
        heroContent.add(Box.createVerticalStrut(14));
        heroContent.add(heroTitle);
        heroContent.add(Box.createVerticalStrut(4));
        heroContent.add(heroSubtitle);
        heroContent.add(Box.createVerticalStrut(32));

        // Feature Highlights
        addFeatureRow(heroContent, "Role-Based Portals", "Admin, Warden, Accountant & Student access");
        addFeatureRow(heroContent, "Room & Bed Allocation", "Transaction-safe real-time bed allotment");
        addFeatureRow(heroContent, "Hostel Fee Ledger", "Digital dues tracking, UPI/Cash receipts");
        addFeatureRow(heroContent, "Dining Mess & Helpdesk", "Daily menus, reviews and maintenance queue");

        heroPanel.add(heroContent, BorderLayout.CENTER);

        // Security Footer
        JPanel heroFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        heroFooter.setOpaque(false);
        JLabel secIcon = new JLabel(new VectorIcon(VectorIcon.Type.SHIELD_CHECK, 16, new Color(52, 211, 153)));
        JLabel secLabel = new JLabel("BCrypt 256-Bit Password Encryption");
        secLabel.setFont(UITheme.FONT_SMALL);
        secLabel.setForeground(new Color(148, 163, 184));
        heroFooter.add(secIcon);
        heroFooter.add(secLabel);
        heroPanel.add(heroFooter, BorderLayout.SOUTH);

        rootPanel.add(heroPanel);

        // 2. Right Sign-In Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UITheme.BG_CARD);
        formPanel.setBorder(new EmptyBorder(36, 44, 36, 44));

        JLabel signInTitle = new JLabel("Portal Sign In");
        signInTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signInTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel signInSub = new JLabel("Enter your campus credentials to access your dashboard");
        signInSub.setFont(UITheme.FONT_SMALL);
        signInSub.setForeground(UITheme.TEXT_MUTED);

        formPanel.add(signInTitle);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(signInSub);
        formPanel.add(Box.createVerticalStrut(16));

        // Quick Demo Role Selector Pills
        JLabel quickLbl = new JLabel("Quick Demo Sign-In (Click role to auto-fill):");
        quickLbl.setFont(UITheme.FONT_SMALL_BOLD);
        quickLbl.setForeground(UITheme.PRIMARY);
        formPanel.add(quickLbl);
        formPanel.add(Box.createVerticalStrut(6));

        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pillsPanel.setOpaque(false);
        pillsPanel.add(createRolePill("Admin", "admin", "admin123"));
        pillsPanel.add(createRolePill("Warden", "warden", "warden123"));
        pillsPanel.add(createRolePill("Accountant", "accountant", "account123"));
        pillsPanel.add(createRolePill("Student", "student", "student123"));
        formPanel.add(pillsPanel);
        formPanel.add(Box.createVerticalStrut(16));

        // Username Field
        JLabel userLbl = new JLabel("Username / Account ID:", new VectorIcon(VectorIcon.Type.USERS, 14, UITheme.TEXT_MUTED), SwingConstants.LEFT);
        userLbl.setIconTextGap(6);
        userLbl.setFont(UITheme.FONT_BODY_BOLD);
        userLbl.setForeground(UITheme.TEXT_MAIN);

        usernameField = UITheme.createTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        usernameField.putClientProperty("JTextField.placeholderText", "Enter your username");

        formPanel.add(userLbl);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(14));

        // Password Field
        JLabel passLbl = new JLabel("Password:", new VectorIcon(VectorIcon.Type.LOCK, 14, UITheme.TEXT_MUTED), SwingConstants.LEFT);
        passLbl.setIconTextGap(6);
        passLbl.setFont(UITheme.FONT_BODY_BOLD);
        passLbl.setForeground(UITheme.TEXT_MAIN);

        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");

        formPanel.add(passLbl);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(8));

        // Show Password & Forgot Password row
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);
        optionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(UITheme.FONT_SMALL);
        showPasswordCheck.setForeground(UITheme.TEXT_MUTED);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setFocusPainted(false);
        showPasswordCheck.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '\u2022');
        });

        JButton forgotPassBtn = new JButton("Forgot Password?");
        forgotPassBtn.setFont(UITheme.FONT_SMALL_BOLD);
        forgotPassBtn.setForeground(UITheme.PRIMARY);
        forgotPassBtn.setBorderPainted(false);
        forgotPassBtn.setContentAreaFilled(false);
        forgotPassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassBtn.addActionListener(e -> showForgotPasswordDialog());

        optionsRow.add(showPasswordCheck, BorderLayout.WEST);
        optionsRow.add(forgotPassBtn, BorderLayout.EAST);
        formPanel.add(optionsRow);
        formPanel.add(Box.createVerticalStrut(6));

        // Error message banner
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL_BOLD);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(10));

        // Sign In Button
        loginButton = UITheme.createPrimaryButton("Sign In to Portal", new VectorIcon(VectorIcon.Type.CHECK, 15, Color.WHITE));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.addActionListener(e -> performLogin());
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(14));

        // Enter key listeners
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

        // Database Connectivity Status
        boolean dbOk = DatabaseConnection.testConnection();
        JPanel dbStatusRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        dbStatusRow.setOpaque(false);

        JLabel statusDot = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dbOk ? UITheme.SUCCESS : UITheme.DANGER);
                g2.fillOval(0, 3, 8, 8);
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(8, 14);
            }
        };

        dbStatusLabel = new JLabel(dbOk ? "Database: Connected (smart_hostel_db)" : "Database: Offline (Verify MySQL 3306)");
        dbStatusLabel.setFont(UITheme.FONT_SMALL);
        dbStatusLabel.setForeground(dbOk ? new Color(22, 101, 52) : UITheme.DANGER);

        dbStatusRow.add(statusDot);
        dbStatusRow.add(dbStatusLabel);
        formPanel.add(dbStatusRow);

        rootPanel.add(formPanel);
        add(rootPanel);
    }

    private JButton createRolePill(String roleName, String username, String password) {
        JButton btn = new JButton(roleName);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(new Color(67, 56, 202));
        btn.setBackground(new Color(238, 242, 255));
        btn.setBorder(new CompoundBorder(new LineBorder(new Color(199, 210, 254), 1), new EmptyBorder(4, 10, 4, 10)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            usernameField.setText(username);
            passwordField.setText(password);
            errorLabel.setText(" ");
        });
        return btn;
    }

    private void addFeatureRow(JPanel parent, String title, String desc) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        JLabel chk = new JLabel(new VectorIcon(VectorIcon.Type.CHECK, 13, new Color(52, 211, 153)));
        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(UITheme.FONT_BODY_BOLD);
        tLbl.setForeground(Color.WHITE);

        JLabel dLbl = new JLabel(desc);
        dLbl.setFont(UITheme.FONT_SMALL);
        dLbl.setForeground(new Color(148, 163, 184));

        textBox.add(tLbl);
        textBox.add(dLbl);

        row.add(chk);
        row.add(textBox);

        parent.add(row);
        parent.add(Box.createVerticalStrut(14));
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
        dialog.setSize(440, 400);
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
