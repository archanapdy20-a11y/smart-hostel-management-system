package com.smarthostel.ui.modules;

import com.smarthostel.config.SessionManager;
import com.smarthostel.dao.RoomDAO;
import com.smarthostel.model.Room;
import com.smarthostel.service.RoomService;
import com.smarthostel.ui.components.ModernTable;
import com.smarthostel.ui.components.VectorIcon;
import com.smarthostel.util.UITheme;
import com.smarthostel.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class RoomManagementPanel extends JPanel {

    private final RoomService roomService = new RoomService();
    private final RoomDAO roomDAO = new RoomDAO();
    private DefaultTableModel tableModel;
    private ModernTable table;
    private JTextField searchField;
    private JComboBox<String> filterBox;
    private List<Room> currentRoomList;

    private JLabel totalRoomsLbl;
    private JLabel totalBedsLbl;
    private JLabel occupiedBedsLbl;
    private JLabel availableBedsLbl;

    public RoomManagementPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(22, 24, 24, 24));

        initUI();
        loadRooms();
    }

    private void initUI() {
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.BG_MAIN);

        // 1. Hostel Room KPI Metric Banner
        JPanel kpiCard = UITheme.createCard();
        kpiCard.setLayout(new GridLayout(1, 4, 16, 0));

        totalRoomsLbl = new JLabel("0 Rooms", new VectorIcon(VectorIcon.Type.BUILDING, 20, UITheme.PRIMARY), SwingConstants.LEFT);
        totalRoomsLbl.setFont(UITheme.FONT_SUBTITLE);
        totalRoomsLbl.setIconTextGap(8);

        totalBedsLbl = new JLabel("0 Total Beds", new VectorIcon(VectorIcon.Type.BED, 20, new Color(14, 165, 233)), SwingConstants.LEFT);
        totalBedsLbl.setFont(UITheme.FONT_SUBTITLE);
        totalBedsLbl.setIconTextGap(8);

        occupiedBedsLbl = new JLabel("0 Occupied", new VectorIcon(VectorIcon.Type.STUDENTS, 20, UITheme.WARNING), SwingConstants.LEFT);
        occupiedBedsLbl.setFont(UITheme.FONT_SUBTITLE);
        occupiedBedsLbl.setIconTextGap(8);

        availableBedsLbl = new JLabel("0 Available", new VectorIcon(VectorIcon.Type.CHECK, 20, UITheme.SUCCESS), SwingConstants.LEFT);
        availableBedsLbl.setFont(UITheme.FONT_SUBTITLE);
        availableBedsLbl.setIconTextGap(8);

        kpiCard.add(createKpiBox("TOTAL ROOMS", totalRoomsLbl));
        kpiCard.add(createKpiBox("BED CAPACITY", totalBedsLbl));
        kpiCard.add(createKpiBox("OCCUPIED BEDS", occupiedBedsLbl));
        kpiCard.add(createKpiBox("AVAILABLE BEDS", availableBedsLbl));

        topContainer.add(kpiCard);
        topContainer.add(Box.createVerticalStrut(12));

        // 2. Toolbar Card: Filter + Search + Action Buttons
        JPanel toolbarCard = UITheme.createCard();
        toolbarCard.setLayout(new BorderLayout(16, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(UITheme.BG_CARD);

        JLabel filterLabel = new JLabel("Filter:", new VectorIcon(VectorIcon.Type.FILTER, 14, UITheme.TEXT_MUTED), SwingConstants.LEFT);
        filterLabel.setFont(UITheme.FONT_BODY_BOLD);
        filterLabel.setIconTextGap(6);
        filterPanel.add(filterLabel);

        filterBox = new JComboBox<>(new String[]{"All Rooms", "Available Only", "Occupied Only"});
        filterBox.addActionListener(e -> filterRooms());
        filterPanel.add(filterBox);

        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Room No, Block...");
        searchField.addActionListener(e -> searchRooms());
        filterPanel.add(searchField);

        JButton btnSearch = UITheme.createSecondaryButton("Search", new VectorIcon(VectorIcon.Type.SEARCH, 14, UITheme.TEXT_MAIN));
        btnSearch.addActionListener(e -> searchRooms());
        filterPanel.add(btnSearch);

        JButton btnReset = UITheme.createSecondaryButton("Reset", new VectorIcon(VectorIcon.Type.REFRESH, 14, UITheme.TEXT_MAIN));
        btnReset.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            loadRooms();
        });
        filterPanel.add(btnReset);

        toolbarCard.add(filterPanel, BorderLayout.WEST);

        // Right Actions - Admin only
        if (SessionManager.getInstance().isAdmin()) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setBackground(UITheme.BG_CARD);

            JButton btnAdd = UITheme.createPrimaryButton("Add Room", new VectorIcon(VectorIcon.Type.ADD, 14, Color.WHITE));
            btnAdd.addActionListener(e -> showRoomDialog(null));
            actionPanel.add(btnAdd);

            JButton btnEdit = UITheme.createSecondaryButton("Edit", new VectorIcon(VectorIcon.Type.EDIT, 14, UITheme.TEXT_MAIN));
            btnEdit.addActionListener(e -> editSelectedRoom());
            actionPanel.add(btnEdit);

            JButton btnDelete = UITheme.createDangerButton("Delete", new VectorIcon(VectorIcon.Type.DELETE, 14, Color.WHITE));
            btnDelete.addActionListener(e -> deleteSelectedRoom());
            actionPanel.add(btnDelete);

            toolbarCard.add(actionPanel, BorderLayout.EAST);
        }

        topContainer.add(toolbarCard);
        add(topContainer, BorderLayout.NORTH);

        // Center Table
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        String[] cols = {
                "Room ID", "Room No", "Block Name", "Floor", "Type",
                "Total Beds", "Occupied Beds", "Available Beds", "Monthly Rent", "Status"
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

    private JPanel createKpiBox(String title, JLabel label) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL_BOLD);
        titleLbl.setForeground(new Color(100, 116, 139));
        box.add(titleLbl);
        box.add(Box.createVerticalStrut(4));
        box.add(label);
        return box;
    }

    public void loadRooms() {
        try {
            currentRoomList = roomService.getAllRooms();
            populateTable(currentRoomList);
            updateKpis();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateKpis() {
        try {
            RoomDAO.RoomStats stats = roomDAO.getRoomStats();
            totalRoomsLbl.setText(currentRoomList.size() + " Rooms");
            totalBedsLbl.setText(stats.totalBeds + " Total Beds");
            occupiedBedsLbl.setText(stats.occupiedBeds + " Occupied");
            availableBedsLbl.setText(stats.availableBeds + " Available");
        } catch (Exception ignored) {}
    }

    private void filterRooms() {
        int idx = filterBox.getSelectedIndex();
        try {
            if (idx == 1) {
                currentRoomList = roomService.getAvailableRooms();
            } else if (idx == 2) {
                currentRoomList = roomService.getOccupiedRooms();
            } else {
                currentRoomList = roomService.getAllRooms();
            }
            populateTable(currentRoomList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchRooms() {
        String q = searchField.getText().trim();
        try {
            currentRoomList = roomService.searchRooms(q);
            populateTable(currentRoomList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Room> list) {
        tableModel.setRowCount(0);
        for (Room r : list) {
            tableModel.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomNumber(),
                    r.getBlockName(),
                    "Floor " + r.getFloorNumber(),
                    r.getRoomType(),
                    r.getTotalCapacity(),
                    r.getOccupiedCapacity(),
                    r.getAvailableBeds(),
                    UITheme.formatCurrency(r.getMonthlyRent()),
                    r.getStatus()
            });
        }
    }

    private void editSelectedRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int roomId = (int) tableModel.getValueAt(row, 0);
        Room room = currentRoomList.stream().filter(r -> r.getRoomId() == roomId).findFirst().orElse(null);
        if (room != null) {
            showRoomDialog(room);
        }
    }

    private void deleteSelectedRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int roomId = (int) tableModel.getValueAt(row, 0);
        String roomNo = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Room " + roomNo + "?\nNote: Rooms with active residents cannot be deleted.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomService.deleteRoom(roomId);
                JOptionPane.showMessageDialog(this, "Room deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRooms();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot Delete", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRoomDialog(Room room) {
        boolean isEdit = room != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Room" : "Add New Room", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UITheme.BG_CARD);

        JTextField roomNoField = UITheme.createTextField();
        JTextField blockField = UITheme.createTextField();
        JComboBox<Integer> floorBox = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5, 6});
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Single", "Double", "Triple", "Deluxe"});
        JComboBox<Integer> capBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 6});
        JTextField rentField = UITheme.createTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "UNDER_MAINTENANCE"});

        if (isEdit) {
            roomNoField.setText(room.getRoomNumber());
            blockField.setText(room.getBlockName());
            floorBox.setSelectedItem(room.getFloorNumber());
            typeBox.setSelectedItem(room.getRoomType());
            capBox.setSelectedItem(room.getTotalCapacity());
            rentField.setText(room.getMonthlyRent().toPlainString());
            statusBox.setSelectedItem(room.getStatus());
        } else {
            typeBox.setSelectedItem("Double");
            capBox.setSelectedItem(2);
            rentField.setText("5000.00");
        }

        typeBox.addActionListener(e -> {
            String t = (String) typeBox.getSelectedItem();
            if ("Single".equals(t)) capBox.setSelectedItem(1);
            else if ("Double".equals(t) || "Deluxe".equals(t)) capBox.setSelectedItem(2);
            else if ("Triple".equals(t)) capBox.setSelectedItem(3);
        });

        form.add(new JLabel("Room Number: *"));
        form.add(roomNoField);
        form.add(new JLabel("Block Name: *"));
        form.add(blockField);
        form.add(new JLabel("Floor Number: *"));
        form.add(floorBox);
        form.add(new JLabel("Room Type: *"));
        form.add(typeBox);
        form.add(new JLabel("Total Bed Capacity: *"));
        form.add(capBox);
        form.add(new JLabel("Monthly Rent (" + UITheme.CURRENCY_SYMBOL + "): *"));
        form.add(rentField);
        form.add(new JLabel("Room Status: *"));
        form.add(statusBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(UITheme.BG_SUBTLE);
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton(isEdit ? "Update Room" : "Save Room");
        saveBtn.addActionListener(e -> {
            try {
                if (!ValidationUtil.isPositiveDecimal(rentField.getText())) {
                    throw new IllegalArgumentException("Monthly rent must be a valid positive amount.");
                }
                BigDecimal rent = new BigDecimal(rentField.getText().trim());

                if (isEdit) {
                    room.setRoomNumber(roomNoField.getText().trim());
                    room.setBlockName(blockField.getText().trim());
                    room.setFloorNumber((Integer) floorBox.getSelectedItem());
                    room.setRoomType((String) typeBox.getSelectedItem());
                    room.setTotalCapacity((Integer) capBox.getSelectedItem());
                    room.setMonthlyRent(rent);
                    room.setStatus((String) statusBox.getSelectedItem());

                    roomService.updateRoom(room);
                    JOptionPane.showMessageDialog(dialog, "Room updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Room newR = new Room(0, roomNoField.getText().trim(), blockField.getText().trim(),
                            (Integer) floorBox.getSelectedItem(), (String) typeBox.getSelectedItem(),
                            (Integer) capBox.getSelectedItem(), 0, rent, (String) statusBox.getSelectedItem());
                    roomService.addRoom(newR);
                    JOptionPane.showMessageDialog(dialog, "New room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                dialog.dispose();
                loadRooms();

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
