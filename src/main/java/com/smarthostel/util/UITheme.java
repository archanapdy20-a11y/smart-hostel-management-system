package com.smarthostel.util;

import com.formdev.flatlaf.FlatLightLaf;
import com.smarthostel.ui.components.VectorIcon;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;

public class UITheme {

    // Modern Enterprise Palette
    public static final Color PRIMARY = new Color(79, 70, 229);        // Indigo-600
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);  // Indigo-700
    public static final Color PRIMARY_DARK = new Color(15, 23, 42);    // Slate-900 (Deep Sidebar)
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255); // Indigo-50
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);   // Slate-800
    public static final Color SIDEBAR_ACTIVE = new Color(79, 70, 229); // Indigo-600

    public static final Color BG_MAIN = new Color(248, 250, 252);      // Slate-50
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_SUBTLE = new Color(241, 245, 249);    // Slate-100

    public static final Color TEXT_MAIN = new Color(15, 23, 42);       // Slate-900
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // Slate-500
    public static final Color TEXT_LIGHT = Color.WHITE;

    public static final Color BORDER = new Color(226, 232, 240);       // Slate-200
    public static final Color BORDER_FOCUS = new Color(99, 102, 241);  // Indigo-500

    public static final Color SUCCESS = new Color(16, 185, 129);       // Emerald-500
    public static final Color SUCCESS_BG = new Color(236, 253, 245);
    public static final Color WARNING = new Color(245, 158, 11);       // Amber-500
    public static final Color WARNING_BG = new Color(254, 243, 199);
    public static final Color DANGER = new Color(239, 68, 68);         // Rose-500
    public static final Color DANGER_BG = new Color(254, 242, 242);
    public static final Color INFO = new Color(59, 130, 246);          // Blue-500
    public static final Color INFO_BG = new Color(239, 246, 255);

    // Font definitions
    private static final String FONT_NAME = "Segoe UI";

    public static final Font FONT_TITLE = new Font(FONT_NAME, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_NAME, Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font(FONT_NAME, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(FONT_NAME, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(FONT_NAME, Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_NAME, Font.BOLD, 11);

    // Currency symbol safely encoded
    public static final String CURRENCY_SYMBOL = "\u20B9";

    // Sets up modern FlatLaf UI properties
    public static void setupFlatLaf() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 8);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Table.selectionBackground", PRIMARY_LIGHT);
            UIManager.put("Table.selectionForeground", PRIMARY_DARK);
            UIManager.put("Table.rowHeight", 36);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
    }

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) return CURRENCY_SYMBOL + " 0.00";
        return CURRENCY_SYMBOL + " " + String.format("%,.2f", amount.doubleValue());
    }

    public static JButton createPrimaryButton(String text) {
        return createPrimaryButton(text, null);
    }

    public static JButton createPrimaryButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY);
            }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        return createSecondaryButton(text, null);
    }

    public static JButton createSecondaryButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(8, 14, 8, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_SUBTLE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_CARD);
            }
        });
        return btn;
    }

    public static JButton createDangerButton(String text) {
        return createDangerButton(text, null);
    }

    public static JButton createDangerButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createSuccessButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(8, 10, 8, 10)));
        return pf;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(16, 20, 16, 20)));
        return card;
    }

    public static JPanel createBadge(String text, Color bg, Color fg) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        badge.setBackground(bg);
        badge.setBorder(new LineBorder(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 120), 1, true));
        JLabel label = new JLabel(text);
        label.setFont(FONT_SMALL_BOLD);
        label.setForeground(fg);
        badge.add(label);
        return badge;
    }

    // Creates an icon badge with tinted circular background
    public static JComponent createIconCircle(VectorIcon.Type iconType, Color accentColor, int circleSize, int iconSize) {
        JPanel circle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color tint = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30);
                g2.setColor(tint);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(circleSize, circleSize));
        circle.setMaximumSize(new Dimension(circleSize, circleSize));
        circle.setMinimumSize(new Dimension(circleSize, circleSize));

        JLabel iconLabel = new JLabel(new VectorIcon(iconType, iconSize, accentColor));
        circle.add(iconLabel);
        return circle;
    }
}
