package com.smarthostel.ui.components;

import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setFont(UITheme.FONT_BODY);
        setRowHeight(36);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setGridColor(UITheme.BORDER);
        setSelectionBackground(UITheme.PRIMARY_LIGHT);
        setSelectionForeground(UITheme.PRIMARY_DARK);
        setFillsViewportHeight(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setBackground(UITheme.BG_SUBTLE);
        header.setForeground(UITheme.TEXT_MAIN);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        // Custom Cell Renderer with Padding and Alternating Striping
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(250, 252, 255));
                    }
                    c.setForeground(UITheme.TEXT_MAIN);
                } else {
                    c.setBackground(UITheme.PRIMARY_LIGHT);
                    c.setForeground(UITheme.PRIMARY_DARK);
                }

                // Status highlighting
                if (value != null) {
                    String str = value.toString();
                    if ("PAID".equalsIgnoreCase(str) || "Resolved".equalsIgnoreCase(str) || "AVAILABLE".equalsIgnoreCase(str) || "ACTIVE".equalsIgnoreCase(str)) {
                        c.setForeground(new Color(5, 150, 105)); // Green
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if ("UNPAID".equalsIgnoreCase(str) || "EMERGENCY".equalsIgnoreCase(str) || "OVERDUE".equalsIgnoreCase(str)) {
                        c.setForeground(UITheme.DANGER);
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if ("Pending".equalsIgnoreCase(str) || "PARTIALLY_PAID".equalsIgnoreCase(str) || "In Progress".equalsIgnoreCase(str)) {
                        c.setForeground(new Color(217, 119, 6)); // Amber
                        setFont(UITheme.FONT_BODY_BOLD);
                    }
                }

                return c;
            }
        });
    }
}
