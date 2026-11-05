package com.smarthostel.ui.dashboards;

import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.dao.ComplaintDAO;
import com.smarthostel.dao.FeeDAO;
import com.smarthostel.dao.RoomDAO;
import com.smarthostel.dao.StudentDAO;
import com.smarthostel.model.AuditLog;
import com.smarthostel.service.AuditService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.ui.components.StatCard;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final StudentDAO studentDAO = new StudentDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final FeeDAO feeDAO = new FeeDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final AuditService auditService = new AuditService();

    private StatCard cardStudents;
    private StatCard cardRooms;
    private StatCard cardFees;
    private StatCard cardComplaints;

    private DefaultTableModel activityTableModel;

    public AdminDashboardPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(22, 24, 24, 24));

        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(UITheme.BG_MAIN);

        // 1. Campus Banner Card
        JPanel bannerCard = UITheme.createCard();
        bannerCard.setLayout(new BorderLayout(16, 0));
        bannerCard.setBackground(new Color(15, 23, 42));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel bannerTitle = new JLabel("Campus Administration & Residence Control");
        bannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bannerTitle.setForeground(Color.WHITE);

        JLabel bannerSub = new JLabel("Central monitoring for student residency, live room allocation, and financial auditing.");
        bannerSub.setFont(UITheme.FONT_BODY);
        bannerSub.setForeground(new Color(203, 213, 225));

        bannerText.add(bannerTitle);
        bannerText.add(Box.createVerticalStrut(3));
        bannerText.add(bannerSub);
        bannerCard.add(bannerText, BorderLayout.CENTER);

        topSection.add(bannerCard);
        topSection.add(Box.createVerticalStrut(14));

        // 2. Metric Stat Cards with Progress Bars
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(UITheme.BG_MAIN);

        cardStudents = new StatCard("Total Students", "0", "Enrolled Residents", UITheme.PRIMARY, VectorIcon.Type.STUDENTS);
        cardRooms = new StatCard("Hostel Capacity", "0 / 0", "Occupied / Total Beds", new Color(14, 165, 233), VectorIcon.Type.BED, 0);
        cardFees = new StatCard("Fee Collection", UITheme.formatCurrency(null), "Verified Revenue", UITheme.SUCCESS, VectorIcon.Type.FEES, 0);
        cardComplaints = new StatCard("Open Complaints", "0", "Requires Attention", UITheme.DANGER, VectorIcon.Type.COMPLAINTS);

        statsPanel.add(cardStudents);
        statsPanel.add(cardRooms);
        statsPanel.add(cardFees);
        statsPanel.add(cardComplaints);

        topSection.add(statsPanel);
        add(topSection, BorderLayout.NORTH);

        // Center: Interactive Quick Action Grid + Audit Trail
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setBackground(UITheme.BG_MAIN);

        // Quick Actions Grid
        JPanel actionsCard = UITheme.createCard();
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));

        JLabel actionLabel = new JLabel("Quick Actions: ");
        actionLabel.setFont(UITheme.FONT_BODY_BOLD);
        actionLabel.setForeground(UITheme.TEXT_MAIN);
        actionsCard.add(actionLabel);

        JButton btnAddStudent = UITheme.createPrimaryButton("Register Student", new VectorIcon(VectorIcon.Type.USER_PLUS, 15, Color.WHITE));
        btnAddStudent.addActionListener(e -> onNavigate.accept("STUDENTS"));
        actionsCard.add(btnAddStudent);

        JButton btnRooms = UITheme.createSecondaryButton("Hostel Rooms", new VectorIcon(VectorIcon.Type.BED, 15, UITheme.TEXT_MAIN));
        btnRooms.addActionListener(e -> onNavigate.accept("ROOMS"));
        actionsCard.add(btnRooms);

        JButton btnAllocate = UITheme.createSecondaryButton("Room Allocation", new VectorIcon(VectorIcon.Type.KEY, 15, UITheme.TEXT_MAIN));
        btnAllocate.addActionListener(e -> onNavigate.accept("ALLOCATION"));
        actionsCard.add(btnAllocate);

        JButton btnFees = UITheme.createSecondaryButton("Record Fee Payment", new VectorIcon(VectorIcon.Type.FEES, 15, UITheme.TEXT_MAIN));
        btnFees.addActionListener(e -> onNavigate.accept("FEES"));
        actionsCard.add(btnFees);

        JButton btnComplaints = UITheme.createSecondaryButton("Maintenance Queue", new VectorIcon(VectorIcon.Type.COMPLAINTS, 15, UITheme.TEXT_MAIN));
        btnComplaints.addActionListener(e -> onNavigate.accept("COMPLAINTS"));
        actionsCard.add(btnComplaints);

        JButton btnReports = UITheme.createSecondaryButton("System Reports", new VectorIcon(VectorIcon.Type.REPORTS, 15, UITheme.TEXT_MAIN));
        btnReports.addActionListener(e -> onNavigate.accept("REPORTS"));
        actionsCard.add(btnReports);

        contentPanel.add(actionsCard, BorderLayout.NORTH);

        // Activity Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tableTitle = new JLabel("System Audit Trail & Recent Activities", new VectorIcon(VectorIcon.Type.SHIELD_CHECK, 18, UITheme.PRIMARY), SwingConstants.LEFT);
        tableTitle.setIconTextGap(8);
        tableTitle.setFont(UITheme.FONT_SUBTITLE);
        tableTitle.setForeground(UITheme.TEXT_MAIN);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Log ID", "User", "Action", "Module / Table", "Record ID", "Activity Details", "Timestamp"};
        activityTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        ModernTable activityTable = new ModernTable(activityTableModel);
        JScrollPane scroll = new JScrollPane(activityTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scroll, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        try {
            int students = studentDAO.getTotalStudentCount();
            cardStudents.setValue(String.valueOf(students));

            RoomDAO.RoomStats rStats = roomDAO.getRoomStats();
            cardRooms.setValue(rStats.occupiedBeds + " / " + rStats.totalBeds);
            cardRooms.setSubtitle("Available Beds: " + rStats.availableBeds);
            int occupancyPercent = rStats.totalBeds > 0 ? (rStats.occupiedBeds * 100) / rStats.totalBeds : 0;
            cardRooms.setProgress(occupancyPercent);

            FeeDAO.FeeSummary fStats = feeDAO.getFeeSummary();
            cardFees.setValue(UITheme.formatCurrency(fStats.collectedFees));
            cardFees.setSubtitle("Pending: " + UITheme.formatCurrency(fStats.pendingFees));
            double totalInvoiced = fStats.totalFees.doubleValue();
            int feePercent = totalInvoiced > 0 ? (int) ((fStats.collectedFees.doubleValue() * 100) / totalInvoiced) : 0;
            cardFees.setProgress(feePercent);

            ComplaintDAO.ComplaintSummary cStats = complaintDAO.getComplaintSummary();
            cardComplaints.setValue(String.valueOf(cStats.pending + cStats.inProgress));
            cardComplaints.setSubtitle("Resolved: " + cStats.resolved + " | Total: " + cStats.total);

            // Load audit logs
            activityTableModel.setRowCount(0);
            List<AuditLog> logs = auditService.getRecentLogs(25);
            for (AuditLog l : logs) {
                activityTableModel.addRow(new Object[]{
                        l.getLogId(),
                        l.getUsername(),
                        l.getActionType(),
                        l.getTableAffected(),
                        l.getRecordId() != null ? l.getRecordId() : "-",
                        l.getDetails(),
                        DateUtil.formatDateTime(l.getTimestamp())
                });
            }

        } catch (Exception e) {
            System.err.println("Error loading admin dashboard metrics: " + e.getMessage());
        }
    }
}
