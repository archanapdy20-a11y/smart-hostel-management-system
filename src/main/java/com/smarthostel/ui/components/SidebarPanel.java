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
    private final List<JButton> navButtons = new ArrayList<>();
    private JButton activeButton = null;

    public SidebarPanel(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(240, 0));
        setBackground(UITheme.PRIMARY_DARK);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // 1. Header / Logo Area
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.PRIMARY_DARK);
        brandPanel.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel titleLabel = new JLabel("SMART HOSTEL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Enterprise Management");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(new Color(165, 180, 252)); // Light indigo

        brandPanel.add(titleLabel);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(subtitleLabel);

        // Role badge
        User user = SessionManager.getInstance().getCurrentUser();
        String role = user != null ? user.getRoleName() : "GUEST";

        JPanel rolePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        rolePill.setBackground(new Color(49, 46, 129));
        rolePill.setBorder(new EmptyBorder(4, 8, 4, 8));
        JLabel roleLabel = new JLabel("ROLE: " + role);
        roleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        roleLabel.setForeground(new Color(199, 210, 254));
        rolePill.add(roleLabel);

        brandPanel.add(Box.createVerticalStrut(12));
        brandPanel.add(rolePill);

        add(brandPanel, BorderLayout.NORTH);

        // 2. Navigation List
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setBackground(UITheme.PRIMARY_DARK);
        navContainer.setBorder(new EmptyBorder(10, 12, 10, 12));

        buildRoleNavigation(navContainer, role);

        JScrollPane scrollPane = new JScrollPane(navContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UITheme.PRIMARY_DARK);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_DARK);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bottom Footer (User & Logout)
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.PRIMARY_DARK);
        footer.setBorder(new EmptyBorder(16, 16, 20, 16));

        JButton logoutBtn = new JButton("🚪 Log Out");
        logoutBtn.setFont(UITheme.FONT_BODY_BOLD);
        logoutBtn.setForeground(new Color(254, 202, 202));
        logoutBtn.setBackground(new Color(69, 10, 10));
        logoutBtn.setBorder(new EmptyBorder(10, 14, 10, 14));
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
        // Common to all: Dashboard
        addNavItem(container, "📊 Dashboard", "DASHBOARD", true);

        if ("ADMIN".equalsIgnoreCase(role)) {
            addNavSection(container, "CAMPUS MANAGEMENT");
            addNavItem(container, "👥 Students", "STUDENTS", false);
            addNavItem(container, "🏢 Hostel Rooms", "ROOMS", false);
            addNavItem(container, "🔑 Room Allocation", "ALLOCATION", false);

            addNavSection(container, "OPERATIONS & MESS");
            addNavItem(container, "💳 Fee Management", "FEES", false);
            addNavItem(container, "🚨 Complaints", "COMPLAINTS", false);
            addNavItem(container, "🍽️ Food & Mess Menu", "FOOD_MENU", false);
            addNavItem(container, "⭐ Food Feedback", "FOOD_FEEDBACK", false);

            addNavSection(container, "SYSTEM & ADMINISTRATION");
            addNavItem(container, "🛡️ Staff & Users", "USERS", false);
            addNavItem(container, "📈 System Reports", "REPORTS", false);
            addNavItem(container, "👤 My Profile", "PROFILE", false);

        } else if ("WARDEN".equalsIgnoreCase(role)) {
            addNavSection(container, "RESIDENT SERVICES");
            addNavItem(container, "👥 View Students", "STUDENTS", false);
            addNavItem(container, "🏢 Room Availability", "ROOMS", false);
            addNavItem(container, "🔑 Room Allocation", "ALLOCATION", false);

            addNavSection(container, "MONITORING");
            addNavItem(container, "🚨 Complaints Queue", "COMPLAINTS", false);
            addNavItem(container, "🍽️ View Food Menu", "FOOD_MENU", false);
            addNavItem(container, "⭐ View Food Feedback", "FOOD_FEEDBACK", false);
            addNavItem(container, "📈 Hostel Reports", "REPORTS", false);
            addNavItem(container, "👤 My Profile", "PROFILE", false);

        } else if ("ACCOUNTANT".equalsIgnoreCase(role)) {
            addNavSection(container, "ACCOUNTS & FINANCE");
            addNavItem(container, "👥 View Students", "STUDENTS", false);
            addNavItem(container, "💳 Fee Collection", "FEES", false);
            addNavItem(container, "📈 Financial Reports", "REPORTS", false);
            addNavItem(container, "👤 My Profile", "PROFILE", false);

        } else if ("STUDENT".equalsIgnoreCase(role)) {
            addNavSection(container, "MY HOSTEL");
            addNavItem(container, "🔑 My Allocated Room", "ALLOCATION", false);
            addNavItem(container, "💳 My Fees & Payments", "FEES", false);
            addNavItem(container, "🍽️ Mess Food Menu", "FOOD_MENU", false);
            addNavItem(container, "🚨 Submit Complaint", "COMPLAINTS", false);
            addNavItem(container, "⭐ Food Feedback", "FOOD_FEEDBACK", false);
            addNavItem(container, "👤 My Profile", "PROFILE", false);
        }
    }

    private void addNavSection(JPanel container, String title) {
        container.add(Box.createVerticalStrut(14));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(129, 140, 248)); // Indigo-400
        label.setBorder(new EmptyBorder(4, 10, 4, 10));
        container.add(label);
        container.add(Box.createVerticalStrut(4));
    }

    private void addNavItem(JPanel container, String text, String key, boolean isDefault) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(new Color(224, 231, 255));
        btn.setBackground(UITheme.PRIMARY_DARK);
        btn.setBorder(new EmptyBorder(10, 12, 10, 12));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btn.addActionListener(e -> {
            setActiveButton(btn);
            onNavigate.accept(key);
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) {
                    btn.setBackground(UITheme.SIDEBAR_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) {
                    btn.setBackground(UITheme.PRIMARY_DARK);
                }
            }
        });

        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(2));

        if (isDefault) {
            setActiveButton(btn);
        }
    }

    private void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(UITheme.PRIMARY_DARK);
            activeButton.setForeground(new Color(224, 231, 255));
            activeButton.setFont(UITheme.FONT_BODY);
        }
        activeButton = btn;
        activeButton.setBackground(UITheme.PRIMARY);
        activeButton.setForeground(Color.WHITE);
        activeButton.setFont(UITheme.FONT_BODY_BOLD);
    }
}
