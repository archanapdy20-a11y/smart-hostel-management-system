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
    private int progressPercent = -1;
    private Color accentColor;

    public StatCard(String title, String value, VectorIcon.Type iconType, Color accentColor) {
        this(title, value, "", accentColor, iconType, -1);
    }

    public StatCard(String title, String value, String subtitle, Color accentColor, VectorIcon.Type iconType) {
        this(title, value, subtitle, accentColor, iconType, -1);
    }

    public StatCard(String title, String value, String subtitle, Color accentColor, VectorIcon.Type iconType, int progress) {
        this.accentColor = accentColor;
        this.progressPercent = progress;

        setLayout(new BorderLayout(14, 0));
        setBackground(UITheme.BG_CARD);
        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new CompoundBorder(
                        new LineBorder(UITheme.BORDER, 1),
                        new EmptyBorder(16, 18, 16, 18)
                )
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(new Color(100, 116, 139));

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        valueLabel.setForeground(new Color(15, 23, 42));

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(new Color(100, 116, 139));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subtitleLabel);

        add(textPanel, BorderLayout.CENTER);

        if (iconType != null) {
            JPanel iconWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            iconWrapper.setOpaque(false);
            iconWrapper.add(UITheme.createIconCircle(iconType, accentColor, 44, 22));
            add(iconWrapper, BorderLayout.EAST);
        }
    }

    public StatCard(String title, String value, String subtitle, Color accentColor) {
        this(title, value, subtitle, accentColor, null, -1);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public void setProgress(int percent) {
        this.progressPercent = percent;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (progressPercent >= 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int barHeight = 4;
            int y = getHeight() - barHeight;
            g2.setColor(new Color(241, 245, 249));
            g2.fillRect(4, y, getWidth() - 4, barHeight);

            int fillWidth = (int) ((getWidth() - 4) * (Math.min(100, progressPercent) / 100.0));
            g2.setColor(accentColor);
            g2.fillRect(4, y, fillWidth, barHeight);
            g2.dispose();
        }
    }
}
