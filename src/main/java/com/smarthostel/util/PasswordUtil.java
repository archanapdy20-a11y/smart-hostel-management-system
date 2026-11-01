package com.smarthostel.util;

import org.mindrot.jbcrypt.BCrypt;

// Password Utility providing BCrypt hashing and verification
public class PasswordUtil {

    private static final int WORKLOAD = 10;

    // Hashes a raw password using BCrypt with salt
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORKLOAD));
    }

    // Verifies candidate password against stored BCrypt hash
    public static boolean checkPassword(String candidatePassword, String storedHash) {
        if (candidatePassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(candidatePassword, storedHash);
        } catch (Exception e) {
            System.err.println("BCrypt verification exception: " + e.getMessage());
            return false;
        }
    }
}
