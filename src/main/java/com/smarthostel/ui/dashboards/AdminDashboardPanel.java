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
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
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
        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadData();
    }

    private void initUI() {
        // Top: 4 Key Metric Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(UITheme.BG_MAIN);

        cardStudents = new StatCard("Total Students", "0", "Enrolled Residents", UITheme.PRIMARY);
        cardRooms = new StatCard("Hostel Capacity", "0 / 0", "Occupied / Total Beds", UITheme.INFO);
        cardFees = new StatCard("Fee Collection", "₹0", "Pending: ₹0", UITheme.SUCCESS);
        cardComplaints = new StatCard("Open Complaints", "0", "Requires Attention", UITheme.DANGER);

        statsPanel.add(cardStudents);
        statsPanel.add(cardRooms);
        statsPanel.add(cardFees);
        statsPanel.add(cardComplaints);

        add(statsPanel, BorderLayout.NORTH);

        // Center: Quick Actions & Recent Activities
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setBackground(UITheme.BG_MAIN);

        // Quick Actions Bar
        JPanel actionsCard = UITheme.createCard();
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));

        JLabel actionLabel = new JLabel("Quick Actions: ");
        actionLabel.setFont(UITheme.FONT_BODY_BOLD);
        actionLabel.setForeground(UITheme.TEXT_MAIN);
        actionsCard.add(actionLabel);

        JButton btnAddStudent = UITheme.createPrimaryButton("+ Register Student");
        btnAddStudent.addActionListener(e -> onNavigate.accept("STUDENTS"));
        actionsCard.add(btnAddStudent);

        JButton btnAllocate = UITheme.createSecondaryButton("🔑 Allocate Room");
        btnAllocate.addActionListener(e -> onNavigate.accept("ALLOCATION"));
        actionsCard.add(btnAllocate);

        JButton btnFees = UITheme.createSecondaryButton("💳 Record Fee Payment");
        btnFees.addActionListener(e -> onNavigate.accept("FEES"));
        actionsCard.add(btnFees);

        JButton btnComplaints = UITheme.createSecondaryButton("🚨 View Complaints");
        btnComplaints.addActionListener(e -> onNavigate.accept("COMPLAINTS"));
        actionsCard.add(btnComplaints);

        contentPanel.add(actionsCard, BorderLayout.NORTH);

        // Activity Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tableTitle = new JLabel("System Audit Trail & Recent Activities");
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

            FeeDAO.FeeSummary fStats = feeDAO.getFeeSummary();
            cardFees.setValue("₹" + fStats.collectedFees.toPlainString());
            cardFees.setSubtitle("Pending: ₹" + fStats.pendingFees.toPlainString());

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
