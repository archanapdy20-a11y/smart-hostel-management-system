package com.smarthostel.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

// Application configuration loader for db.properties
public class AppConfig {

    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        // Try external config/db.properties first
        File externalConfigFile = new File("config/db.properties");
        if (externalConfigFile.exists()) {
            try (InputStream input = new FileInputStream(externalConfigFile)) {
                properties.load(input);
                System.out.println("Loaded database configuration from: " + externalConfigFile.getAbsolutePath());
                return;
            } catch (Exception e) {
                System.err.println("Failed to read external config file: " + e.getMessage());
            }
        }

        // Try parent config directory (if running from subfolder)
        File parentConfigFile = new File("../config/db.properties");
        if (parentConfigFile.exists()) {
            try (InputStream input = new FileInputStream(parentConfigFile)) {
                properties.load(input);
                System.out.println("Loaded database configuration from: " + parentConfigFile.getAbsolutePath());
                return;
            } catch (Exception e) {
                System.err.println("Failed to read parent config file: " + e.getMessage());
            }
        }

        // Classpath fallback
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("Loaded database configuration from classpath resources.");
            } else {
                System.err.println("db.properties not found in classpath. Applying defaults.");
                setDefaultProperties();
            }
        } catch (Exception e) {
            System.err.println("Error reading classpath properties: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/smart_hostel_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("app.theme", "light");
        properties.setProperty("app.name", "Smart Hostel Management System");
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getDbUrl() {
        return getProperty("db.url", "jdbc:mysql://localhost:3306/smart_hostel_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    }

    public static String getDbUser() {
        return getProperty("db.user", "root");
    }

    public static String getDbPassword() {
        return getProperty("db.password", "");
    }

    public static String getDbDriver() {
        return getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
}
