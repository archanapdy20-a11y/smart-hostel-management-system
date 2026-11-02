package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.Role;
import com.smarthostel.model.User;
import com.smarthostel.service.UserService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final UserService userService = new UserService();
    private DefaultTableModel tableModel;
    private ModernTable table;
    private JTextField searchField;
    private JComboBox<String> roleFilterBox;
    private List<User> currentUserList;

    public UserManagementPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadUsers();
    }

    private void initUI() {
        // Toolbar Card
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        // Left: Filter + Search
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(UITheme.BG_CARD);

        JLabel filterLabel = new JLabel("Role Filter:");
        filterLabel.setFont(UITheme.FONT_BODY_BOLD);
        filterPanel.add(filterLabel);

        roleFilterBox = new JComboBox<>(new String[]{"ALL ROLES", "ADMIN", "WARDEN", "ACCOUNTANT", "STUDENT"});
        roleFilterBox.addActionListener(e -> filterUsers());
        filterPanel.add(roleFilterBox);

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Username, name, email...");
        searchField.addActionListener(e -> searchUsers());
        filterPanel.add(searchField);

        JButton btnSearch = UITheme.createSecondaryButton("Search", new VectorIcon(VectorIcon.Type.SEARCH, 14, UITheme.TEXT_MAIN));
        btnSearch.addActionListener(e -> searchUsers());
        filterPanel.add(btnSearch);

        JButton btnReset = UITheme.createSecondaryButton("Reset", new VectorIcon(VectorIcon.Type.REFRESH, 14, UITheme.TEXT_MAIN));
        btnReset.addActionListener(e -> {
            searchField.setText("");
            roleFilterBox.setSelectedIndex(0);
            loadUsers();
        });
        filterPanel.add(btnReset);

        toolbarCard.add(filterPanel, BorderLayout.WEST);

        // Right Actions - Admin only
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(UITheme.BG_CARD);

        JButton btnAdd = UITheme.createPrimaryButton("Add Staff Account", new VectorIcon(VectorIcon.Type.ADD, 14, Color.WHITE));
        btnAdd.addActionListener(e -> showAddStaffDialog());
        actionPanel.add(btnAdd);

        JButton btnEdit = UITheme.createSecondaryButton("Edit User", new VectorIcon(VectorIcon.Type.EDIT, 14, UITheme.TEXT_MAIN));
        btnEdit.addActionListener(e -> editSelectedUser());
        actionPanel.add(btnEdit);

        JButton btnResetPass = UITheme.createSecondaryButton("Reset Password", new VectorIcon(VectorIcon.Type.LOCK, 14, UITheme.TEXT_MAIN));
        btnResetPass.addActionListener(e -> resetSelectedUserPassword());
        actionPanel.add(btnResetPass);

        JButton btnDelete = UITheme.createDangerButton("Delete Account", new VectorIcon(VectorIcon.Type.DELETE, 14, Color.WHITE));
        btnDelete.addActionListener(e -> deleteSelectedUser());
        actionPanel.add(btnDelete);

        toolbarCard.add(actionPanel, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        String[] cols = {"User ID", "Username", "Full Name", "Role", "Email", "Phone", "Status", "Created At"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new ModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void loadUsers() {
        try {
            currentUserList = userService.getAllUsers();
            filterUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterUsers() {
        if (currentUserList == null) return;
        String selectedRole = (String) roleFilterBox.getSelectedItem();
        List<User> filtered = currentUserList;
        if (selectedRole != null && !"ALL ROLES".equalsIgnoreCase(selectedRole)) {
            filtered = currentUserList.stream()
                    .filter(u -> selectedRole.equalsIgnoreCase(u.getRoleName()))
                    .toList();
        }
        populateTable(filtered);
    }

    private void searchUsers() {
        String query = searchField.getText().trim();
        try {
            currentUserList = userService.searchUsers(query);
            filterUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<User> list) {
        tableModel.setRowCount(0);
        for (User u : list) {
            tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getRoleName(),
                    u.getEmail(),
                    u.getPhone() != null ? u.getPhone() : "-",
                    u.getStatus(),
                    DateUtil.formatDateTime(u.getCreatedAt())
            });
        }
    }

    private void showAddStaffDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Staff Account (Admin Only)", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JTextField usernameField = UITheme.createTextField();
        JPasswordField passwordField = UITheme.createPasswordField();
        JTextField nameField = UITheme.createTextField();
        JTextField emailField = UITheme.createTextField();
        JTextField phoneField = UITheme.createTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"WARDEN", "ACCOUNTANT", "ADMIN"});

        form.add(new JLabel("Username: *"));
        form.add(usernameField);
        form.add(new JLabel("Initial Password (Min. 6 chars): *"));
        form.add(passwordField);
        form.add(new JLabel("Full Name: *"));
        form.add(nameField);
        form.add(new JLabel("Email Address: *"));
        form.add(emailField);
        form.add(new JLabel("Phone Number:"));
        form.add(phoneField);
        form.add(new JLabel("Staff Role: *"));
        form.add(roleBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Create Account");
        saveBtn.addActionListener(e -> {
            try {
                String uName = usernameField.getText().trim();
                String pass = new String(passwordField.getPassword());
                String fName = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String role = (String) roleBox.getSelectedItem();

                int roleId = "ADMIN".equalsIgnoreCase(role) ? 1 :
                             "WARDEN".equalsIgnoreCase(role) ? 2 :
                             "ACCOUNTANT".equalsIgnoreCase(role) ? 3 : 4;

                User newUser = new User(0, uName, null, roleId, role, fName, email, phone, "ACTIVE");
                userService.createStaffUser(newUser, pass);

                JOptionPane.showMessageDialog(dialog, "Staff account for '" + uName + "' (" + role + ") created successfully!",
                        "Account Created", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUsers();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error Creating Account", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        User user = currentUserList.stream().filter(u -> u.getUserId() == userId).findFirst().orElse(null);
        if (user == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User - " + user.getUsername(), true);
        dialog.setSize(480, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JTextField uField = UITheme.createTextField();
        uField.setText(user.getUsername());
        uField.setEditable(false);
        uField.setBackground(UITheme.BG_SUBTLE);

        JTextField nameField = UITheme.createTextField();
        nameField.setText(user.getFullName());

        JTextField emailField = UITheme.createTextField();
        emailField.setText(user.getEmail());

        JTextField phoneField = UITheme.createTextField();
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"ADMIN", "WARDEN", "ACCOUNTANT", "STUDENT"});
        roleBox.setSelectedItem(user.getRoleName());

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "SUSPENDED"});
        statusBox.setSelectedItem(user.getStatus());

        form.add(new JLabel("Username (Read-Only):"));
        form.add(uField);
        form.add(new JLabel("Full Name: *"));
        form.add(nameField);
        form.add(new JLabel("Email Address: *"));
        form.add(emailField);
        form.add(new JLabel("Phone Number:"));
        form.add(phoneField);
        form.add(new JLabel("Assigned Role: *"));
        form.add(roleBox);
        form.add(new JLabel("Account Status: *"));
        form.add(statusBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Save Changes");
        saveBtn.addActionListener(e -> {
            try {
                String role = (String) roleBox.getSelectedItem();
                int roleId = "ADMIN".equalsIgnoreCase(role) ? 1 :
                             "WARDEN".equalsIgnoreCase(role) ? 2 :
                             "ACCOUNTANT".equalsIgnoreCase(role) ? 3 : 4;

                user.setFullName(nameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim());
                user.setRoleId(roleId);
                user.setRoleName(role);
                user.setStatus((String) statusBox.getSelectedItem());

                userService.updateUser(user);
                JOptionPane.showMessageDialog(dialog, "User details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUsers();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error Updating User", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void resetSelectedUserPassword() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user whose password to reset.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        String newPassword = JOptionPane.showInputDialog(this,
                "Enter new password for user '" + username + "' (Min 6 characters):",
                "Reset User Password", JOptionPane.QUESTION_MESSAGE);

        if (newPassword != null) {
            if (newPassword.trim().length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                userService.resetUserPassword(userId, newPassword.trim());
                JOptionPane.showMessageDialog(this, "Password for '" + username + "' has been successfully reset!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String role = (String) tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete account: " + username + " (" + role + ")?\nThis action cannot be undone.",
                "Confirm Account Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = userService.deleteUser(userId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Account for '" + username + "' deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot Delete User", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
