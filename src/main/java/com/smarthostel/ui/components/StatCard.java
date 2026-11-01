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

    public StatCard(String title, String value, String subtitle, Color accentColor) {
        setLayout(new BorderLayout(0, 8));
        setBackground(UITheme.BG_CARD);
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new CompoundBorder(
                        new LineBorder(UITheme.BORDER, 1),
                        new EmptyBorder(16, 18, 16, 18)
                )
        ));

        // Top: Title
        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(UITheme.TEXT_MUTED);
        add(titleLabel, BorderLayout.NORTH);

        // Center: Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(UITheme.TEXT_MAIN);
        add(valueLabel, BorderLayout.CENTER);

        // Bottom: Subtitle
        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        add(subtitleLabel, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }
}
