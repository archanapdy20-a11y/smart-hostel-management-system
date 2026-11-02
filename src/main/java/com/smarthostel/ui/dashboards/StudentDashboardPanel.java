package com.smarthostel.ui.dashboards;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.NotificationDAO;
import com.smarthostel.model.*;
import com.smarthostel.service.*;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class StudentDashboardPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final AllocationService allocationService = new AllocationService();
    private final FeeService feeService = new FeeService();
    private final ComplaintService complaintService = new ComplaintService();
    private final FoodService foodService = new FoodService();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    private JLabel roomValLabel;
    private JLabel roomSubLabel;
    private JLabel feeValLabel;
    private JLabel feeSubLabel;
    private JLabel complaintValLabel;
    private JLabel complaintSubLabel;

    private JPanel menuContainer;
    private JPanel notificationContainer;

    public StudentDashboardPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadData();
    }

    private void initUI() {
        // Welcome banner
        User user = SessionManager.getInstance().getCurrentUser();
        Student student = SessionManager.getInstance().getCurrentStudent();

        JPanel banner = UITheme.createCard();
        banner.setLayout(new BorderLayout());
        banner.setBackground(UITheme.PRIMARY_DARK);

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setBackground(UITheme.PRIMARY_DARK);

        String residentName = student != null ? student.getFullName() : (user != null ? user.getFullName() : "Resident");
        JLabel welcomeLabel = new JLabel("Welcome back, " + residentName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);

        String reg = student != null ? student.getRegisterNumber() + " | " + student.getDepartment() + " (Year " + student.getAcademicYear() + ")" : "Student Resident";
        JLabel regLabel = new JLabel(reg);
        regLabel.setFont(UITheme.FONT_BODY);
        regLabel.setForeground(new Color(199, 210, 254));

        bannerText.add(welcomeLabel);
        bannerText.add(Box.createVerticalStrut(4));
        bannerText.add(regLabel);

        banner.add(bannerText, BorderLayout.WEST);

        // Top section with Banner + 3 Stat Cards
        JPanel topContainer = new JPanel(new BorderLayout(0, 16));
        topContainer.setBackground(UITheme.BG_MAIN);
        topContainer.add(banner, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsPanel.setBackground(UITheme.BG_MAIN);

        // Room Card
        JPanel cardRoom = UITheme.createCard();
        cardRoom.setLayout(new BorderLayout(12, 0));
        JPanel roomTextBox = new JPanel();
        roomTextBox.setLayout(new BoxLayout(roomTextBox, BoxLayout.Y_AXIS));
        roomTextBox.setOpaque(false);
        JLabel rTitle = new JLabel("MY ALLOCATED ROOM");
        rTitle.setFont(UITheme.FONT_SMALL_BOLD);
        rTitle.setForeground(UITheme.TEXT_MUTED);
        roomValLabel = new JLabel("Loading...");
        roomValLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        roomValLabel.setForeground(UITheme.PRIMARY);
        roomSubLabel = new JLabel("-");
        roomSubLabel.setFont(UITheme.FONT_SMALL);
        roomSubLabel.setForeground(UITheme.TEXT_MUTED);
        roomTextBox.add(rTitle);
        roomTextBox.add(Box.createVerticalStrut(4));
        roomTextBox.add(roomValLabel);
        roomTextBox.add(Box.createVerticalStrut(2));
        roomTextBox.add(roomSubLabel);
        cardRoom.add(roomTextBox, BorderLayout.CENTER);
        cardRoom.add(UITheme.createIconCircle(VectorIcon.Type.ROOMS, UITheme.PRIMARY, 42, 20), BorderLayout.EAST);
        cardsPanel.add(cardRoom);

        // Fee Card
        JPanel cardFee = UITheme.createCard();
        cardFee.setLayout(new BorderLayout(12, 0));
        JPanel feeTextBox = new JPanel();
        feeTextBox.setLayout(new BoxLayout(feeTextBox, BoxLayout.Y_AXIS));
        feeTextBox.setOpaque(false);
        JLabel fTitle = new JLabel("FEE PAYMENT STATUS");
        fTitle.setFont(UITheme.FONT_SMALL_BOLD);
        fTitle.setForeground(UITheme.TEXT_MUTED);
        feeValLabel = new JLabel("Loading...");
        feeValLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        feeValLabel.setForeground(UITheme.SUCCESS);
        feeSubLabel = new JLabel("-");
        feeSubLabel.setFont(UITheme.FONT_SMALL);
        feeSubLabel.setForeground(UITheme.TEXT_MUTED);
        feeTextBox.add(fTitle);
        feeTextBox.add(Box.createVerticalStrut(4));
        feeTextBox.add(feeValLabel);
        feeTextBox.add(Box.createVerticalStrut(2));
        feeTextBox.add(feeSubLabel);
        cardFee.add(feeTextBox, BorderLayout.CENTER);
        cardFee.add(UITheme.createIconCircle(VectorIcon.Type.FEES, UITheme.SUCCESS, 42, 20), BorderLayout.EAST);
        cardsPanel.add(cardFee);

        // Complaints Card
        JPanel cardComp = UITheme.createCard();
        cardComp.setLayout(new BorderLayout(12, 0));
        JPanel compTextBox = new JPanel();
        compTextBox.setLayout(new BoxLayout(compTextBox, BoxLayout.Y_AXIS));
        compTextBox.setOpaque(false);
        JLabel cTitle = new JLabel("MY COMPLAINTS");
        cTitle.setFont(UITheme.FONT_SMALL_BOLD);
        cTitle.setForeground(UITheme.TEXT_MUTED);
        complaintValLabel = new JLabel("0 Active");
        complaintValLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        complaintValLabel.setForeground(UITheme.WARNING);
        complaintSubLabel = new JLabel("-");
        complaintSubLabel.setFont(UITheme.FONT_SMALL);
        complaintSubLabel.setForeground(UITheme.TEXT_MUTED);
        compTextBox.add(cTitle);
        compTextBox.add(Box.createVerticalStrut(4));
        compTextBox.add(complaintValLabel);
        compTextBox.add(Box.createVerticalStrut(2));
        compTextBox.add(complaintSubLabel);
        cardComp.add(compTextBox, BorderLayout.CENTER);
        cardComp.add(UITheme.createIconCircle(VectorIcon.Type.COMPLAINTS, UITheme.WARNING, 42, 20), BorderLayout.EAST);
        cardsPanel.add(cardComp);

        topContainer.add(cardsPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // Main Center Area: Today's Menu + Notifications
        JPanel mainCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        mainCenter.setBackground(UITheme.BG_MAIN);

        // Left: Today's Food Menu Card
        JPanel menuCard = UITheme.createCard();
        menuCard.setLayout(new BorderLayout(0, 12));

        JLabel menuHeader = new JLabel("Today's Hostel Mess Menu", new VectorIcon(VectorIcon.Type.FOOD_MENU, 18, UITheme.PRIMARY), SwingConstants.LEFT);
        menuHeader.setIconTextGap(8);
        menuHeader.setFont(UITheme.FONT_SUBTITLE);
        menuHeader.setForeground(UITheme.TEXT_MAIN);
        menuCard.add(menuHeader, BorderLayout.NORTH);

        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(UITheme.BG_CARD);
        menuCard.add(menuContainer, BorderLayout.CENTER);

        JButton btnSubmitFeedback = UITheme.createSecondaryButton("Submit Meal Feedback", new VectorIcon(VectorIcon.Type.FOOD_FEEDBACK, 15, UITheme.TEXT_MAIN));
        btnSubmitFeedback.addActionListener(e -> onNavigate.accept("FOOD_FEEDBACK"));
        menuCard.add(btnSubmitFeedback, BorderLayout.SOUTH);

        mainCenter.add(menuCard);

        // Right: Notifications & Quick Actions Card
        JPanel notifCard = UITheme.createCard();
        notifCard.setLayout(new BorderLayout(0, 12));

        JLabel notifHeader = new JLabel("Announcements & Notices", new VectorIcon(VectorIcon.Type.INFO, 18, UITheme.INFO), SwingConstants.LEFT);
        notifHeader.setIconTextGap(8);
        notifHeader.setFont(UITheme.FONT_SUBTITLE);
        notifHeader.setForeground(UITheme.TEXT_MAIN);
        notifCard.add(notifHeader, BorderLayout.NORTH);

        notificationContainer = new JPanel();
        notificationContainer.setLayout(new BoxLayout(notificationContainer, BoxLayout.Y_AXIS));
        notificationContainer.setBackground(UITheme.BG_CARD);
        JScrollPane scrollNotif = new JScrollPane(notificationContainer);
        scrollNotif.setBorder(null);
        notifCard.add(scrollNotif, BorderLayout.CENTER);

        JButton btnNewComplaint = UITheme.createPrimaryButton("Report Hostel Issue", new VectorIcon(VectorIcon.Type.ADD, 15, Color.WHITE));
        btnNewComplaint.addActionListener(e -> onNavigate.accept("COMPLAINTS"));
        notifCard.add(btnNewComplaint, BorderLayout.SOUTH);

        mainCenter.add(notifCard);

        add(mainCenter, BorderLayout.CENTER);
    }

    public void loadData() {
        Student student = SessionManager.getInstance().getCurrentStudent();
        int studentId = student != null ? student.getStudentId() : -1;

        // 1. Room Allotment
        try {
            if (studentId > 0) {
                Allocation alloc = allocationService.getActiveAllocationByStudent(studentId);
                if (alloc != null) {
                    roomValLabel.setText("Room " + alloc.getRoomNumber());
                    roomSubLabel.setText(alloc.getBlockName() + " (" + alloc.getRoomType() + ")");
                } else {
                    roomValLabel.setText("Not Allocated");
                    roomSubLabel.setText("Contact warden for allotment");
                }
            } else {
                roomValLabel.setText("No Profile");
                roomSubLabel.setText("Student profile not linked");
            }
        } catch (Exception e) {
            roomValLabel.setText("Unavailable");
        }

        // 2. Fees Status
        try {
            if (studentId > 0) {
                List<Fee> fees = feeService.getFeesByStudent(studentId);
                if (!fees.isEmpty()) {
                    Fee latest = fees.get(0);
                    feeValLabel.setText(UITheme.formatCurrency(latest.getPendingAmount()) + " Due");
                    feeSubLabel.setText(latest.getSemester() + " - " + latest.getStatus());
                } else {
                    feeValLabel.setText("No Invoices");
                    feeSubLabel.setText("All dues settled");
                }
            }
        } catch (Exception e) {
            feeValLabel.setText("Error");
        }

        // 3. Complaints
        try {
            if (studentId > 0) {
                List<Complaint> complaints = complaintService.getComplaintsByStudent(studentId);
                long pendingCount = complaints.stream().filter(c -> !"Resolved".equalsIgnoreCase(c.getStatus())).count();
                complaintValLabel.setText(pendingCount + " Open Issues");
                complaintSubLabel.setText("Total Reported: " + complaints.size());
            }
        } catch (Exception e) {
            complaintValLabel.setText("-");
        }

        // 4. Food Menu
        try {
            menuContainer.removeAll();
            List<FoodMenu> todayMenu = foodService.getTodayMenu();
            if (todayMenu.isEmpty()) {
                JLabel emptyMenu = new JLabel("No mess menu published for today.");
                emptyMenu.setFont(UITheme.FONT_BODY);
                emptyMenu.setForeground(UITheme.TEXT_MUTED);
                menuContainer.add(emptyMenu);
            } else {
                for (FoodMenu m : todayMenu) {
                    JPanel mealRow = new JPanel(new BorderLayout(8, 4));
                    mealRow.setBackground(UITheme.BG_SUBTLE);
                    mealRow.setBorder(new EmptyBorder(8, 12, 8, 12));

                    JLabel typeLbl = new JLabel(m.getMealType().toUpperCase());
                    typeLbl.setFont(UITheme.FONT_SMALL_BOLD);
                    typeLbl.setForeground(UITheme.PRIMARY);

                    JLabel itemsLbl = new JLabel("<html><b>" + m.getMenuItems() + "</b></html>");
                    itemsLbl.setFont(UITheme.FONT_BODY);
                    itemsLbl.setForeground(UITheme.TEXT_MAIN);

                    mealRow.add(typeLbl, BorderLayout.NORTH);
                    mealRow.add(itemsLbl, BorderLayout.CENTER);

                    menuContainer.add(mealRow);
                    menuContainer.add(Box.createVerticalStrut(8));
                }
            }
            menuContainer.revalidate();
            menuContainer.repaint();
        } catch (Exception e) {
            System.err.println("Error loading menu: " + e.getMessage());
        }

        // 5. Notifications
        try {
            notificationContainer.removeAll();
            User user = SessionManager.getInstance().getCurrentUser();
            List<Notification> notifs = notificationDAO.getNotificationsForUser("STUDENT", user != null ? user.getUserId() : null);
            if (notifs.isEmpty()) {
                JLabel emptyNotif = new JLabel("No active announcements.");
                emptyNotif.setFont(UITheme.FONT_BODY);
                emptyNotif.setForeground(UITheme.TEXT_MUTED);
                notificationContainer.add(emptyNotif);
            } else {
                for (Notification n : notifs) {
                    JPanel notifRow = new JPanel();
                    notifRow.setLayout(new BoxLayout(notifRow, BoxLayout.Y_AXIS));
                    notifRow.setBackground(UITheme.BG_CARD);
                    notifRow.setBorder(new EmptyBorder(6, 4, 10, 4));

                    JLabel titleLbl = new JLabel(n.getTitle(), new VectorIcon(VectorIcon.Type.CHECK, 12, UITheme.PRIMARY), SwingConstants.LEFT);
                    titleLbl.setIconTextGap(6);
                    titleLbl.setFont(UITheme.FONT_BODY_BOLD);
                    titleLbl.setForeground(UITheme.TEXT_MAIN);

                    JLabel msgLbl = new JLabel("<html>" + n.getMessage() + "</html>");
                    msgLbl.setFont(UITheme.FONT_SMALL);
                    msgLbl.setForeground(UITheme.TEXT_MUTED);

                    notifRow.add(titleLbl);
                    notifRow.add(Box.createVerticalStrut(2));
                    notifRow.add(msgLbl);

                    notificationContainer.add(notifRow);
                }
            }
            notificationContainer.revalidate();
            notificationContainer.repaint();
        } catch (Exception e) {
            System.err.println("Error loading notifications: " + e.getMessage());
        }
    }
}
