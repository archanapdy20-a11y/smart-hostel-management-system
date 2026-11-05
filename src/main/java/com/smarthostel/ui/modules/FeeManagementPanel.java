package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.Fee;
import com.smarthostel.model.Payment;
import com.smarthostel.model.Student;
import com.smarthostel.service.FeeService;
import com.smarthostel.service.StudentService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.ui.components.StatCard;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;
import com.smarthostel.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class FeeManagementPanel extends JPanel {

    private final FeeService feeService = new FeeService();
    private final StudentService studentService = new StudentService();

    private JTabbedPane tabbedPane;
    private DefaultTableModel feesTableModel;
    private DefaultTableModel paymentsTableModel;
    private ModernTable feesTable;
    private ModernTable paymentsTable;

    private List<Fee> currentFees;
    private List<Payment> currentPayments;

    private StatCard totalInvoicedCard;
    private StatCard totalCollectedCard;
    private StatCard pendingDuesCard;
    private StatCard totalTransactionsCard;

    public FeeManagementPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadData();
    }

    private void initUI() {
        boolean canCollect = SessionManager.getInstance().isAdmin() || SessionManager.getInstance().isAccountant();
        boolean isStudent = SessionManager.getInstance().isStudent();

        // Top Toolbar Card
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        JLabel titleLabel = new JLabel("Hostel Fees & Payment Receipts");
        titleLabel.setFont(UITheme.FONT_SUBTITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);
        toolbarCard.add(titleLabel, BorderLayout.WEST);

        // Actions: Only Admin & Accountant can create fee invoices or record payments
        if (canCollect) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setBackground(UITheme.BG_CARD);

            JButton btnCreateFee = UITheme.createSecondaryButton("Generate Fee Invoice", new VectorIcon(VectorIcon.Type.ADD, 14, UITheme.TEXT_MAIN));
            btnCreateFee.addActionListener(e -> showCreateFeeDialog());
            actionPanel.add(btnCreateFee);

            JButton btnCollect = UITheme.createPrimaryButton("Record Payment", new VectorIcon(VectorIcon.Type.FEES, 14, Color.WHITE));
            btnCollect.addActionListener(e -> showRecordPaymentDialog());
            actionPanel.add(btnCollect);

            JButton btnReceipt = UITheme.createSecondaryButton("View Receipt", new VectorIcon(VectorIcon.Type.PRINT_PDF, 14, UITheme.TEXT_MAIN));
            btnReceipt.addActionListener(e -> viewSelectedReceipt());
            actionPanel.add(btnReceipt);

            toolbarCard.add(actionPanel, BorderLayout.EAST);
        } else if (isStudent) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setBackground(UITheme.BG_CARD);

            JButton btnReceipt = UITheme.createSecondaryButton("View Receipt", new VectorIcon(VectorIcon.Type.PRINT_PDF, 14, UITheme.TEXT_MAIN));
            btnReceipt.addActionListener(e -> viewSelectedReceipt());
            actionPanel.add(btnReceipt);

            toolbarCard.add(actionPanel, BorderLayout.EAST);
        }

        // KPI Metric Banner
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiPanel.setOpaque(false);
        totalInvoicedCard = new StatCard("TOTAL INVOICED", UITheme.formatCurrency(0.0), VectorIcon.Type.FEES, UITheme.PRIMARY);
        totalCollectedCard = new StatCard("COLLECTED FEES", UITheme.formatCurrency(0.0), VectorIcon.Type.TREND_UP, UITheme.SUCCESS);
        pendingDuesCard = new StatCard("PENDING DUES", UITheme.formatCurrency(0.0), VectorIcon.Type.COMPLAINTS, UITheme.DANGER);
        totalTransactionsCard = new StatCard("PAYMENT RECEIPTS", "0", VectorIcon.Type.REPORTS, UITheme.INFO);
        kpiPanel.add(totalInvoicedCard);
        kpiPanel.add(totalCollectedCard);
        kpiPanel.add(pendingDuesCard);
        kpiPanel.add(totalTransactionsCard);

        JPanel topContainer = new JPanel(new BorderLayout(0, 14));
        topContainer.setOpaque(false);
        topContainer.add(kpiPanel, BorderLayout.NORTH);
        topContainer.add(toolbarCard, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Center: Tabbed Pane for Fees Invoices vs Payment Receipts
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);

        // Tab 1: Fees Invoices
        JPanel feesTab = new JPanel(new BorderLayout(0, 10));
        feesTab.setBackground(UITheme.BG_CARD);
        feesTab.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] feeCols = {"Fee ID", "Student Name", "Register No", "Academic Year", "Semester", "Fee Type", "Total Fee", "Paid", "Pending Dues", "Due Date", "Status"};
        feesTableModel = new DefaultTableModel(feeCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        feesTable = new ModernTable(feesTableModel);
        feesTab.add(new JScrollPane(feesTable), BorderLayout.CENTER);
        tabbedPane.addTab("Invoiced Fees", new VectorIcon(VectorIcon.Type.FEES, 15, UITheme.PRIMARY), feesTab);

        // Tab 2: Payments History
        JPanel paymentsTab = new JPanel(new BorderLayout(0, 10));
        paymentsTab.setBackground(UITheme.BG_CARD);
        paymentsTab.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] payCols = {"Receipt No", "Student Name", "Register No", "Semester", "Amount Paid", "Payment Date", "Method", "Txn ID", "Recorded By", "Remarks"};
        paymentsTableModel = new DefaultTableModel(payCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        paymentsTable = new ModernTable(paymentsTableModel);
        paymentsTab.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
        tabbedPane.addTab("Payment History & Receipts", new VectorIcon(VectorIcon.Type.REPORTS, 15, UITheme.PRIMARY), paymentsTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void loadData() {
        boolean isStudent = SessionManager.getInstance().isStudent();
        Student s = SessionManager.getInstance().getCurrentStudent();

        try {
            if (isStudent && s != null) {
                currentFees = feeService.getFeesByStudent(s.getStudentId());
                currentPayments = feeService.getPaymentsByStudent(s.getStudentId());
            } else {
                currentFees = feeService.getAllFees("ALL");
                currentPayments = feeService.getAllPayments();
            }

            // Populate Fees
            feesTableModel.setRowCount(0);
            for (Fee f : currentFees) {
                feesTableModel.addRow(new Object[]{
                        f.getFeeId(),
                        f.getStudentName(),
                        f.getRegisterNumber(),
                        f.getAcademicYear(),
                        f.getSemester(),
                        f.getFeeType(),
                        UITheme.formatCurrency(f.getTotalAmount()),
                        UITheme.formatCurrency(f.getPaidAmount()),
                        UITheme.formatCurrency(f.getPendingAmount()),
                        DateUtil.format(f.getDueDate()),
                        f.getStatus()
                });
            }

            // Populate Payments
            paymentsTableModel.setRowCount(0);
            for (Payment p : currentPayments) {
                paymentsTableModel.addRow(new Object[]{
                        p.getReceiptNumber(),
                        p.getStudentName(),
                        p.getRegisterNumber(),
                        p.getFeeSemester(),
                        UITheme.formatCurrency(p.getAmountPaid()),
                        DateUtil.format(p.getPaymentDate()),
                        p.getPaymentMethod(),
                        p.getTransactionId() != null ? p.getTransactionId() : "-",
                        p.getRecordedByName() != null ? p.getRecordedByName() : "Accountant",
                        p.getRemarks() != null ? p.getRemarks() : ""
                });
            }

            // Update KPI metric summary cards
            BigDecimal totalInvoiced = BigDecimal.ZERO;
            BigDecimal totalCollected = BigDecimal.ZERO;
            BigDecimal pendingDues = BigDecimal.ZERO;
            for (Fee f : currentFees) {
                if (f.getTotalAmount() != null) totalInvoiced = totalInvoiced.add(f.getTotalAmount());
                if (f.getPaidAmount() != null) totalCollected = totalCollected.add(f.getPaidAmount());
                if (f.getPendingAmount() != null) pendingDues = pendingDues.add(f.getPendingAmount());
            }
            if (totalInvoicedCard != null) totalInvoicedCard.setValue(UITheme.formatCurrency(totalInvoiced));
            if (totalCollectedCard != null) {
                totalCollectedCard.setValue(UITheme.formatCurrency(totalCollected));
                if (totalInvoiced.compareTo(BigDecimal.ZERO) > 0) {
                    int pct = totalCollected.multiply(BigDecimal.valueOf(100)).divide(totalInvoiced, 0, java.math.RoundingMode.HALF_UP).intValue();
                    totalCollectedCard.setProgress(pct);
                }
            }
            if (pendingDuesCard != null) pendingDuesCard.setValue(UITheme.formatCurrency(pendingDues));
            if (totalTransactionsCard != null) totalTransactionsCard.setValue(String.valueOf(currentPayments.size()));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading fee records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCreateFeeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Fee Invoice", true);
        dialog.setSize(480, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JComboBox<Student> studentBox = new JComboBox<>();
        JTextField yearField = UITheme.createTextField();
        yearField.setText("2024-2025");
        JComboBox<String> semBox = new JComboBox<>(new String[]{
                "Semester 1", "Semester 2", "Semester 3", "Semester 4",
                "Semester 5", "Semester 6", "Semester 7", "Semester 8"
        });
        JTextField typeField = UITheme.createTextField();
        typeField.setText("Hostel & Mess Fee");
        JTextField amountField = UITheme.createTextField();
        amountField.setText("35000.00");
        JTextField dueField = UITheme.createTextField();
        dueField.setText(DateUtil.toSqlString(new Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000)));

        try {
            List<Student> students = studentService.getAllStudents();
            for (Student s : students) studentBox.addItem(s);
        } catch (Exception ignored) {}

        form.add(new JLabel("Select Student: *"));
        form.add(studentBox);
        form.add(new JLabel("Academic Year: *"));
        form.add(yearField);
        form.add(new JLabel("Semester: *"));
        form.add(semBox);
        form.add(new JLabel("Fee Type: *"));
        form.add(typeField);
        form.add(new JLabel("Total Fee Amount (" + UITheme.CURRENCY_SYMBOL + "): *"));
        form.add(amountField);
        form.add(new JLabel("Due Date (YYYY-MM-DD): *"));
        form.add(dueField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Create Invoice");
        saveBtn.addActionListener(e -> {
            Student s = (Student) studentBox.getSelectedItem();
            Date dueDate = DateUtil.parseSqlDate(dueField.getText());
            if (s == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a student.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!ValidationUtil.isPositiveDecimal(amountField.getText())) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid positive fee amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dueDate == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid Due Date. Format: YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal total = new BigDecimal(amountField.getText().trim());
                Fee fee = new Fee(0, s.getStudentId(), yearField.getText().trim(),
                        (String) semBox.getSelectedItem(), typeField.getText().trim(),
                        total, BigDecimal.ZERO, total, dueDate, "UNPAID");

                feeService.createFee(fee);
                JOptionPane.showMessageDialog(dialog, "Fee invoice created successfully for " + s.getFullName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showRecordPaymentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Record Fee Payment Receipt", true);
        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JComboBox<Fee> feeBox = new JComboBox<>();
        JLabel pendingLbl = new JLabel("Pending: -");
        pendingLbl.setFont(UITheme.FONT_BODY_BOLD);
        pendingLbl.setForeground(UITheme.DANGER);

        JTextField amountField = UITheme.createTextField();
        JComboBox<String> methodBox = new JComboBox<>(new String[]{"Cash", "UPI", "Bank Transfer"});
        JTextField txnField = UITheme.createTextField();
        txnField.putClientProperty("JTextField.placeholderText", "e.g., UPI/4092840182/HDFC or Cash");
        JTextField dateField = UITheme.createTextField();
        dateField.setText(DateUtil.toSqlString(DateUtil.getToday()));
        JTextField remarksField = UITheme.createTextField();

        // Populate unpaid/partially paid fees
        try {
            List<Fee> unpaid = feeService.getAllFees("UNPAID");
            List<Fee> partial = feeService.getAllFees("PARTIALLY_PAID");
            for (Fee f : unpaid) feeBox.addItem(f);
            for (Fee f : partial) feeBox.addItem(f);
        } catch (Exception ignored) {}

        feeBox.addActionListener(e -> {
            Fee f = (Fee) feeBox.getSelectedItem();
            if (f != null) {
                pendingLbl.setText("Pending: " + UITheme.formatCurrency(f.getPendingAmount()));
                amountField.setText(f.getPendingAmount().toPlainString());
            }
        });

        if (feeBox.getItemCount() > 0) feeBox.setSelectedIndex(0);

        form.add(new JLabel("Select Pending Fee: *"));
        form.add(feeBox);
        form.add(new JLabel("Outstanding Dues:"));
        form.add(pendingLbl);
        form.add(new JLabel("Payment Amount (" + UITheme.CURRENCY_SYMBOL + "): *"));
        form.add(amountField);
        form.add(new JLabel("Payment Method: *"));
        form.add(methodBox);
        form.add(new JLabel("Transaction ID / Ref:"));
        form.add(txnField);
        form.add(new JLabel("Payment Date: *"));
        form.add(dateField);
        form.add(new JLabel("Remarks:"));
        form.add(remarksField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton confirmBtn = UITheme.createPrimaryButton("Verify & Record Payment");
        confirmBtn.addActionListener(e -> {
            Fee f = (Fee) feeBox.getSelectedItem();
            Date payDate = DateUtil.parseSqlDate(dateField.getText());

            if (f == null) {
                JOptionPane.showMessageDialog(dialog, "No outstanding fee invoice selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!ValidationUtil.isPositiveDecimal(amountField.getText())) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid positive payment amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (payDate == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid payment date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal amount = new BigDecimal(amountField.getText().trim());

            try {
                Payment payment = new Payment(0, f.getFeeId(), f.getStudentId(), amount, payDate,
                        (String) methodBox.getSelectedItem(), txnField.getText().trim(),
                        null, null, remarksField.getText().trim());

                feeService.recordPayment(payment);

                JOptionPane.showMessageDialog(dialog,
                        "Payment of " + UITheme.formatCurrency(amount) + " successfully verified!\nReceipt Number: " + payment.getReceiptNumber(),
                        "Payment Receipt Generated", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadData();
                tabbedPane.setSelectedIndex(1);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Payment Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewSelectedReceipt() {
        int row = paymentsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment from the Payment History tab to view its receipt.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String receiptNo = (String) paymentsTableModel.getValueAt(row, 0);
        String studentName = (String) paymentsTableModel.getValueAt(row, 1);
        String regNo = (String) paymentsTableModel.getValueAt(row, 2);
        String semester = (String) paymentsTableModel.getValueAt(row, 3);
        String amount = (String) paymentsTableModel.getValueAt(row, 4);
        String date = (String) paymentsTableModel.getValueAt(row, 5);
        String method = (String) paymentsTableModel.getValueAt(row, 6);
        String txnId = (String) paymentsTableModel.getValueAt(row, 7);
        String recordedBy = (String) paymentsTableModel.getValueAt(row, 8);

        JDialog receiptDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Official Payment Receipt - " + receiptNo, true);
        receiptDialog.setSize(480, 540);
        receiptDialog.setLocationRelativeTo(this);
        receiptDialog.setLayout(new BorderLayout());

        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setBorder(new EmptyBorder(24, 30, 24, 30));

        JLabel header = new JLabel("SMART HOSTEL MANAGEMENT SYSTEM");
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(UITheme.PRIMARY_DARK);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("OFFICIAL HOSTEL FEE PAYMENT RECEIPT");
        sub.setFont(UITheme.FONT_SMALL_BOLD);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        receiptPanel.add(header);
        receiptPanel.add(Box.createVerticalStrut(4));
        receiptPanel.add(sub);
        receiptPanel.add(Box.createVerticalStrut(16));
        receiptPanel.add(new JSeparator());
        receiptPanel.add(Box.createVerticalStrut(16));

        addReceiptLine(receiptPanel, "Receipt Number:", receiptNo);
        addReceiptLine(receiptPanel, "Payment Date:", date);
        addReceiptLine(receiptPanel, "Student Name:", studentName);
        addReceiptLine(receiptPanel, "Register Number:", regNo);
        addReceiptLine(receiptPanel, "Fee Semester:", semester);
        addReceiptLine(receiptPanel, "Payment Method:", method);
        addReceiptLine(receiptPanel, "Transaction ID:", txnId);
        addReceiptLine(receiptPanel, "Verified & Recorded By:", recordedBy);

        receiptPanel.add(Box.createVerticalStrut(16));
        receiptPanel.add(new JSeparator());
        receiptPanel.add(Box.createVerticalStrut(16));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(Color.WHITE);
        JLabel totLbl = new JLabel("AMOUNT RECEIVED:");
        totLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel valLbl = new JLabel(amount);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valLbl.setForeground(UITheme.SUCCESS);
        totalRow.add(totLbl, BorderLayout.WEST);
        totalRow.add(valLbl, BorderLayout.EAST);
        receiptPanel.add(totalRow);

        receiptPanel.add(Box.createVerticalStrut(20));
        JLabel signLbl = new JLabel("Authorized Electronic Stamp - No Signature Required");
        signLbl.setFont(UITheme.FONT_SMALL);
        signLbl.setForeground(UITheme.TEXT_MUTED);
        signLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(signLbl);

        receiptDialog.add(receiptPanel, BorderLayout.CENTER);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        JButton closeBtn = UITheme.createPrimaryButton("Close Receipt");
        closeBtn.addActionListener(e -> receiptDialog.dispose());
        closePanel.add(closeBtn);
        receiptDialog.add(closePanel, BorderLayout.SOUTH);

        receiptDialog.setVisible(true);
    }

    private void addReceiptLine(JPanel p, String title, String val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_BODY);
        t.setForeground(UITheme.TEXT_MUTED);
        JLabel v = new JLabel(val);
        v.setFont(UITheme.FONT_BODY_BOLD);
        v.setForeground(UITheme.TEXT_MAIN);
        row.add(t, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        p.add(row);
        p.add(Box.createVerticalStrut(8));
    }
}
