package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.FoodFeedback;
import com.smarthostel.model.Student;
import com.smarthostel.service.FoodService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class FoodFeedbackPanel extends JPanel {

    private final FoodService foodService = new FoodService();
    private DefaultTableModel tableModel;
    private ModernTable table;
    private JLabel avgRatingLabel;
    private List<FoodFeedback> currentFeedbackList;

    public FoodFeedbackPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadFeedback();
    }

    private void initUI() {
        boolean isStudent = SessionManager.getInstance().isStudent();

        // Top Toolbar
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setBackground(UITheme.BG_CARD);

        JLabel titleLbl = new JLabel("Mess Food Quality & Resident Feedback");
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_MAIN);
        leftPanel.add(titleLbl);

        avgRatingLabel = new JLabel("Avg Rating: 0.0 ★");
        avgRatingLabel.setFont(UITheme.FONT_BODY_BOLD);
        avgRatingLabel.setForeground(UITheme.WARNING);
        leftPanel.add(avgRatingLabel);

        toolbarCard.add(leftPanel, BorderLayout.WEST);

        // Actions: Student can submit feedback
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(UITheme.BG_CARD);

        if (isStudent) {
            JButton btnSubmit = UITheme.createPrimaryButton("⭐ Rate Today's Meal");
            btnSubmit.addActionListener(e -> showSubmitFeedbackDialog());
            rightPanel.add(btnSubmit);
        }

        JButton btnRefresh = UITheme.createSecondaryButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> loadFeedback());
        rightPanel.add(btnRefresh);

        toolbarCard.add(rightPanel, BorderLayout.EAST);
        add(toolbarCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        String[] cols = {"ID", "Student", "Register No", "Menu Date", "Meal Type", "Rating", "Comments", "Submitted At"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new ModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void loadFeedback() {
        boolean isStudent = SessionManager.getInstance().isStudent();
        Student s = SessionManager.getInstance().getCurrentStudent();

        try {
            if (isStudent && s != null) {
                currentFeedbackList = foodService.getFeedbackByStudent(s.getStudentId());
            } else {
                currentFeedbackList = foodService.getAllFeedback();
            }

            double avg = foodService.getAverageRating();
            avgRatingLabel.setText("Hostel Avg Rating: " + avg + " ★ (" + currentFeedbackList.size() + " Reviews)");

            tableModel.setRowCount(0);
            for (FoodFeedback fb : currentFeedbackList) {
                tableModel.addRow(new Object[]{
                        fb.getFeedbackId(),
                        fb.getStudentName(),
                        fb.getRegisterNumber(),
                        DateUtil.format(fb.getMenuDate()),
                        fb.getMealType(),
                        fb.getRatingStars() + " (" + fb.getRating() + "/5)",
                        fb.getComments() != null ? fb.getComments() : "-",
                        DateUtil.formatDateTime(fb.getSubmittedAt())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading feedback: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSubmitFeedbackDialog() {
        Student s = SessionManager.getInstance().getCurrentStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "No active student profile found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Submit Food & Mess Feedback", true);
        dialog.setSize(480, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JTextField dateField = UITheme.createTextField();
        dateField.setText(DateUtil.toSqlString(DateUtil.getToday()));

        JComboBox<String> mealBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner"});
        JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});

        JTextField commentsField = UITheme.createTextField();
        commentsField.putClientProperty("JTextField.placeholderText", "Taste, portion size, hygiene...");

        form.add(new JLabel("Meal Date (YYYY-MM-DD): *"));
        form.add(dateField);
        form.add(new JLabel("Meal Type: *"));
        form.add(mealBox);
        form.add(new JLabel("Rating (Stars 1-5): *"));
        form.add(ratingBox);
        form.add(new JLabel("Feedback Comments:"));
        form.add(commentsField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton submitBtn = UITheme.createPrimaryButton("Submit Feedback");
        submitBtn.addActionListener(e -> {
            Date date = DateUtil.parseSqlDate(dateField.getText());
            if (date == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                FoodFeedback fb = new FoodFeedback(0, s.getStudentId(), date,
                        (String) mealBox.getSelectedItem(), (Integer) ratingBox.getSelectedItem(),
                        commentsField.getText().trim(), null);

                foodService.submitFeedback(fb);
                JOptionPane.showMessageDialog(dialog, "Thank you! Your feedback has been submitted to the mess committee.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadFeedback();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Submission Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
