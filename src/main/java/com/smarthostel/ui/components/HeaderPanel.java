package com.smarthostel.ui.components;

import com.smarthostel.config.SessionManager;
import com.smarthostel.model.User;
import com.smarthostel.util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class HeaderPanel extends JPanel {

    private final JLabel titleLabel;
    private final JLabel breadcrumbLabel;
    private final Runnable onRefresh;

    public HeaderPanel(Runnable onRefresh) {
        this.onRefresh = onRefresh;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_CARD);
        setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER, 1),
                new EmptyBorder(12, 24, 12, 24)
        ));
        setPreferredSize(new Dimension(0, 64));

        // Left: Title and Breadcrumb
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UITheme.BG_CARD);

        titleLabel = new JLabel("System Dashboard");
        titleLabel.setFont(UITheme.FONT_SUBTITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        breadcrumbLabel = new JLabel("Smart Hostel Management / Overview");
        breadcrumbLabel.setFont(UITheme.FONT_SMALL);
        breadcrumbLabel.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(breadcrumbLabel);

        add(titlePanel, BorderLayout.WEST);

        // Right: Refresh button and User Profile Pill
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightPanel.setBackground(UITheme.BG_CARD);

        JButton refreshBtn = new JButton("Refresh", new VectorIcon(VectorIcon.Type.REFRESH, 14, UITheme.TEXT_MAIN));
        refreshBtn.setFont(UITheme.FONT_SMALL_BOLD);
        refreshBtn.setBackground(UITheme.BG_SUBTLE);
        refreshBtn.setForeground(UITheme.TEXT_MAIN);
        refreshBtn.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1), new EmptyBorder(6, 12, 6, 12)));
        refreshBtn.setIconTextGap(8);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> {
            if (this.onRefresh != null) this.onRefresh.run();
        });
        rightPanel.add(refreshBtn);

        // User Pill
        User user = SessionManager.getInstance().getCurrentUser();
        String name = user != null ? user.getFullName() : "Administrator";
        String initial = name.length() > 0 ? name.substring(0, 1).toUpperCase() : "U";

        JPanel userPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        userPill.setBackground(UITheme.BG_SUBTLE);
        userPill.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1), new EmptyBorder(4, 8, 4, 12)));

        JLabel avatarLabel = new JLabel(initial, SwingConstants.CENTER);
        avatarLabel.setPreferredSize(new Dimension(28, 28));
        avatarLabel.setFont(UITheme.FONT_BODY_BOLD);
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(UITheme.PRIMARY);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(UITheme.FONT_BODY_BOLD);
        nameLabel.setForeground(UITheme.TEXT_MAIN);

        userPill.add(avatarLabel);
        userPill.add(nameLabel);

        rightPanel.add(userPill);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setTitles(String title, String breadcrumb) {
        titleLabel.setText(title);
        breadcrumbLabel.setText(breadcrumb);
    }
}
