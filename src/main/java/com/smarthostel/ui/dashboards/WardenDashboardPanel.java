package com.smarthostel.ui.dashboards;

import com.smarthostel.dao.ComplaintDAO;
import com.smarthostel.dao.FoodFeedbackDAO;
import com.smarthostel.dao.RoomDAO;
import com.smarthostel.dao.StudentDAO;
import com.smarthostel.model.Complaint;
import com.smarthostel.service.ComplaintService;
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

        // 1. Warden Residency Overview Banner
        JPanel bannerCard = UITheme.createCard();
        bannerCard.setLayout(new BorderLayout(16, 0));
        bannerCard.setBackground(new Color(15, 23, 42));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel bannerTitle = new JLabel("Hostel Warden Supervision & Facility Management");
        bannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bannerTitle.setForeground(Color.WHITE);

        JLabel bannerSub = new JLabel("Oversee room occupancy, manage daily bed allocations, resolve maintenance grievances, and monitor mess quality.");
        bannerSub.setFont(UITheme.FONT_BODY);
        bannerSub.setForeground(new Color(203, 213, 225));

        bannerText.add(bannerTitle);
        bannerText.add(Box.createVerticalStrut(3));
        bannerText.add(bannerSub);
        bannerCard.add(bannerText, BorderLayout.CENTER);

        topSection.add(bannerCard);
        topSection.add(Box.createVerticalStrut(14));

        // 2. 4 Stat Cards with Progress Bars
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(UITheme.BG_MAIN);

        cardStudents = new StatCard("Hostel Residents", "0", "Enrolled Students", UITheme.PRIMARY, VectorIcon.Type.STUDENTS);
        cardAvailableBeds = new StatCard("Available Beds", "0", "Ready for Allocation", UITheme.SUCCESS, VectorIcon.Type.BED, 0);
        cardPendingComplaints = new StatCard("Open Complaints", "0", "Pending Action", UITheme.DANGER, VectorIcon.Type.COMPLAINTS);
        cardFoodRating = new StatCard("Mess Food Rating", "0.0 / 5.0", "Resident Reviews", UITheme.WARNING, VectorIcon.Type.FOOD_FEEDBACK, 0);

        statsPanel.add(cardStudents);
        statsPanel.add(cardAvailableBeds);
        statsPanel.add(cardPendingComplaints);
        statsPanel.add(cardFoodRating);

        topSection.add(statsPanel);
        add(topSection, BorderLayout.NORTH);

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

        JButton btnManageAlloc = UITheme.createPrimaryButton("Room Allocation", new VectorIcon(VectorIcon.Type.KEY, 15, Color.WHITE));
        btnManageAlloc.addActionListener(e -> onNavigate.accept("ALLOCATION"));
        actionsCard.add(btnManageAlloc);

        JButton btnRooms = UITheme.createSecondaryButton("Room Availability", new VectorIcon(VectorIcon.Type.BED, 15, UITheme.TEXT_MAIN));
        btnRooms.addActionListener(e -> onNavigate.accept("ROOMS"));
        actionsCard.add(btnRooms);

        JButton btnAllComplaints = UITheme.createSecondaryButton("Resolve Complaints", new VectorIcon(VectorIcon.Type.COMPLAINTS, 15, UITheme.TEXT_MAIN));
        btnAllComplaints.addActionListener(e -> onNavigate.accept("COMPLAINTS"));
        actionsCard.add(btnAllComplaints);

        JButton btnViewMenu = UITheme.createSecondaryButton("View Food Menu", new VectorIcon(VectorIcon.Type.FOOD_MENU, 15, UITheme.TEXT_MAIN));
        btnViewMenu.addActionListener(e -> onNavigate.accept("FOOD_MENU"));
        actionsCard.add(btnViewMenu);

        JButton btnFeedback = UITheme.createSecondaryButton("Resident Feedback", new VectorIcon(VectorIcon.Type.STAR, 15, UITheme.TEXT_MAIN));
        btnFeedback.addActionListener(e -> onNavigate.accept("FOOD_FEEDBACK"));
        actionsCard.add(btnFeedback);

        centerPanel.add(actionsCard, BorderLayout.NORTH);

        // Priority Complaints Table
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tableTitle = new JLabel("High Priority & Pending Hostel Complaints", new VectorIcon(VectorIcon.Type.COMPLAINTS, 18, UITheme.DANGER), SwingConstants.LEFT);
        tableTitle.setIconTextGap(8);
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
            cardAvailableBeds.setSubtitle("Total Capacity: " + rStats.totalBeds + " Beds");
            int availablePercent = rStats.totalBeds > 0 ? (rStats.availableBeds * 100) / rStats.totalBeds : 0;
            cardAvailableBeds.setProgress(availablePercent);

            ComplaintDAO.ComplaintSummary cStats = complaintDAO.getComplaintSummary();
            cardPendingComplaints.setValue(String.valueOf(cStats.pending + cStats.inProgress));
            cardPendingComplaints.setSubtitle("Pending: " + cStats.pending + " | In Progress: " + cStats.inProgress);

            double rating = foodFeedbackDAO.getAverageRating();
            cardFoodRating.setValue(String.format("%.1f / 5.0", rating));
            int ratingPercent = (int) ((rating / 5.0) * 100);
            cardFoodRating.setProgress(ratingPercent);

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
