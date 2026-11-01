package com.smarthostel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton JDBC Database Connection Manager
public class DatabaseConnection {

    private static Connection connection = null;

    private DatabaseConnection() {
    }

    // Retrieves or instantiates an active MySQL JDBC Connection
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Class.forName(AppConfig.getDbDriver());
                connection = DriverManager.getConnection(
                        AppConfig.getDbUrl(),
                        AppConfig.getDbUser(),
                        AppConfig.getDbPassword()
                );
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found in classpath: " + e.getMessage(), e);
        }
        return connection;
    }

    // Creates a new isolated connection for background transactions
    public static Connection createNewConnection() throws SQLException {
        try {
            Class.forName(AppConfig.getDbDriver());
            return DriverManager.getConnection(
                    AppConfig.getDbUrl(),
                    AppConfig.getDbUser(),
                    AppConfig.getDbPassword()
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage(), e);
        }
    }

    // Checks if MySQL database is reachable
    public static boolean testConnection() {
        try (Connection testConn = createNewConnection()) {
            return testConn != null && !testConn.isClosed();
        } catch (Exception e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    // Safely closes the singleton connection on application exit
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
