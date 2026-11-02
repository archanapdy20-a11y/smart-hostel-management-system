package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.Student;
import com.smarthostel.service.StudentService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class StudentManagementPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private DefaultTableModel tableModel;
    private ModernTable table;
    private JTextField searchField;
    private List<Student> currentStudentList;

    public StudentManagementPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadStudents();
    }

    private void initUI() {
        // Top Toolbar Card: Search + Action Buttons
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        // Search Bar (Left)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(UITheme.BG_CARD);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UITheme.FONT_BODY_BOLD);
        searchPanel.add(searchLabel);

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Name, Reg No, Department...");
        searchField.addActionListener(e -> searchStudents());
        searchPanel.add(searchField);

        JButton btnSearch = UITheme.createSecondaryButton("Search", new VectorIcon(VectorIcon.Type.SEARCH, 14, UITheme.TEXT_MAIN));
        btnSearch.addActionListener(e -> searchStudents());
        searchPanel.add(btnSearch);

        JButton btnReset = UITheme.createSecondaryButton("Reset", new VectorIcon(VectorIcon.Type.REFRESH, 14, UITheme.TEXT_MAIN));
        btnReset.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });
        searchPanel.add(btnReset);

        toolbarCard.add(searchPanel, BorderLayout.WEST);

        // Action Buttons (Right) - Only Admin can Add/Edit/Delete
        boolean canManage = SessionManager.getInstance().isAdmin();

        if (canManage) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setBackground(UITheme.BG_CARD);

            JButton btnAdd = UITheme.createPrimaryButton("Register Student", new VectorIcon(VectorIcon.Type.ADD, 14, Color.WHITE));
            btnAdd.addActionListener(e -> showStudentDialog(null));
            actionPanel.add(btnAdd);

            JButton btnEdit = UITheme.createSecondaryButton("Edit", new VectorIcon(VectorIcon.Type.EDIT, 14, UITheme.TEXT_MAIN));
            btnEdit.addActionListener(e -> editSelectedStudent());
            actionPanel.add(btnEdit);

            JButton btnDelete = UITheme.createDangerButton("Delete", new VectorIcon(VectorIcon.Type.DELETE, 14, Color.WHITE));
            btnDelete.addActionListener(e -> deleteSelectedStudent());
            actionPanel.add(btnDelete);

            toolbarCard.add(actionPanel, BorderLayout.EAST);
        }

        add(toolbarCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        String[] columns = {
                "ID", "Reg Number", "Student Name", "Gender", "Dept", "Year",
                "Allocated Room", "Phone", "Email", "Parent Name", "Parent Contact", "Fee Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new ModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void loadStudents() {
        try {
            currentStudentList = studentService.getAllStudents();
            populateTable(currentStudentList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudents() {
        String query = searchField.getText().trim();
        try {
            currentStudentList = studentService.searchStudents(query);
            populateTable(currentStudentList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getRegisterNumber(),
                    s.getFullName(),
                    s.getGender(),
                    s.getDepartment(),
                    "Year " + s.getAcademicYear(),
                    s.getAllocatedRoom(),
                    s.getPhoneNumber(),
                    s.getEmail(),
                    s.getParentName(),
                    s.getParentContact(),
                    s.getFeeStatus()
            });
        }
    }

    private void editSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        Student student = currentStudentList.stream().filter(s -> s.getStudentId() == studentId).findFirst().orElse(null);
        if (student != null) {
            showStudentDialog(student);
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + name + " (ID: " + studentId + ")?\nThis action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = studentService.deleteStudent(studentId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStudents();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cannot delete student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStudentDialog(Student student) {
        boolean isEdit = student != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Student Details" : "Register New Student", true);
        dialog.setSize(580, 680);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 12));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JTextField regField = UITheme.createTextField();
        JTextField nameField = UITheme.createTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField deptField = UITheme.createTextField();
        JComboBox<Integer> yearBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JTextField dobField = UITheme.createTextField();
        JTextField phoneField = UITheme.createTextField();
        JTextField emailField = UITheme.createTextField();
        JTextField addressField = UITheme.createTextField();
        JTextField parentNameField = UITheme.createTextField();
        JTextField parentPhoneField = UITheme.createTextField();
        JTextField admDateField = UITheme.createTextField();

        dobField.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        admDateField.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");

        if (isEdit) {
            regField.setText(student.getRegisterNumber());
            nameField.setText(student.getFullName());
            genderBox.setSelectedItem(student.getGender());
            deptField.setText(student.getDepartment());
            yearBox.setSelectedItem(student.getAcademicYear());
            dobField.setText(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "");
            phoneField.setText(student.getPhoneNumber());
            emailField.setText(student.getEmail());
            addressField.setText(student.getAddress());
            parentNameField.setText(student.getParentName());
            parentPhoneField.setText(student.getParentContact());
            admDateField.setText(student.getAdmissionDate() != null ? student.getAdmissionDate().toString() : "");
        } else {
            admDateField.setText(DateUtil.toSqlString(DateUtil.getToday()));
        }

        form.add(new JLabel("Register Number: *"));
        form.add(regField);
        form.add(new JLabel("Full Name: *"));
        form.add(nameField);
        form.add(new JLabel("Gender: *"));
        form.add(genderBox);
        form.add(new JLabel("Department: *"));
        form.add(deptField);
        form.add(new JLabel("Academic Year: *"));
        form.add(yearBox);
        form.add(new JLabel("Date of Birth (YYYY-MM-DD): *"));
        form.add(dobField);
        form.add(new JLabel("Phone Number: *"));
        form.add(phoneField);
        form.add(new JLabel("Email Address: *"));
        form.add(emailField);
        form.add(new JLabel("Permanent Address: *"));
        form.add(addressField);
        form.add(new JLabel("Parent / Guardian Name: *"));
        form.add(parentNameField);
        form.add(new JLabel("Parent Contact Phone: *"));
        form.add(parentPhoneField);
        form.add(new JLabel("Admission Date (YYYY-MM-DD): *"));
        form.add(admDateField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton(isEdit ? "Update Student" : "Register Student");
        saveBtn.addActionListener(e -> {
            try {
                Date dob = DateUtil.parseSqlDate(dobField.getText());
                Date adm = DateUtil.parseSqlDate(admDateField.getText());

                if (dob == null) throw new IllegalArgumentException("Invalid Date of Birth. Use YYYY-MM-DD.");
                if (adm == null) throw new IllegalArgumentException("Invalid Admission Date. Use YYYY-MM-DD.");

                if (isEdit) {
                    student.setRegisterNumber(regField.getText().trim());
                    student.setFullName(nameField.getText().trim());
                    student.setGender((String) genderBox.getSelectedItem());
                    student.setDepartment(deptField.getText().trim());
                    student.setAcademicYear((Integer) yearBox.getSelectedItem());
                    student.setDateOfBirth(dob);
                    student.setPhoneNumber(phoneField.getText().trim());
                    student.setEmail(emailField.getText().trim());
                    student.setAddress(addressField.getText().trim());
                    student.setParentName(parentNameField.getText().trim());
                    student.setParentContact(parentPhoneField.getText().trim());
                    student.setAdmissionDate(adm);

                    studentService.updateStudent(student);
                    JOptionPane.showMessageDialog(dialog, "Student details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Student newS = new Student(0, null, regField.getText().trim(), nameField.getText().trim(),
                            (String) genderBox.getSelectedItem(), deptField.getText().trim(),
                            (Integer) yearBox.getSelectedItem(), dob, phoneField.getText().trim(),
                            emailField.getText().trim(), addressField.getText().trim(),
                            parentNameField.getText().trim(), parentPhoneField.getText().trim(), adm);

                    studentService.registerStudent(newS, true);
                    JOptionPane.showMessageDialog(dialog, "Student registered successfully! Login account initialized.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                dialog.dispose();
                loadStudents();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation / Save Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
