package com.smarthostel.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
    private static final String DISPLAY_DATETIME_FORMAT = "dd MMM yyyy, hh:mm a";

    public static Date getToday() {
        return new Date(System.currentTimeMillis());
    }

    public static String format(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(date);
    }

    public static String format(java.util.Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(date);
    }

    public static String formatDateTime(java.util.Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATETIME_FORMAT).format(date);
    }

    public static String toSqlString(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date);
    }

    public static Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            java.util.Date parsed = new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(dateStr.trim());
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            return null;
        }
    }
}
