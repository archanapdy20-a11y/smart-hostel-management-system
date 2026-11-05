package com.smarthostel.ui.dashboards;

import com.smarthostel.dao.FeeDAO;
import com.smarthostel.model.Payment;
import com.smarthostel.service.FeeService;
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

public class AccountantDashboardPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final FeeService feeService = new FeeService();

    private StatCard cardTotalFees;
    private StatCard cardCollected;
    private StatCard cardPending;
    private StatCard cardRecentCount;

    private DefaultTableModel paymentsTableModel;

    public AccountantDashboardPanel(Consumer<String> onNavigate) {
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

        // 1. Finance Overview Banner
        JPanel bannerCard = UITheme.createCard();
        bannerCard.setLayout(new BorderLayout(16, 0));
        bannerCard.setBackground(new Color(15, 23, 42));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel bannerTitle = new JLabel("Hostel Accounts & Financial Revenue Audit");
        bannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bannerTitle.setForeground(Color.WHITE);

        JLabel bannerSub = new JLabel("Real-time fee collection reconciliation, pending dues tracking, and payment verification.");
        bannerSub.setFont(UITheme.FONT_BODY);
        bannerSub.setForeground(new Color(203, 213, 225));

        bannerText.add(bannerTitle);
        bannerText.add(Box.createVerticalStrut(3));
        bannerText.add(bannerSub);
        bannerCard.add(bannerText, BorderLayout.CENTER);

        topSection.add(bannerCard);
        topSection.add(Box.createVerticalStrut(14));

        // 2. 4 Finance Stat Cards with Progress Bars
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(UITheme.BG_MAIN);

        cardTotalFees = new StatCard("Total Invoiced", UITheme.formatCurrency(null), "All Academic Fees", UITheme.PRIMARY, VectorIcon.Type.FEES);
        cardCollected = new StatCard("Collected Revenue", UITheme.formatCurrency(null), "Verified Receipts", UITheme.SUCCESS, VectorIcon.Type.CHECK, 0);
        cardPending = new StatCard("Pending Dues", UITheme.formatCurrency(null), "Unpaid Outstanding", UITheme.DANGER, VectorIcon.Type.COMPLAINTS);
        cardRecentCount = new StatCard("Recent Transactions", "0", "Verified Receipts", new Color(14, 165, 233), VectorIcon.Type.REPORTS);

        statsPanel.add(cardTotalFees);
        statsPanel.add(cardCollected);
        statsPanel.add(cardPending);
        statsPanel.add(cardRecentCount);

        topSection.add(statsPanel);
        add(topSection, BorderLayout.NORTH);

        // Center: Quick Actions & Recent Transactions
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UITheme.BG_MAIN);

        JPanel actionsCard = UITheme.createCard();
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));

        JLabel actionLabel = new JLabel("Finance Operations: ");
        actionLabel.setFont(UITheme.FONT_BODY_BOLD);
        actionLabel.setForeground(UITheme.TEXT_MAIN);
        actionsCard.add(actionLabel);

        JButton btnRecord = UITheme.createPrimaryButton("Record Fee Payment", new VectorIcon(VectorIcon.Type.FEES, 15, Color.WHITE));
        btnRecord.addActionListener(e -> onNavigate.accept("FEES"));
        actionsCard.add(btnRecord);

        JButton btnStudents = UITheme.createSecondaryButton("Resident Dues List", new VectorIcon(VectorIcon.Type.STUDENTS, 15, UITheme.TEXT_MAIN));
        btnStudents.addActionListener(e -> onNavigate.accept("STUDENTS"));
        actionsCard.add(btnStudents);

        JButton btnReports = UITheme.createSecondaryButton("Export Fee Reports", new VectorIcon(VectorIcon.Type.EXPORT_CSV, 15, UITheme.TEXT_MAIN));
        btnReports.addActionListener(e -> onNavigate.accept("REPORTS"));
        actionsCard.add(btnReports);

        centerPanel.add(actionsCard, BorderLayout.NORTH);

        // Recent Payments Table
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tableTitle = new JLabel("Recent Verified Fee Payments & Receipts", new VectorIcon(VectorIcon.Type.PRINT_PDF, 18, UITheme.SUCCESS), SwingConstants.LEFT);
        tableTitle.setIconTextGap(8);
        tableTitle.setFont(UITheme.FONT_SUBTITLE);
        tableTitle.setForeground(UITheme.TEXT_MAIN);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Receipt No", "Student", "Reg No", "Semester", "Amount Paid", "Method", "Txn ID", "Date", "Recorded By"};
        paymentsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        ModernTable table = new ModernTable(paymentsTableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        try {
            FeeDAO.FeeSummary summary = feeService.getFeeSummary();
            cardTotalFees.setValue(UITheme.formatCurrency(summary.totalFees));
            cardCollected.setValue(UITheme.formatCurrency(summary.collectedFees));
            cardPending.setValue(UITheme.formatCurrency(summary.pendingFees));

            double total = summary.totalFees.doubleValue();
            int collectedPercent = total > 0 ? (int) ((summary.collectedFees.doubleValue() * 100) / total) : 0;
            cardCollected.setProgress(collectedPercent);

            List<Payment> recent = feeService.getRecentPayments(20);
            cardRecentCount.setValue(String.valueOf(recent.size()));

            paymentsTableModel.setRowCount(0);
            for (Payment p : recent) {
                paymentsTableModel.addRow(new Object[]{
                        p.getReceiptNumber(),
                        p.getStudentName(),
                        p.getRegisterNumber(),
                        p.getFeeSemester(),
                        UITheme.formatCurrency(p.getAmountPaid()),
                        p.getPaymentMethod(),
                        p.getTransactionId() != null ? p.getTransactionId() : "-",
                        DateUtil.format(p.getPaymentDate()),
                        p.getRecordedByName() != null ? p.getRecordedByName() : "Accountant"
                });
            }

        } catch (Exception e) {
            System.err.println("Error loading accountant dashboard: " + e.getMessage());
        }
    }
}
