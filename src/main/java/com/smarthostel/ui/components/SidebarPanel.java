package com.smarthostel.ui.components;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.User;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final List<NavItemData> navItems = new ArrayList<>();
    private NavItemData activeItem = null;

    private static class NavItemData {
        final JButton button;
        final VectorIcon icon;
        final String key;

        NavItemData(JButton button, VectorIcon icon, String key) {
            this.button = button;
            this.icon = icon;
            this.key = key;
        }
    }

    public SidebarPanel(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(250, 0));
        setBackground(UITheme.PRIMARY_DARK);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // 1. Brand Logo Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.PRIMARY_DARK);
        brandPanel.setBorder(new EmptyBorder(22, 18, 18, 18));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoRow.setOpaque(false);

        JLabel logoBadge = new JLabel(new VectorIcon(VectorIcon.Type.HOSTEL, 28, new Color(165, 180, 252)));
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("SMART HOSTEL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Residence System");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(new Color(148, 163, 184));

        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitleLabel);

        logoRow.add(logoBadge);
        logoRow.add(titleBox);
        brandPanel.add(logoRow);

        // Role indicator pill
        User user = SessionManager.getInstance().getCurrentUser();
        String role = user != null ? user.getRoleName() : "GUEST";

        JPanel rolePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        rolePill.setBackground(UITheme.SIDEBAR_HOVER);
        rolePill.setBorder(new EmptyBorder(4, 10, 4, 10));

        JLabel statusDot = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SUCCESS);
                g2.fillOval(0, 2, 8, 8);
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(8, 12);
            }
        };

        JLabel roleLabel = new JLabel("ROLE: " + role);
        roleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        roleLabel.setForeground(new Color(224, 231, 255));
        rolePill.add(statusDot);
        rolePill.add(roleLabel);

        brandPanel.add(Box.createVerticalStrut(14));
        brandPanel.add(rolePill);

        add(brandPanel, BorderLayout.NORTH);

        // 2. Scrollable Navigation Menu
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setBackground(UITheme.PRIMARY_DARK);
        navContainer.setBorder(new EmptyBorder(6, 12, 10, 12));

        buildRoleNavigation(navContainer, role);

        JScrollPane scrollPane = new JScrollPane(navContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UITheme.PRIMARY_DARK);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_DARK);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bottom Logout Button
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.PRIMARY_DARK);
        footer.setBorder(new EmptyBorder(14, 14, 18, 14));

        JButton logoutBtn = new JButton("Sign Out", new VectorIcon(VectorIcon.Type.LOGOUT, 16, new Color(248, 113, 113)));
        logoutBtn.setFont(UITheme.FONT_BODY_BOLD);
        logoutBtn.setForeground(new Color(254, 202, 202));
        logoutBtn.setBackground(new Color(69, 10, 10));
        logoutBtn.setBorder(new EmptyBorder(9, 14, 9, 14));
        logoutBtn.setIconTextGap(10);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out of the system?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                onLogout.run();
            }
        });

        footer.add(logoutBtn, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void buildRoleNavigation(JPanel container, String role) {
        addNavItem(container, "Dashboard", "DASHBOARD", VectorIcon.Type.DASHBOARD, true);

        if ("ADMIN".equalsIgnoreCase(role)) {
            addNavSection(container, "CAMPUS MANAGEMENT");
            addNavItem(container, "Students", "STUDENTS", VectorIcon.Type.STUDENTS, false);
            addNavItem(container, "Hostel Rooms", "ROOMS", VectorIcon.Type.ROOMS, false);
            addNavItem(container, "Room Allocation", "ALLOCATION", VectorIcon.Type.ALLOCATION, false);

            addNavSection(container, "OPERATIONS & MESS");
            addNavItem(container, "Fee Management", "FEES", VectorIcon.Type.FEES, false);
            addNavItem(container, "Complaints", "COMPLAINTS", VectorIcon.Type.COMPLAINTS, false);
            addNavItem(container, "Food & Mess Menu", "FOOD_MENU", VectorIcon.Type.FOOD_MENU, false);
            addNavItem(container, "Food Feedback", "FOOD_FEEDBACK", VectorIcon.Type.FOOD_FEEDBACK, false);

            addNavSection(container, "SYSTEM & ADMINISTRATION");
            addNavItem(container, "Staff & Users", "USERS", VectorIcon.Type.USERS, false);
            addNavItem(container, "System Reports", "REPORTS", VectorIcon.Type.REPORTS, false);
            addNavItem(container, "My Profile", "PROFILE", VectorIcon.Type.PROFILE, false);

        } else if ("WARDEN".equalsIgnoreCase(role)) {
            addNavSection(container, "RESIDENT SERVICES");
            addNavItem(container, "View Students", "STUDENTS", VectorIcon.Type.STUDENTS, false);
            addNavItem(container, "Room Availability", "ROOMS", VectorIcon.Type.ROOMS, false);
            addNavItem(container, "Room Allocation", "ALLOCATION", VectorIcon.Type.ALLOCATION, false);

            addNavSection(container, "MONITORING");
            addNavItem(container, "Complaints Queue", "COMPLAINTS", VectorIcon.Type.COMPLAINTS, false);
            addNavItem(container, "View Food Menu", "FOOD_MENU", VectorIcon.Type.FOOD_MENU, false);
            addNavItem(container, "View Food Feedback", "FOOD_FEEDBACK", VectorIcon.Type.FOOD_FEEDBACK, false);
            addNavItem(container, "Hostel Reports", "REPORTS", VectorIcon.Type.REPORTS, false);
            addNavItem(container, "My Profile", "PROFILE", VectorIcon.Type.PROFILE, false);

        } else if ("ACCOUNTANT".equalsIgnoreCase(role)) {
            addNavSection(container, "ACCOUNTS & FINANCE");
            addNavItem(container, "View Students", "STUDENTS", VectorIcon.Type.STUDENTS, false);
            addNavItem(container, "Fee Collection", "FEES", VectorIcon.Type.FEES, false);
            addNavItem(container, "Financial Reports", "REPORTS", VectorIcon.Type.REPORTS, false);
            addNavItem(container, "My Profile", "PROFILE", VectorIcon.Type.PROFILE, false);

        } else if ("STUDENT".equalsIgnoreCase(role)) {
            addNavSection(container, "MY HOSTEL");
            addNavItem(container, "My Allocated Room", "ALLOCATION", VectorIcon.Type.ROOMS, false);
            addNavItem(container, "My Fees & Payments", "FEES", VectorIcon.Type.FEES, false);
            addNavItem(container, "Mess Food Menu", "FOOD_MENU", VectorIcon.Type.FOOD_MENU, false);
            addNavItem(container, "Submit Complaint", "COMPLAINTS", VectorIcon.Type.COMPLAINTS, false);
            addNavItem(container, "Food Feedback", "FOOD_FEEDBACK", VectorIcon.Type.FOOD_FEEDBACK, false);
            addNavItem(container, "My Profile", "PROFILE", VectorIcon.Type.PROFILE, false);
        }
    }

    private void addNavSection(JPanel container, String title) {
        container.add(Box.createVerticalStrut(12));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(148, 163, 184));
        label.setBorder(new EmptyBorder(3, 8, 3, 8));
        container.add(label);
        container.add(Box.createVerticalStrut(3));
    }

    private void addNavItem(JPanel container, String text, String key, VectorIcon.Type iconType, boolean isDefault) {
        VectorIcon icon = new VectorIcon(iconType, 17, new Color(165, 180, 252));
        JButton btn = new JButton(text, icon);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(new Color(224, 231, 255));
        btn.setBackground(UITheme.PRIMARY_DARK);
        btn.setBorder(new EmptyBorder(9, 12, 9, 12));
        btn.setIconTextGap(12);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        NavItemData itemData = new NavItemData(btn, icon, key);

        btn.addActionListener(e -> {
            setActiveItem(itemData);
            onNavigate.accept(key);
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (itemData != activeItem) {
                    btn.setBackground(UITheme.SIDEBAR_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (itemData != activeItem) {
                    btn.setBackground(UITheme.PRIMARY_DARK);
                }
            }
        });

        navItems.add(itemData);
        container.add(btn);
        container.add(Box.createVerticalStrut(2));

        if (isDefault) {
            setActiveItem(itemData);
        }
    }

    private void setActiveItem(NavItemData item) {
        if (activeItem != null) {
            activeItem.button.setBackground(UITheme.PRIMARY_DARK);
            activeItem.button.setForeground(new Color(224, 231, 255));
            activeItem.button.setFont(UITheme.FONT_BODY);
            activeItem.icon.setColor(new Color(165, 180, 252));
            activeItem.button.repaint();
        }
        activeItem = item;
        activeItem.button.setBackground(UITheme.PRIMARY);
        activeItem.button.setForeground(Color.WHITE);
        activeItem.button.setFont(UITheme.FONT_BODY_BOLD);
        activeItem.icon.setColor(Color.WHITE);
        activeItem.button.repaint();
    }
}
