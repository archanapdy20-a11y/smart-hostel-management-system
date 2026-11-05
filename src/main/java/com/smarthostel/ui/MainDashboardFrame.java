package com.smarthostel.ui;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.User;
import com.smarthostel.service.AuthService;
import com.smarthostel.ui.components.HeaderPanel;
import com.smarthostel.ui.components.SidebarPanel;
import com.smarthostel.ui.dashboards.*;
import com.smarthostel.ui.modules.*;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainDashboardFrame extends JFrame {

    private final AuthService authService = new AuthService();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);

    private final Deque<String> backHistory = new ArrayDeque<>();
    private final Deque<String> forwardHistory = new ArrayDeque<>();
    private boolean isNavigatingHistory = false;

    private HeaderPanel headerPanel;
    private SidebarPanel sidebarPanel;

    private String currentModule = "DASHBOARD";

    // Module Panel References
    private JPanel dashboardPanel;
    private StudentManagementPanel studentPanel;
    private RoomManagementPanel roomPanel;
    private RoomAllocationPanel allocationPanel;
    private FeeManagementPanel feePanel;
    private ComplaintManagementPanel complaintPanel;
    private FoodMenuPanel foodMenuPanel;
    private FoodFeedbackPanel foodFeedbackPanel;
    private ReportsPanel reportsPanel;
    private ProfilePanel profilePanel;
    private UserManagementPanel userPanel;

    public MainDashboardFrame() {
        User user = SessionManager.getInstance().getCurrentUser();
        String role = user != null ? user.getRoleName() : "USER";

        setTitle("Smart Hostel Management System - [" + role + " Portal]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        initUI(role);
    }

    private void initUI(String role) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        // 1. Sidebar Panel (WEST)
        sidebarPanel = new SidebarPanel(this::navigateTo, this::handleLogout);
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Main Content Wrapper
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(UITheme.BG_MAIN);

        // Header Panel with forward and backward navigation support
        headerPanel = new HeaderPanel(this::refreshCurrentModule, this::navigateBack, this::navigateForward);
        mainWrapper.add(headerPanel, BorderLayout.NORTH);

        // 3. Central Dynamic Card Area (CENTER)
        contentArea.setBackground(UITheme.BG_MAIN);

        // Initialize role-specific Dashboard
        if ("ADMIN".equalsIgnoreCase(role)) {
            dashboardPanel = new AdminDashboardPanel(this::navigateTo);
        } else if ("WARDEN".equalsIgnoreCase(role)) {
            dashboardPanel = new WardenDashboardPanel(this::navigateTo);
        } else if ("ACCOUNTANT".equalsIgnoreCase(role)) {
            dashboardPanel = new AccountantDashboardPanel(this::navigateTo);
        } else {
            dashboardPanel = new StudentDashboardPanel(this::navigateTo);
        }
        contentArea.add(dashboardPanel, "DASHBOARD");

        // Initialize Module Panels
        studentPanel = new StudentManagementPanel();
        contentArea.add(studentPanel, "STUDENTS");

        roomPanel = new RoomManagementPanel();
        contentArea.add(roomPanel, "ROOMS");

        allocationPanel = new RoomAllocationPanel();
        contentArea.add(allocationPanel, "ALLOCATION");

        feePanel = new FeeManagementPanel();
        contentArea.add(feePanel, "FEES");

        complaintPanel = new ComplaintManagementPanel();
        contentArea.add(complaintPanel, "COMPLAINTS");

        foodMenuPanel = new FoodMenuPanel();
        contentArea.add(foodMenuPanel, "FOOD_MENU");

        foodFeedbackPanel = new FoodFeedbackPanel();
        contentArea.add(foodFeedbackPanel, "FOOD_FEEDBACK");

        reportsPanel = new ReportsPanel();
        contentArea.add(reportsPanel, "REPORTS");

        userPanel = new UserManagementPanel();
        contentArea.add(userPanel, "USERS");

        profilePanel = new ProfilePanel(this::handleLogout);
        contentArea.add(profilePanel, "PROFILE");

        mainWrapper.add(contentArea, BorderLayout.CENTER);
        add(mainWrapper, BorderLayout.CENTER);

        // Set initial header titles
        updateHeaderTitles("DASHBOARD");
    }

    public void navigateTo(String moduleKey) {
        if (!isNavigatingHistory && currentModule != null && !currentModule.equals(moduleKey)) {
            backHistory.push(currentModule);
            forwardHistory.clear();
        }
        this.currentModule = moduleKey;
        cardLayout.show(contentArea, moduleKey);
        updateHeaderTitles(moduleKey);
        if (sidebarPanel != null) {
            sidebarPanel.setActiveItemByKey(moduleKey);
        }
        if (headerPanel != null) {
            headerPanel.updateHistoryButtons(!backHistory.isEmpty(), !forwardHistory.isEmpty());
        }
        refreshCurrentModule();
    }

    public void navigateBack() {
        if (!backHistory.isEmpty()) {
            isNavigatingHistory = true;
            forwardHistory.push(currentModule);
            String prev = backHistory.pop();
            navigateTo(prev);
            isNavigatingHistory = false;
            if (headerPanel != null) {
                headerPanel.updateHistoryButtons(!backHistory.isEmpty(), !forwardHistory.isEmpty());
            }
        }
    }

    public void navigateForward() {
        if (!forwardHistory.isEmpty()) {
            isNavigatingHistory = true;
            backHistory.push(currentModule);
            String next = forwardHistory.pop();
            navigateTo(next);
            isNavigatingHistory = false;
            if (headerPanel != null) {
                headerPanel.updateHistoryButtons(!backHistory.isEmpty(), !forwardHistory.isEmpty());
            }
        }
    }

    private void updateHeaderTitles(String key) {
        switch (key) {
            case "DASHBOARD" -> headerPanel.setTitles("Overview Dashboard", "Home / System Analytics & Key Metrics");
            case "STUDENTS" -> headerPanel.setTitles("Student Directory & Management", "Campus Management / Student Profiles");
            case "ROOMS" -> headerPanel.setTitles("Hostel Blocks & Room Inventory", "Hostel Infrastructure / Room Availability");
            case "ALLOCATION" -> headerPanel.setTitles("Room Allocation & Bed Management", "Resident Services / Active Allotments");
            case "FEES" -> headerPanel.setTitles("Hostel Fees & Payment Receipts", "Finance & Accounts / Student Dues");
            case "COMPLAINTS" -> headerPanel.setTitles("Complaints & Incident Helpdesk", "Hostel Operations / Maintenance Tickets");
            case "FOOD_MENU" -> headerPanel.setTitles("Daily & Weekly Mess Food Menu", "Hostel Mess & Dining / Published Menu");
            case "FOOD_FEEDBACK" -> headerPanel.setTitles("Resident Food Quality Feedback", "Hostel Mess & Dining / Reviews & Ratings");
            case "USERS" -> headerPanel.setTitles("Staff & User Account Management", "System Administration / User Roles & Security");
            case "REPORTS" -> headerPanel.setTitles("System Reports & Data Exports", "Management Reports / Export to CSV");
            case "PROFILE" -> headerPanel.setTitles("My Profile & Security Settings", "User Preferences / Account Details");
        }
    }

    private void refreshCurrentModule() {
        switch (currentModule) {
            case "DASHBOARD" -> {
                if (dashboardPanel instanceof AdminDashboardPanel p) p.loadData();
                else if (dashboardPanel instanceof WardenDashboardPanel p) p.loadData();
                else if (dashboardPanel instanceof AccountantDashboardPanel p) p.loadData();
                else if (dashboardPanel instanceof StudentDashboardPanel p) p.loadData();
            }
            case "STUDENTS" -> studentPanel.loadStudents();
            case "ROOMS" -> roomPanel.loadRooms();
            case "ALLOCATION" -> allocationPanel.loadAllocations();
            case "FEES" -> feePanel.loadData();
            case "COMPLAINTS" -> complaintPanel.loadComplaints();
            case "FOOD_MENU" -> foodMenuPanel.loadMenu();
            case "FOOD_FEEDBACK" -> foodFeedbackPanel.loadFeedback();
            case "USERS" -> userPanel.loadUsers();
            case "PROFILE" -> {}
        }
    }

    private void handleLogout() {
        authService.logout();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
