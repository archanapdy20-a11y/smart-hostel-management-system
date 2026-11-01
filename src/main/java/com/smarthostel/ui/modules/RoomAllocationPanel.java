package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.Allocation;
import com.smarthostel.model.Room;
import com.smarthostel.model.Student;
import com.smarthostel.service.AllocationService;
import com.smarthostel.service.RoomService;
import com.smarthostel.service.StudentService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class RoomAllocationPanel extends JPanel {

    private final AllocationService allocationService = new AllocationService();
    private final StudentService studentService = new StudentService();
    private final RoomService roomService = new RoomService();

    private DefaultTableModel tableModel;
    private ModernTable table;
    private JCheckBox activeOnlyCheck;
    private JTextField searchField;
    private List<Allocation> currentAllocations;

    public RoomAllocationPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadAllocations();
    }

    private void initUI() {
        boolean canManage = SessionManager.getInstance().isAdmin() || SessionManager.getInstance().isWarden();
        boolean isStudent = SessionManager.getInstance().isStudent();

        // Toolbar Card
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterPanel.setBackground(UITheme.BG_CARD);

        activeOnlyCheck = new JCheckBox("Active Allocations Only", true);
        activeOnlyCheck.setFont(UITheme.FONT_BODY_BOLD);
        activeOnlyCheck.addActionListener(e -> loadAllocations());
        filterPanel.add(activeOnlyCheck);

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Student, Reg No, Room...");
        searchField.addActionListener(e -> searchAllocations());
        filterPanel.add(searchField);

        JButton btnSearch = UITheme.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> searchAllocations());
        filterPanel.add(btnSearch);

        JButton btnReset = UITheme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            searchField.setText("");
            activeOnlyCheck.setSelected(true);
            loadAllocations();
        });
        filterPanel.add(btnReset);

        toolbarCard.add(filterPanel, BorderLayout.WEST);

        // Actions: Only Admin and Warden can Allocate or Vacate
        if (canManage) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setBackground(UITheme.BG_CARD);

            JButton btnAllocate = UITheme.createPrimaryButton("+ Allocate New Room");
            btnAllocate.addActionListener(e -> showAllocationDialog());
            actionPanel.add(btnAllocate);

            JButton btnVacate = UITheme.createDangerButton("🚪 Vacate Room");
            btnVacate.addActionListener(e -> vacateSelectedRoom());
            actionPanel.add(btnVacate);

            toolbarCard.add(actionPanel, BorderLayout.EAST);
        }

        add(toolbarCard, BorderLayout.NORTH);

        // Allocations Table
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        String[] cols = {
                "Alloc ID", "Student Name", "Register No", "Room No", "Block", "Type",
                "Allocation Date", "Vacate Date", "Status", "Allocated By", "Remarks"
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

    public void loadAllocations() {
        try {
            boolean activeOnly = activeOnlyCheck.isSelected();
            currentAllocations = allocationService.getAllocations(activeOnly);

            // If current user is student, filter only their allocation
            if (SessionManager.getInstance().isStudent()) {
                Student s = SessionManager.getInstance().getCurrentStudent();
                if (s != null) {
                    currentAllocations = currentAllocations.stream()
                            .filter(a -> a.getStudentId() == s.getStudentId())
                            .toList();
                }
            }

            populateTable(currentAllocations);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading allocations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAllocations() {
        String query = searchField.getText().trim();
        try {
            currentAllocations = allocationService.searchAllocations(query);
            populateTable(currentAllocations);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Allocation> list) {
        tableModel.setRowCount(0);
        for (Allocation a : list) {
            tableModel.addRow(new Object[]{
                    a.getAllocationId(),
                    a.getStudentName(),
                    a.getRegisterNumber(),
                    a.getRoomNumber(),
                    a.getBlockName(),
                    a.getRoomType(),
                    DateUtil.format(a.getAllocationDate()),
                    a.getVacateDate() != null ? DateUtil.format(a.getVacateDate()) : "-",
                    a.getStatus(),
                    a.getAllocatedByName() != null ? a.getAllocatedByName() : "System",
                    a.getNotes() != null ? a.getNotes() : ""
            });
        }
    }

    private void vacateSelectedRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an active allocation from the table to vacate.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int allocId = (int) tableModel.getValueAt(row, 0);
        String studentName = (String) tableModel.getValueAt(row, 1);
        String roomNo = (String) tableModel.getValueAt(row, 3);
        String status = (String) tableModel.getValueAt(row, 8);

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This allocation is already vacated/inactive.", "Already Vacated", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this,
                "Enter reason for vacating Room " + roomNo + " for " + studentName + ":",
                "Vacate Room Confirmation", JOptionPane.QUESTION_MESSAGE);

        if (reason != null && !reason.trim().isEmpty()) {
            try {
                allocationService.vacateRoom(allocId, reason.trim());
                JOptionPane.showMessageDialog(this, "Room vacated successfully! Bed occupancy count updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllocations();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cannot vacate room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAllocationDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Allocate Room to Student", true);
        dialog.setSize(540, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JComboBox<Student> studentBox = new JComboBox<>();
        JLabel studentInfoLbl = new JLabel("Select a student to view details");
        studentInfoLbl.setFont(UITheme.FONT_SMALL);
        studentInfoLbl.setForeground(UITheme.TEXT_MUTED);

        JComboBox<Room> roomBox = new JComboBox<>();
        JLabel roomInfoLbl = new JLabel("Select a room");
        roomInfoLbl.setFont(UITheme.FONT_SMALL);
        roomInfoLbl.setForeground(UITheme.TEXT_MUTED);

        JTextField dateField = UITheme.createTextField();
        dateField.setText(DateUtil.toSqlString(DateUtil.getToday()));
        JTextField notesField = UITheme.createTextField();
        notesField.putClientProperty("JTextField.placeholderText", "e.g., Year 3 special request");

        // Populate Unallocated Students
        try {
            List<Student> students = studentService.getAllStudents();
            for (Student s : students) {
                if (allocationService.getActiveAllocationByStudent(s.getStudentId()) == null) {
                    studentBox.addItem(s);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching unallocated students: " + e.getMessage());
        }

        // Populate Available Rooms
        try {
            List<Room> rooms = roomService.getAvailableRooms();
            for (Room r : rooms) {
                roomBox.addItem(r);
            }
        } catch (Exception e) {
            System.err.println("Error fetching available rooms: " + e.getMessage());
        }

        studentBox.addActionListener(e -> {
            Student s = (Student) studentBox.getSelectedItem();
            if (s != null) {
                studentInfoLbl.setText("Dept: " + s.getDepartment() + " | Year " + s.getAcademicYear() + " | " + s.getGender());
            }
        });

        roomBox.addActionListener(e -> {
            Room r = (Room) roomBox.getSelectedItem();
            if (r != null) {
                roomInfoLbl.setText("Type: " + r.getRoomType() + " | Free: " + r.getAvailableBeds() + "/" + r.getTotalCapacity() + " beds | ₹" + r.getMonthlyRent());
            }
        });

        // Trigger initial info
        if (studentBox.getItemCount() > 0) studentBox.setSelectedIndex(0);
        if (roomBox.getItemCount() > 0) roomBox.setSelectedIndex(0);

        form.add(new JLabel("Select Student: *"));
        form.add(studentBox);
        form.add(new JLabel("Student Info:"));
        form.add(studentInfoLbl);

        form.add(new JLabel("Select Available Room: *"));
        form.add(roomBox);
        form.add(new JLabel("Room Info:"));
        form.add(roomInfoLbl);

        form.add(new JLabel("Allocation Date: *"));
        form.add(dateField);
        form.add(new JLabel("Notes / Remarks:"));
        form.add(notesField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton allocateBtn = UITheme.createPrimaryButton("Confirm Allocation");
        allocateBtn.addActionListener(e -> {
            Student s = (Student) studentBox.getSelectedItem();
            Room r = (Room) roomBox.getSelectedItem();
            Date allocDate = DateUtil.parseSqlDate(dateField.getText());

            if (s == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a student. All students might already be allocated.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (r == null) {
                JOptionPane.showMessageDialog(dialog, "Please select an available room.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (allocDate == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid allocation date. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Allocation alloc = new Allocation(0, s.getStudentId(), r.getRoomId(), allocDate, null, "ACTIVE", null, notesField.getText().trim());
                allocationService.allocateRoom(alloc);

                JOptionPane.showMessageDialog(dialog, "Room " + r.getRoomNumber() + " successfully allocated to " + s.getFullName() + "!", "Allocation Successful", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAllocations();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Allocation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(allocateBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
