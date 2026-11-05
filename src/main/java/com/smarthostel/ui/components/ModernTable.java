package com.smarthostel.ui.components;

import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setFont(UITheme.FONT_BODY);
        setRowHeight(40);
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
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(51, 65, 85));
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        // Pill Badge and Alternate Row Striping Renderer
        setDefaultRenderer(Object.class, new StatusBadgeCellRenderer());
    }

    private static class StatusBadgeCellRenderer extends DefaultTableCellRenderer {
        private boolean isBadge = false;
        private Color badgeBg = Color.WHITE;
        private Color badgeFg = Color.BLACK;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
            isBadge = false;

            if (!isSelected) {
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                label.setForeground(UITheme.TEXT_MAIN);
            } else {
                label.setBackground(UITheme.PRIMARY_LIGHT);
                label.setForeground(UITheme.PRIMARY_DARK);
            }

            if (value != null) {
                String str = value.toString().trim();
                if (isPositiveStatus(str)) {
                    configureBadge(label, new Color(220, 252, 231), new Color(22, 101, 52));
                } else if (isNegativeStatus(str)) {
                    configureBadge(label, new Color(254, 226, 226), new Color(185, 28, 28));
                } else if (isWarningStatus(str)) {
                    configureBadge(label, new Color(254, 243, 199), new Color(180, 83, 9));
                } else if (isInfoStatus(str)) {
                    configureBadge(label, new Color(224, 231, 255), new Color(67, 56, 202));
                }
            }
            return label;
        }

        private void configureBadge(JLabel label, Color bg, Color fg) {
            isBadge = true;
            badgeBg = bg;
            badgeFg = fg;
            label.setForeground(fg);
            label.setFont(UITheme.FONT_SMALL_BOLD);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        private boolean isPositiveStatus(String s) {
            return "PAID".equalsIgnoreCase(s) || "AVAILABLE".equalsIgnoreCase(s) ||
                   "ACTIVE".equalsIgnoreCase(s) || "Resolved".equalsIgnoreCase(s);
        }

        private boolean isNegativeStatus(String s) {
            return "UNPAID".equalsIgnoreCase(s) || "OVERDUE".equalsIgnoreCase(s) ||
                   "EMERGENCY".equalsIgnoreCase(s) || "SUSPENDED".equalsIgnoreCase(s);
        }

        private boolean isWarningStatus(String s) {
            return "Pending".equalsIgnoreCase(s) || "PARTIALLY_PAID".equalsIgnoreCase(s) ||
                   "UNDER_MAINTENANCE".equalsIgnoreCase(s) || "HIGH".equalsIgnoreCase(s);
        }

        private boolean isInfoStatus(String s) {
            return "In Progress".equalsIgnoreCase(s) || "OCCUPIED".equalsIgnoreCase(s) ||
                   "VACATED".equalsIgnoreCase(s) || "MEDIUM".equalsIgnoreCase(s);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isBadge) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeBg);
                int padX = 10;
                int padY = 7;
                g2.fillRoundRect(padX, padY, getWidth() - (padX * 2), getHeight() - (padY * 2), 14, 14);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
