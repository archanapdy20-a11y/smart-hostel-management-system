package com.smarthostel.ui.components;

import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class StatCard extends JPanel {

    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;

    public StatCard(String title, String value, String subtitle, Color accentColor, VectorIcon.Type iconType) {
        setLayout(new BorderLayout(14, 0));
        setBackground(UITheme.BG_CARD);
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new CompoundBorder(
                        new LineBorder(UITheme.BORDER, 1),
                        new EmptyBorder(14, 16, 14, 16)
                )
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(UITheme.TEXT_MUTED);

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(UITheme.TEXT_MAIN);

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subtitleLabel);

        add(textPanel, BorderLayout.CENTER);

        if (iconType != null) {
            JPanel iconWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            iconWrapper.setOpaque(false);
            iconWrapper.add(UITheme.createIconCircle(iconType, accentColor, 42, 20));
            add(iconWrapper, BorderLayout.EAST);
        }
    }

    public StatCard(String title, String value, String subtitle, Color accentColor) {
        this(title, value, subtitle, accentColor, null);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }
}
