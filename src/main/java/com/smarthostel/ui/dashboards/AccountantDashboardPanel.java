package com.smarthostel.ui.dashboards;

import com.smarthostel.dao.FeeDAO;
import com.smarthostel.model.Payment;
import com.smarthostel.service.FeeService;
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
        setLayout(new BorderLayout(0, 20));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadData();
    }

    private void initUI() {
        // Top 4 Finance Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(UITheme.BG_MAIN);

        cardTotalFees = new StatCard("Total Invoiced", "₹0", "All Academic Fees", UITheme.PRIMARY);
        cardCollected = new StatCard("Collected Revenue", "₹0", "Verified Receipts", UITheme.SUCCESS);
        cardPending = new StatCard("Pending Dues", "₹0", "Unpaid / Outstanding", UITheme.DANGER);
        cardRecentCount = new StatCard("Recent Transactions", "0", "Completed Transactions", UITheme.INFO);

        statsPanel.add(cardTotalFees);
        statsPanel.add(cardCollected);
        statsPanel.add(cardPending);
        statsPanel.add(cardRecentCount);

        add(statsPanel, BorderLayout.NORTH);

        // Center: Quick Actions & Recent Transactions
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UITheme.BG_MAIN);

        JPanel actionsCard = UITheme.createCard();
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));

        JLabel actionLabel = new JLabel("Finance Operations: ");
        actionLabel.setFont(UITheme.FONT_BODY_BOLD);
        actionLabel.setForeground(UITheme.TEXT_MAIN);
        actionsCard.add(actionLabel);

        JButton btnRecord = UITheme.createPrimaryButton("+ Record Fee Payment");
        btnRecord.addActionListener(e -> onNavigate.accept("FEES"));
        actionsCard.add(btnRecord);

        JButton btnStudents = UITheme.createSecondaryButton("👥 Resident Dues List");
        btnStudents.addActionListener(e -> onNavigate.accept("STUDENTS"));
        actionsCard.add(btnStudents);

        JButton btnReports = UITheme.createSecondaryButton("📈 Export Fee Reports");
        btnReports.addActionListener(e -> onNavigate.accept("REPORTS"));
        actionsCard.add(btnReports);

        centerPanel.add(actionsCard, BorderLayout.NORTH);

        // Recent Payments Table
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        JLabel tableTitle = new JLabel("Recent Verified Fee Payments & Receipts");
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
            cardTotalFees.setValue("₹" + summary.totalFees.toPlainString());
            cardCollected.setValue("₹" + summary.collectedFees.toPlainString());
            cardPending.setValue("₹" + summary.pendingFees.toPlainString());

            List<Payment> recent = feeService.getRecentPayments(20);
            cardRecentCount.setValue(String.valueOf(recent.size()));

            paymentsTableModel.setRowCount(0);
            for (Payment p : recent) {
                paymentsTableModel.addRow(new Object[]{
                        p.getReceiptNumber(),
                        p.getStudentName(),
                        p.getRegisterNumber(),
                        p.getFeeSemester(),
                        "₹" + p.getAmountPaid().toPlainString(),
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
