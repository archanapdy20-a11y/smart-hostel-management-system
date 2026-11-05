package com.smarthostel.ui.components;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.User;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Sidebar navigation with forward/backward collapse toggle and smooth scrolling
public class SidebarPanel extends JPanel {

    public static final int EXPANDED_WIDTH = 250;
    public static final int COLLAPSED_WIDTH = 70;

    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final List<NavItemData> navItems = new ArrayList<>();
    private final List<JLabel> sectionHeaders = new ArrayList<>();
    private NavItemData activeItem = null;

    private boolean isCollapsed = false;
    private JButton toggleBtn;
    private JPanel titleBox;
    private JLabel roleLabel;
    private JButton logoutBtn;

    private static class NavItemData {
        final JButton button;
        final VectorIcon icon;
        final String label;
        final String key;

        NavItemData(JButton button, VectorIcon icon, String label, String key) {
            this.button = button;
            this.icon = icon;
            this.label = label;
            this.key = key;
        }
    }

    public SidebarPanel(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(EXPANDED_WIDTH, 0));
        setBackground(UITheme.PRIMARY_DARK);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Top brand header with collapse/expand toggle
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.PRIMARY_DARK);
        brandPanel.setBorder(new EmptyBorder(18, 14, 14, 14));

        JPanel headerRow = new JPanel(new BorderLayout(6, 0));
        headerRow.setOpaque(false);

        JPanel logoGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoGroup.setOpaque(false);

        JLabel logoBadge = new JLabel(new VectorIcon(VectorIcon.Type.HOSTEL, 24, new Color(165, 180, 252)));
        titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("SMART HOSTEL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Residence System");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(new Color(148, 163, 184));

        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(1));
        titleBox.add(subtitleLabel);

        logoGroup.add(logoBadge);
        logoGroup.add(titleBox);

        toggleBtn = new JButton(new VectorIcon(VectorIcon.Type.CHEVRON_LEFT, 13, new Color(224, 231, 255)));
        toggleBtn.setPreferredSize(new Dimension(28, 28));
        toggleBtn.setBackground(UITheme.SIDEBAR_HOVER);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder());
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setToolTipText("Collapse Sidebar (Backward)");
        toggleBtn.addActionListener(e -> toggleSidebarCollapse());

        headerRow.add(logoGroup, BorderLayout.CENTER);
        headerRow.add(toggleBtn, BorderLayout.EAST);
        brandPanel.add(headerRow);

        // Role indicator pill
        User user = SessionManager.getInstance().getCurrentUser();
        String role = user != null ? user.getRoleName() : "GUEST";

        JPanel rolePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        rolePill.setBackground(UITheme.SIDEBAR_HOVER);
        rolePill.setBorder(new EmptyBorder(4, 8, 4, 8));

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

        roleLabel = new JLabel("ROLE: " + role);
        roleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        roleLabel.setForeground(new Color(224, 231, 255));
        rolePill.add(statusDot);
        rolePill.add(roleLabel);

        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(rolePill);
        add(brandPanel, BorderLayout.NORTH);

        // Scrollable navigation menu container
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setBackground(UITheme.PRIMARY_DARK);
        navContainer.setBorder(new EmptyBorder(6, 10, 10, 10));

        buildRoleNavigation(navContainer, role);

        JScrollPane scrollPane = new JScrollPane(navContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UITheme.PRIMARY_DARK);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_DARK);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(99, 102, 241, 140);
                this.trackColor = UITheme.PRIMARY_DARK;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                return btn;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99, 102, 241, 160));
                g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 2, thumbBounds.width - 2, thumbBounds.height - 4, 6, 6);
                g2.dispose();
            }
        });
        add(scrollPane, BorderLayout.CENTER);

        // Bottom logout action footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.PRIMARY_DARK);
        footer.setBorder(new EmptyBorder(10, 10, 14, 10));

        logoutBtn = new JButton("Sign Out", new VectorIcon(VectorIcon.Type.LOGOUT, 16, new Color(248, 113, 113)));
        logoutBtn.setFont(UITheme.FONT_BODY_BOLD);
        logoutBtn.setForeground(new Color(254, 202, 202));
        logoutBtn.setBackground(new Color(69, 10, 10));
        logoutBtn.setBorder(new EmptyBorder(9, 12, 9, 12));
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

    public void toggleSidebarCollapse() {
        isCollapsed = !isCollapsed;
        int targetWidth = isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH;
        setPreferredSize(new Dimension(targetWidth, 0));

        titleBox.setVisible(!isCollapsed);
        roleLabel.setVisible(!isCollapsed);
        for (JLabel sec : sectionHeaders) {
            sec.setVisible(!isCollapsed);
        }

        if (isCollapsed) {
            toggleBtn.setIcon(new VectorIcon(VectorIcon.Type.CHEVRON_RIGHT, 13, new Color(224, 231, 255)));
            toggleBtn.setToolTipText("Expand Sidebar (Forward)");
            logoutBtn.setText("");
            logoutBtn.setHorizontalAlignment(SwingConstants.CENTER);
            logoutBtn.setIconTextGap(0);
            logoutBtn.setToolTipText("Sign Out");
            for (NavItemData item : navItems) {
                item.button.setText("");
                item.button.setHorizontalAlignment(SwingConstants.CENTER);
                item.button.setIconTextGap(0);
                item.button.setToolTipText(item.label);
            }
        } else {
            toggleBtn.setIcon(new VectorIcon(VectorIcon.Type.CHEVRON_LEFT, 13, new Color(224, 231, 255)));
            toggleBtn.setToolTipText("Collapse Sidebar (Backward)");
            logoutBtn.setText("Sign Out");
            logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
            logoutBtn.setIconTextGap(10);
            logoutBtn.setToolTipText(null);
            for (NavItemData item : navItems) {
                item.button.setText(item.label);
                item.button.setHorizontalAlignment(SwingConstants.LEFT);
                item.button.setIconTextGap(12);
                item.button.setToolTipText(null);
            }
        }

        revalidate();
        repaint();
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
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
            addNavItem(container, "My Allocated Room", "ALLOCATION", VectorIcon.Type.BED, false);
            addNavItem(container, "My Fees & Payments", "FEES", VectorIcon.Type.FEES, false);
            addNavItem(container, "Mess Food Menu", "FOOD_MENU", VectorIcon.Type.FOOD_MENU, false);
            addNavItem(container, "Submit Complaint", "COMPLAINTS", VectorIcon.Type.COMPLAINTS, false);
            addNavItem(container, "Food Feedback", "FOOD_FEEDBACK", VectorIcon.Type.FOOD_FEEDBACK, false);
            addNavItem(container, "My Profile", "PROFILE", VectorIcon.Type.PROFILE, false);
        }
    }

    private void addNavSection(JPanel container, String title) {
        container.add(Box.createVerticalStrut(10));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(148, 163, 184));
        label.setBorder(new EmptyBorder(3, 8, 3, 8));
        sectionHeaders.add(label);
        container.add(label);
        container.add(Box.createVerticalStrut(3));
    }

    private void addNavItem(JPanel container, String text, String key, VectorIcon.Type iconType, boolean isDefault) {
        VectorIcon icon = new VectorIcon(iconType, 16, new Color(165, 180, 252));
        JButton btn = new JButton(text, icon);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(new Color(224, 231, 255));
        btn.setBackground(UITheme.PRIMARY_DARK);
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));
        btn.setIconTextGap(12);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        NavItemData itemData = new NavItemData(btn, icon, text, key);

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

    public void setActiveItemByKey(String key) {
        if (key == null) return;
        for (NavItemData item : navItems) {
            if (key.equalsIgnoreCase(item.key)) {
                setActiveItem(item);
                break;
            }
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
