package com.smarthostel.ui.modules;

import com.smarthostel.model.*;
import com.smarthostel.service.ReportService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.sql.Date;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final ReportService reportService = new ReportService();

    private JComboBox<String> reportCategoryBox;
    private DefaultTableModel tableModel;
    private ModernTable table;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel reportSummaryLabel;

    private JTextField searchField;
    private JTextField fromDateField;
    private JTextField toDateField;

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        generateReport();
    }

    private void initUI() {
        // Container for Top Toolbars
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.BG_MAIN);

        // 1. Report Selector & Export Toolbar
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setBackground(UITheme.BG_CARD);

        JLabel catLbl = new JLabel("Select Report Type:");
        catLbl.setFont(UITheme.FONT_BODY_BOLD);
        leftPanel.add(catLbl);

        reportCategoryBox = new JComboBox<>(new String[]{
                "Student Details Directory",
                "Room Availability & Capacity",
                "Room Allocations Record",
                "Hostel Fee Collection Overview",
                "Pending Fees & Defaulters",
                "Payment Transaction History",
                "Complaints & Resolution Log",
                "Food Quality & Feedback Summary"
        });
        reportCategoryBox.setPreferredSize(new Dimension(280, 36));
        reportCategoryBox.addActionListener(e -> generateReport());
        leftPanel.add(reportCategoryBox);

        JButton btnGenerate = UITheme.createSecondaryButton("🔄 Refresh");
        btnGenerate.addActionListener(e -> generateReport());
        leftPanel.add(btnGenerate);

        toolbarCard.add(leftPanel, BorderLayout.WEST);

        // Right Panel: Export buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(UITheme.BG_CARD);

        JButton btnExportCSV = UITheme.createPrimaryButton("📥 Export to CSV");
        btnExportCSV.addActionListener(e -> exportCSV());
        rightPanel.add(btnExportCSV);

        JButton btnPrint = UITheme.createSecondaryButton("📄 Export to PDF / Print");
        btnPrint.addActionListener(e -> printReport());
        rightPanel.add(btnPrint);

        toolbarCard.add(rightPanel, BorderLayout.EAST);
        topContainer.add(toolbarCard);
        topContainer.add(Box.createVerticalStrut(10));

        // 2. Filter & Real-Time Search Bar
        JPanel filterCard = UITheme.createCard();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 0));

        JLabel searchLbl = new JLabel("🔍 Quick Filter / Search:");
        searchLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterCard.add(searchLbl);

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Search within report...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearchFilter(); }
            public void removeUpdate(DocumentEvent e) { applySearchFilter(); }
            public void changedUpdate(DocumentEvent e) { applySearchFilter(); }
        });
        filterCard.add(searchField);

        JLabel fromLbl = new JLabel("From Date:");
        fromLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterCard.add(fromLbl);

        fromDateField = UITheme.createTextField();
        fromDateField.setPreferredSize(new Dimension(110, 36));
        fromDateField.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        filterCard.add(fromDateField);

        JLabel toLbl = new JLabel("To Date:");
        toLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterCard.add(toLbl);

        toDateField = UITheme.createTextField();
        toDateField.setPreferredSize(new Dimension(110, 36));
        toDateField.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        filterCard.add(toDateField);

        JButton btnFilterDate = UITheme.createSecondaryButton("Apply Date Filter");
        btnFilterDate.addActionListener(e -> generateReport());
        filterCard.add(btnFilterDate);

        JButton btnClearFilter = UITheme.createSecondaryButton("Clear Filters");
        btnClearFilter.addActionListener(e -> {
            searchField.setText("");
            fromDateField.setText("");
            toDateField.setText("");
            generateReport();
        });
        filterCard.add(btnClearFilter);

        topContainer.add(filterCard);
        add(topContainer, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        reportSummaryLabel = new JLabel("Report Preview");
        reportSummaryLabel.setFont(UITheme.FONT_SUBTITLE);
        reportSummaryLabel.setForeground(UITheme.TEXT_MAIN);
        tableCard.add(reportSummaryLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        rowSorter = new TableRowSorter<>(tableModel);

        table = new ModernTable(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void applySearchFilter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void generateReport() {
        int idx = reportCategoryBox.getSelectedIndex();
        try {
            switch (idx) {
                case 0 -> generateStudentReport();
                case 1 -> generateRoomReport();
                case 2 -> generateAllocationReport();
                case 3 -> generateFeeCollectionReport();
                case 4 -> generatePendingFeesReport();
                case 5 -> generatePaymentHistoryReport();
                case 6 -> generateComplaintsReport();
                case 7 -> generateFeedbackReport();
            }
            applySearchFilter();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isWithinDateRange(java.util.Date targetDate) {
        if (targetDate == null) return true;
        String fromStr = fromDateField.getText().trim();
        String toStr = toDateField.getText().trim();
        if (fromStr.isEmpty() && toStr.isEmpty()) return true;

        try {
            if (!fromStr.isEmpty()) {
                Date from = DateUtil.parseSqlDate(fromStr);
                if (from != null && targetDate.before(from)) return false;
            }
            if (!toStr.isEmpty()) {
                Date to = DateUtil.parseSqlDate(toStr);
                if (to != null && targetDate.after(to)) return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    private void generateStudentReport() throws Exception {
        List<Student> list = reportService.getStudentReport();
        String[] cols = {"Student ID", "Register No", "Full Name", "Gender", "Department", "Academic Year", "Phone", "Email", "Admission Date", "Allocated Room"};
        tableModel.setDataVector(new Object[0][0], cols);
        int count = 0;
        for (Student s : list) {
            if (isWithinDateRange(s.getAdmissionDate())) {
                count++;
                tableModel.addRow(new Object[]{
                        s.getStudentId(), s.getRegisterNumber(), s.getFullName(),
                        s.getGender(), s.getDepartment(), "Year " + s.getAcademicYear(),
                        s.getPhoneNumber(), s.getEmail(), DateUtil.format(s.getAdmissionDate()), s.getAllocatedRoom()
                });
            }
        }
        reportSummaryLabel.setText("Student Details Directory (" + count + " Total Records)");
    }

    private void generateRoomReport() throws Exception {
        List<Room> list = reportService.getRoomAvailabilityReport();
        String[] cols = {"Room No", "Block", "Floor", "Type", "Total Beds", "Occupied", "Available Beds", "Monthly Rent", "Status"};
        tableModel.setDataVector(new Object[0][0], cols);
        int totalAvailable = 0;
        for (Room r : list) {
            totalAvailable += r.getAvailableBeds();
            tableModel.addRow(new Object[]{
                    r.getRoomNumber(), r.getBlockName(), "Floor " + r.getFloorNumber(),
                    r.getRoomType(), r.getTotalCapacity(), r.getOccupiedCapacity(),
                    r.getAvailableBeds(), "₹" + r.getMonthlyRent().toPlainString(), r.getStatus()
            });
        }
        reportSummaryLabel.setText("Available Rooms (" + list.size() + " Rooms | " + totalAvailable + " Available Beds)");
    }

    private void generateAllocationReport() throws Exception {
        List<Allocation> list = reportService.getAllocationReport();
        String[] cols = {"Alloc ID", "Student Name", "Register No", "Room No", "Block", "Allocation Date", "Vacate Date", "Status", "Allocated By"};
        tableModel.setDataVector(new Object[0][0], cols);
        int count = 0;
        for (Allocation a : list) {
            if (isWithinDateRange(a.getAllocationDate())) {
                count++;
                tableModel.addRow(new Object[]{
                        a.getAllocationId(), a.getStudentName(), a.getRegisterNumber(),
                        a.getRoomNumber(), a.getBlockName(), DateUtil.format(a.getAllocationDate()),
                        a.getVacateDate() != null ? DateUtil.format(a.getVacateDate()) : "-",
                        a.getStatus(), a.getAllocatedByName() != null ? a.getAllocatedByName() : "System"
                });
            }
        }
        reportSummaryLabel.setText("Room Allocations Audit Report (" + count + " Records)");
    }

    private void generateFeeCollectionReport() throws Exception {
        List<Fee> list = reportService.getFeeCollectionReport();
        String[] cols = {"Fee ID", "Student Name", "Register No", "Semester", "Fee Type", "Total Fee", "Paid", "Pending Balance", "Due Date", "Status"};
        tableModel.setDataVector(new Object[0][0], cols);
        int count = 0;
        for (Fee f : list) {
            if (isWithinDateRange(f.getDueDate())) {
                count++;
                tableModel.addRow(new Object[]{
                        f.getFeeId(), f.getStudentName(), f.getRegisterNumber(),
                        f.getSemester(), f.getFeeType(), "₹" + f.getTotalAmount().toPlainString(),
                        "₹" + f.getPaidAmount().toPlainString(), "₹" + f.getPendingAmount().toPlainString(),
                        DateUtil.format(f.getDueDate()), f.getStatus()
                });
            }
        }
        reportSummaryLabel.setText("Hostel Fee Invoices & Collection Overview (" + count + " Records)");
    }

    private void generatePendingFeesReport() throws Exception {
        List<Fee> list = reportService.getPendingFeesReport();
        String[] cols = {"Fee ID", "Student Name", "Register No", "Semester", "Total Fee", "Paid", "Pending Balance", "Due Date", "Status"};
        tableModel.setDataVector(new Object[0][0], cols);
        int count = 0;
        for (Fee f : list) {
            if (isWithinDateRange(f.getDueDate())) {
                count++;
                tableModel.addRow(new Object[]{
                        f.getFeeId(), f.getStudentName(), f.getRegisterNumber(),
                        f.getSemester(), "₹" + f.getTotalAmount().toPlainString(),
                        "₹" + f.getPaidAmount().toPlainString(), "₹" + f.getPendingAmount().toPlainString(),
                        DateUtil.format(f.getDueDate()), f.getStatus()
                });
            }
        }
        reportSummaryLabel.setText("Outstanding Fee Dues Report (" + count + " Pending Invoices)");
    }

    private void generatePaymentHistoryReport() throws Exception {
        List<Payment> list = reportService.getPaymentHistoryReport();
        String[] cols = {"Receipt No", "Student Name", "Register No", "Semester", "Amount Received", "Payment Date", "Method", "Txn ID", "Recorded By"};
        tableModel.setDataVector(new Object[0][0], cols);
        int count = 0;
        for (Payment p : list) {
            if (isWithinDateRange(p.getPaymentDate())) {
                count++;
                tableModel.addRow(new Object[]{
                        p.getReceiptNumber(), p.getStudentName(), p.getRegisterNumber(),
                        p.getFeeSemester(), "₹" + p.getAmountPaid().toPlainString(),
                        DateUtil.format(p.getPaymentDate()), p.getPaymentMethod(),
                        p.getTransactionId() != null ? p.getTransactionId() : "-",
                        p.getRecordedByName() != null ? p.getRecordedByName() : "Staff"
                });
            }
        }
        reportSummaryLabel.setText("Verified Payment Receipts Report (" + count + " Transactions)");
    }

    private void generateComplaintsReport() throws Exception {
        List<Complaint> list = reportService.getComplaintsReport();
        String[] cols = {"ID", "Student", "Reg No", "Room", "Category", "Priority", "Status", "Reported At", "Resolved By"};
        tableModel.setDataVector(new Object[0][0], cols);
        int count = 0;
        for (Complaint c : list) {
            if (isWithinDateRange(c.getSubmittedAt())) {
                count++;
                tableModel.addRow(new Object[]{
                        c.getComplaintId(), c.getStudentName(), c.getRegisterNumber(),
                        c.getRoomNumber(), c.getCategory(), c.getPriority(),
                        c.getStatus(), DateUtil.formatDateTime(c.getSubmittedAt()),
                        c.getResolvedByName() != null ? c.getResolvedByName() : "-"
                });
            }
        }
        reportSummaryLabel.setText("Complaints & Incidents Log (" + count + " Tickets)");
    }

    private void generateFeedbackReport() throws Exception {
        List<FoodFeedback> list = reportService.getFoodFeedbackReport();
        String[] cols = {"ID", "Student", "Reg No", "Date", "Meal", "Rating", "Comments", "Submitted At"};
        tableModel.setDataVector(new Object[0][0], cols);
        int count = 0;
        for (FoodFeedback fb : list) {
            if (isWithinDateRange(fb.getMenuDate())) {
                count++;
                tableModel.addRow(new Object[]{
                        fb.getFeedbackId(), fb.getStudentName(), fb.getRegisterNumber(),
                        DateUtil.format(fb.getMenuDate()), fb.getMealType(),
                        fb.getRatingStars() + " (" + fb.getRating() + "/5)",
                        fb.getComments() != null ? fb.getComments() : "-",
                        DateUtil.formatDateTime(fb.getSubmittedAt())
                });
            }
        }
        reportSummaryLabel.setText("Food Quality Feedback History (" + count + " Submissions)");
    }

    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("hostel_report_" + System.currentTimeMillis() + ".csv"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            boolean success = reportService.exportTableToCSV(tableModel, file);
            if (success) {
                JOptionPane.showMessageDialog(this, "Report successfully exported to:\n" + file.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export report.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printReport() {
        try {
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat((String) reportCategoryBox.getSelectedItem()),
                    new java.text.MessageFormat("Smart Hostel Management System - Page {0}"));
            if (complete) {
                JOptionPane.showMessageDialog(this, "Print / PDF job submitted successfully.", "Print / PDF", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Printing error: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
