package com.smarthostel.ui.dashboards;

import com.smarthostel.dao.ComplaintDAO;
import com.smarthostel.dao.FoodFeedbackDAO;
import com.smarthostel.dao.RoomDAO;
import com.smarthostel.dao.StudentDAO;
import com.smarthostel.model.Complaint;
import com.smarthostel.service.ComplaintService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.ui.components.StatCard;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class WardenDashboardPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final StudentDAO studentDAO = new StudentDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final ComplaintService complaintService = new ComplaintService();
    private final FoodFeedbackDAO foodFeedbackDAO = new FoodFeedbackDAO();

    private StatCard cardStudents;
    private StatCard cardAvailableBeds;
    private StatCard cardPendingComplaints;
    private StatCard cardFoodRating;

    private DefaultTableModel complaintsTableModel;

    public WardenDashboardPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadData();
    }

    private void initUI() {
        // Top: 4 Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(UITheme.BG_MAIN);

        cardStudents = new StatCard("Hostel Residents", "0", "Enrolled Students", UITheme.PRIMARY);
        cardAvailableBeds = new StatCard("Available Beds", "0", "Ready for Allocation", UITheme.SUCCESS);
        cardPendingComplaints = new StatCard("Open Complaints", "0", "Pending Action", UITheme.DANGER);
        cardFoodRating = new StatCard("Mess Food Rating", "0.0 ★", "Based on Resident Reviews", UITheme.WARNING);

        statsPanel.add(cardStudents);
        statsPanel.add(cardAvailableBeds);
        statsPanel.add(cardPendingComplaints);
        statsPanel.add(cardFoodRating);

        add(statsPanel, BorderLayout.NORTH);

        // Center: Complaints requiring Warden attention
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UITheme.BG_MAIN);

        // Quick Actions
        JPanel actionsCard = UITheme.createCard();
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));

        JLabel actionLabel = new JLabel("Warden Quick Links: ");
        actionLabel.setFont(UITheme.FONT_BODY_BOLD);
        actionLabel.setForeground(UITheme.TEXT_MAIN);
        actionsCard.add(actionLabel);

        JButton btnManageAlloc = UITheme.createPrimaryButton("🔑 Room Allocation");
        btnManageAlloc.addActionListener(e -> onNavigate.accept("ALLOCATION"));
        actionsCard.add(btnManageAlloc);

        JButton btnAllComplaints = UITheme.createSecondaryButton("🚨 Resolve Complaints");
        btnAllComplaints.addActionListener(e -> onNavigate.accept("COMPLAINTS"));
        actionsCard.add(btnAllComplaints);

        JButton btnViewMenu = UITheme.createSecondaryButton("🍽️ View Food Menu");
        btnViewMenu.addActionListener(e -> onNavigate.accept("FOOD_MENU"));
        actionsCard.add(btnViewMenu);

        JButton btnFeedback = UITheme.createSecondaryButton("⭐ Resident Feedback");
        btnFeedback.addActionListener(e -> onNavigate.accept("FOOD_FEEDBACK"));
        actionsCard.add(btnFeedback);

        centerPanel.add(actionsCard, BorderLayout.NORTH);

        // Priority Complaints Table
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tableTitle = new JLabel("High Priority & Pending Hostel Complaints");
        tableTitle.setFont(UITheme.FONT_SUBTITLE);
        tableTitle.setForeground(UITheme.TEXT_MAIN);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Student", "Reg No", "Room", "Category", "Priority", "Status", "Reported At"};
        complaintsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        ModernTable table = new ModernTable(complaintsTableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        try {
            int students = studentDAO.getTotalStudentCount();
            cardStudents.setValue(String.valueOf(students));

            RoomDAO.RoomStats rStats = roomDAO.getRoomStats();
            cardAvailableBeds.setValue(String.valueOf(rStats.availableBeds));
            cardAvailableBeds.setSubtitle("Total Beds: " + rStats.totalBeds);

            ComplaintDAO.ComplaintSummary cStats = complaintDAO.getComplaintSummary();
            cardPendingComplaints.setValue(String.valueOf(cStats.pending + cStats.inProgress));
            cardPendingComplaints.setSubtitle("Pending: " + cStats.pending + " | In Progress: " + cStats.inProgress);

            double rating = foodFeedbackDAO.getAverageRating();
            cardFoodRating.setValue(rating + " ★");

            // Complaints
            complaintsTableModel.setRowCount(0);
            List<Complaint> list = complaintService.getAllComplaints("Pending");
            for (Complaint c : list) {
                complaintsTableModel.addRow(new Object[]{
                        c.getComplaintId(),
                        c.getStudentName(),
                        c.getRegisterNumber(),
                        c.getRoomNumber(),
                        c.getCategory(),
                        c.getPriority(),
                        c.getStatus(),
                        DateUtil.formatDateTime(c.getSubmittedAt())
                });
            }

        } catch (Exception e) {
            System.err.println("Error loading warden dashboard: " + e.getMessage());
        }
    }
}
