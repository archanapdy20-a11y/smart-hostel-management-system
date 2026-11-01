package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.Complaint;
import com.smarthostel.model.Student;
import com.smarthostel.service.ComplaintService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ComplaintManagementPanel extends JPanel {

    private final ComplaintService complaintService = new ComplaintService();
    private DefaultTableModel tableModel;
    private ModernTable table;
    private JComboBox<String> statusFilterBox;
    private JTextField searchField;
    private List<Complaint> currentComplaints;

    public ComplaintManagementPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadComplaints();
    }

    private void initUI() {
        boolean isStudent = SessionManager.getInstance().isStudent();
        boolean canResolve = SessionManager.getInstance().isAdmin() || SessionManager.getInstance().isWarden();

        // Toolbar Card
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        // Left: Filter + Search
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(UITheme.BG_CARD);

        JLabel filterLbl = new JLabel("Status:");
        filterLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterPanel.add(filterLbl);

        statusFilterBox = new JComboBox<>(new String[]{"ALL", "Pending", "In Progress", "Resolved"});
        statusFilterBox.addActionListener(e -> loadComplaints());
        filterPanel.add(statusFilterBox);

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Category, description...");
        searchField.addActionListener(e -> searchComplaints());
        filterPanel.add(searchField);

        JButton btnSearch = UITheme.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> searchComplaints());
        filterPanel.add(btnSearch);

        JButton btnReset = UITheme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            searchField.setText("");
            statusFilterBox.setSelectedIndex(0);
            loadComplaints();
        });
        filterPanel.add(btnReset);

        toolbarCard.add(filterPanel, BorderLayout.WEST);

        // Right Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(UITheme.BG_CARD);

        if (isStudent) {
            JButton btnSubmit = UITheme.createPrimaryButton("+ Report New Issue");
            btnSubmit.addActionListener(e -> showSubmitComplaintDialog());
            actionPanel.add(btnSubmit);
        }

        if (canResolve) {
            JButton btnResolve = UITheme.createPrimaryButton("🛠️ Update / Resolve Issue");
            btnResolve.addActionListener(e -> showResolveDialog());
            actionPanel.add(btnResolve);
        }

        JButton btnViewDetails = UITheme.createSecondaryButton("🔍 View Details");
        btnViewDetails.addActionListener(e -> showDetailsDialog());
        actionPanel.add(btnViewDetails);

        toolbarCard.add(actionPanel, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        String[] cols = {
                "ID", "Student Name", "Reg No", "Room No", "Category",
                "Priority", "Status", "Reported On", "Resolved By"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new ModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void loadComplaints() {
        String filter = (String) statusFilterBox.getSelectedItem();
        boolean isStudent = SessionManager.getInstance().isStudent();
        Student s = SessionManager.getInstance().getCurrentStudent();

        try {
            if (isStudent && s != null) {
                currentComplaints = complaintService.getComplaintsByStudent(s.getStudentId());
                if (!"ALL".equalsIgnoreCase(filter)) {
                    currentComplaints = currentComplaints.stream()
                            .filter(c -> c.getStatus().equalsIgnoreCase(filter))
                            .toList();
                }
            } else {
                currentComplaints = complaintService.getAllComplaints(filter);
            }

            populateTable(currentComplaints);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading complaints: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchComplaints() {
        String q = searchField.getText().trim();
        try {
            currentComplaints = complaintService.searchComplaints(q);
            if (SessionManager.getInstance().isStudent()) {
                Student s = SessionManager.getInstance().getCurrentStudent();
                if (s != null) {
                    currentComplaints = currentComplaints.stream()
                            .filter(c -> c.getStudentId() == s.getStudentId())
                            .toList();
                }
            }
            populateTable(currentComplaints);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Complaint> list) {
        tableModel.setRowCount(0);
        for (Complaint c : list) {
            tableModel.addRow(new Object[]{
                    c.getComplaintId(),
                    c.getStudentName(),
                    c.getRegisterNumber(),
                    c.getRoomNumber(),
                    c.getCategory(),
                    c.getPriority(),
                    c.getStatus(),
                    DateUtil.formatDateTime(c.getSubmittedAt()),
                    c.getResolvedByName() != null ? c.getResolvedByName() : "-"
            });
        }
    }

    private void showSubmitComplaintDialog() {
        Student s = SessionManager.getInstance().getCurrentStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Student profile is not active. Cannot submit complaint.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Report Hostel Issue / Complaint", true);
        dialog.setSize(520, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JComboBox<String> catBox = new JComboBox<>(new String[]{
                "Water Problem", "Electricity Problem", "Room Maintenance",
                "Bathroom Cleaning", "Food Quality", "Other Issues"
        });

        JTextArea descArea = new JTextArea(6, 30);
        descArea.setFont(UITheme.FONT_BODY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        form.add(new JLabel("Select Complaint Category: *"));
        form.add(catBox);
        form.add(new JLabel("Describe the Issue in Detail (Minimum 10 characters): *"));
        form.add(new JScrollPane(descArea));

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton submitBtn = UITheme.createPrimaryButton("Submit Complaint");
        submitBtn.addActionListener(e -> {
            try {
                Complaint comp = new Complaint(0, s.getStudentId(), (String) catBox.getSelectedItem(),
                        descArea.getText().trim(), "Pending", null, null, null, null, null);

                complaintService.submitComplaint(comp);
                JOptionPane.showMessageDialog(dialog,
                        "Complaint registered successfully!\nAssigned Priority: " + comp.getPriority(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showResolveDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a complaint from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(row, 0);
        Complaint complaint = currentComplaints.stream().filter(c -> c.getComplaintId() == complaintId).findFirst().orElse(null);
        if (complaint == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Resolve Complaint #" + complaintId, true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"In Progress", "Resolved", "Pending"});
        statusBox.setSelectedItem(complaint.getStatus());

        JTextArea notesArea = new JTextArea(5, 30);
        notesArea.setFont(UITheme.FONT_BODY);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setText(complaint.getResolutionNotes() != null ? complaint.getResolutionNotes() : "");
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        form.add(new JLabel("Update Status:"));
        form.add(statusBox);
        form.add(new JLabel("Resolution Notes / Technician Action:"));
        form.add(new JScrollPane(notesArea));

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton updateBtn = UITheme.createPrimaryButton("Save Resolution");
        updateBtn.addActionListener(e -> {
            try {
                complaintService.updateStatus(complaintId, (String) statusBox.getSelectedItem(), notesArea.getText().trim());
                JOptionPane.showMessageDialog(dialog, "Complaint updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadComplaints();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(updateBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showDetailsDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(row, 0);
        Complaint c = currentComplaints.stream().filter(comp -> comp.getComplaintId() == complaintId).findFirst().orElse(null);
        if (c == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Complaint Details - #" + c.getComplaintId(), true);
        dialog.setSize(500, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        p.setBackground(Color.WHITE);

        addDetailRow(p, "Student Name:", c.getStudentName() + " (" + c.getRegisterNumber() + ")");
        addDetailRow(p, "Allocated Room:", c.getRoomNumber());
        addDetailRow(p, "Category:", c.getCategory());
        addDetailRow(p, "Priority:", c.getPriority());
        addDetailRow(p, "Current Status:", c.getStatus());
        addDetailRow(p, "Submitted At:", DateUtil.formatDateTime(c.getSubmittedAt()));
        addDetailRow(p, "Resolved At:", c.getResolvedAt() != null ? DateUtil.formatDateTime(c.getResolvedAt()) : "Pending");
        addDetailRow(p, "Resolved By:", c.getResolvedByName() != null ? c.getResolvedByName() : "-");

        p.add(Box.createVerticalStrut(10));
        JLabel descTitle = new JLabel("Complaint Description:");
        descTitle.setFont(UITheme.FONT_BODY_BOLD);
        p.add(descTitle);
        p.add(Box.createVerticalStrut(4));
        JTextArea desc = new JTextArea(c.getDescription());
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBackground(UITheme.BG_SUBTLE);
        desc.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.add(desc);

        if (c.getResolutionNotes() != null && !c.getResolutionNotes().isEmpty()) {
            p.add(Box.createVerticalStrut(10));
            JLabel resTitle = new JLabel("Resolution Action Notes:");
            resTitle.setFont(UITheme.FONT_BODY_BOLD);
            p.add(resTitle);
            p.add(Box.createVerticalStrut(4));
            JTextArea notes = new JTextArea(c.getResolutionNotes());
            notes.setEditable(false);
            notes.setLineWrap(true);
            notes.setWrapStyleWord(true);
            notes.setBackground(UITheme.BG_SUBTLE);
            notes.setBorder(new EmptyBorder(8, 8, 8, 8));
            p.add(notes);
        }

        dialog.add(new JScrollPane(p), BorderLayout.CENTER);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        JButton closeBtn = UITheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        closePanel.add(closeBtn);
        dialog.add(closePanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel p, String title, String val) {
        JPanel r = new JPanel(new BorderLayout());
        r.setBackground(Color.WHITE);
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_BODY);
        t.setForeground(UITheme.TEXT_MUTED);
        JLabel v = new JLabel(val);
        v.setFont(UITheme.FONT_BODY_BOLD);
        v.setForeground(UITheme.TEXT_MAIN);
        r.add(t, BorderLayout.WEST);
        r.add(v, BorderLayout.EAST);
        p.add(r);
        p.add(Box.createVerticalStrut(6));
    }
}
