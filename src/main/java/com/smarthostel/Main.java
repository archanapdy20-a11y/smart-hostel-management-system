package com.smarthostel;

import com.smarthostel.config.AppConfig;
import com.smarthostel.config.DatabaseConnection;
import com.smarthostel.ui.LoginFrame;
import com.smarthostel.util.UITheme;

import javax.swing.*;

// Smart Hostel Management System main entry point
public class Main {

    public static void main(String[] args) {
        // 1. Initialize Modern FlatLaf Theme
        SwingUtilities.invokeLater(() -> {
            UITheme.setupFlatLaf();

            // 2. Test MySQL JDBC Connectivity
            boolean isDbConnected = DatabaseConnection.testConnection();
            if (!isDbConnected) {
                String errorMsg = "<html><b>Could not establish connection to MySQL database!</b><br><br>" +
                        "Database URL: " + AppConfig.getDbUrl() + "<br>" +
                        "Database User: " + AppConfig.getDbUser() + "<br><br>" +
                        "Please verify:<br>" +
                        "1. MySQL Server is running on port 3306.<br>" +
                        "2. Database '<b>smart_hostel_db</b>' exists (run database/schema.sql).<br>" +
                        "3. Credentials in '<b>config/db.properties</b>' are correct.<br><br>" +
                        "Would you like to open the login window anyway?</html>";

                int choice = JOptionPane.showConfirmDialog(null, errorMsg, "Database Connection Notice",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) {
                    System.exit(0);
                    return;
                }
            }

            // 3. Launch Login Frame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
