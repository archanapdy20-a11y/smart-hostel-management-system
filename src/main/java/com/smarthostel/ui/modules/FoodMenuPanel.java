package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.FoodMenu;
import com.smarthostel.service.FoodService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.DateUtil;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class FoodMenuPanel extends JPanel {

    private final FoodService foodService = new FoodService();
    private DefaultTableModel tableModel;
    private ModernTable table;
    private JComboBox<String> viewFilterBox;
    private List<FoodMenu> currentMenuList;

    public FoodMenuPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        loadMenu();
    }

    private void initUI() {
        boolean canManage = SessionManager.getInstance().isAdmin();

        // Toolbar Card
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterPanel.setBackground(UITheme.BG_CARD);

        JLabel viewLbl = new JLabel("View Period:", new VectorIcon(VectorIcon.Type.FILTER, 13, UITheme.TEXT_MUTED), SwingConstants.LEFT);
        viewLbl.setIconTextGap(6);
        viewLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterPanel.add(viewLbl);

        viewFilterBox = new JComboBox<>(new String[]{"Today's Menu", "This Week's Menu", "All Scheduled Menus"});
        viewFilterBox.addActionListener(e -> loadMenu());
        filterPanel.add(viewFilterBox);

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh", new VectorIcon(VectorIcon.Type.REFRESH, 14, UITheme.TEXT_MAIN));
        btnRefresh.addActionListener(e -> loadMenu());
        filterPanel.add(btnRefresh);

        toolbarCard.add(filterPanel, BorderLayout.WEST);

        // Actions: Only Admin can add or edit menus
        if (canManage) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setBackground(UITheme.BG_CARD);

            JButton btnAdd = UITheme.createPrimaryButton("Add Menu Item", new VectorIcon(VectorIcon.Type.ADD, 14, Color.WHITE));
            btnAdd.addActionListener(e -> showMenuDialog());
            actionPanel.add(btnAdd);

            JButton btnDelete = UITheme.createDangerButton("Delete", new VectorIcon(VectorIcon.Type.DELETE, 14, Color.WHITE));
            btnDelete.addActionListener(e -> deleteSelectedMenu());
            actionPanel.add(btnDelete);

            toolbarCard.add(actionPanel, BorderLayout.EAST);
        }

        add(toolbarCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        String[] cols = {"Menu ID", "Date", "Day", "Meal Type", "Menu Items", "Special Dietary Notes", "Created By"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new ModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void loadMenu() {
        int idx = viewFilterBox.getSelectedIndex();
        try {
            if (idx == 0) {
                currentMenuList = foodService.getTodayMenu();
            } else if (idx == 1) {
                currentMenuList = foodService.getWeeklyMenu(DateUtil.getToday());
            } else {
                currentMenuList = foodService.getAllMenus();
            }

            tableModel.setRowCount(0);
            for (FoodMenu m : currentMenuList) {
                tableModel.addRow(new Object[]{
                        m.getMenuId(),
                        DateUtil.format(m.getMenuDate()),
                        m.getDayOfWeek(),
                        m.getMealType(),
                        m.getMenuItems(),
                        m.getSpecialNotes() != null ? m.getSpecialNotes() : "-",
                        m.getCreatedByName() != null ? m.getCreatedByName() : "Admin"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading food menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedMenu() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a menu entry to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int menuId = (int) tableModel.getValueAt(row, 0);
        String day = (String) tableModel.getValueAt(row, 2);
        String meal = (String) tableModel.getValueAt(row, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the " + meal + " menu for " + day + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                foodService.deleteMenu(menuId);
                JOptionPane.showMessageDialog(this, "Menu deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMenu();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showMenuDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Publish / Edit Food Menu", true);
        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JTextField dateField = UITheme.createTextField();
        dateField.setText(DateUtil.toSqlString(DateUtil.getToday()));

        JComboBox<String> dayBox = new JComboBox<>(new String[]{
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        });
        dayBox.setSelectedItem(LocalDate.now().getDayOfWeek().toString());

        JComboBox<String> mealBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner"});
        JTextField itemsField = UITheme.createTextField();
        itemsField.putClientProperty("JTextField.placeholderText", "e.g., Idli, Vada, Sambar, Chutney");

        JTextField notesField = UITheme.createTextField();
        notesField.putClientProperty("JTextField.placeholderText", "e.g., Seasonal fruit included");

        form.add(new JLabel("Menu Date (YYYY-MM-DD): *"));
        form.add(dateField);
        form.add(new JLabel("Day of Week: *"));
        form.add(dayBox);
        form.add(new JLabel("Meal Type: *"));
        form.add(mealBox);
        form.add(new JLabel("Food Items: *"));
        form.add(itemsField);
        form.add(new JLabel("Special Notes:"));
        form.add(notesField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Save Menu");
        saveBtn.addActionListener(e -> {
            Date date = DateUtil.parseSqlDate(dateField.getText());
            if (date == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                FoodMenu menu = new FoodMenu(0, date, (String) dayBox.getSelectedItem(),
                        (String) mealBox.getSelectedItem(), itemsField.getText().trim(),
                        notesField.getText().trim(), null);

                foodService.saveMenu(menu);
                JOptionPane.showMessageDialog(dialog, "Food menu saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
