package com.smarthostel.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^[0-9]{10,15}$");

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) return false;
        String clean = phone.replaceAll("[\\s\\-\\(\\)\\+]", "");
        return PHONE_PATTERN.matcher(clean).matches();
    }

    public static boolean isPositiveInteger(String str) {
        if (!isNotEmpty(str)) return false;
        try {
            int val = Integer.parseInt(str.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDecimal(String str) {
        if (!isNotEmpty(str)) return false;
        try {
            double val = Double.parseDouble(str.trim());
            return val > 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
