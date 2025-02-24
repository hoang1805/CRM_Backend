package com.example.crm_backend.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Importer {
    private static final List<String> DATE_PATTERNS = List.of(
            "dd-MM-yyyy",
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM-dd-yyyy",
            "yyyy/MM/dd",
            "yyyyMMdd",
            "dd-MM-yyyy HH:mm",
            "yyyy-MM-dd HH:mm:ss"
    );

    public static Long readNumber(String str) {
        try {
            return str != null && !str.isEmpty() ? Long.parseLong(str.trim()) : null;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Không thể chuyển đổi số: " + str);
        }
    }

    public static Boolean readBoolean(String str) {
        if (str == null) return null;
        str = str.trim().toLowerCase();
        return str.equals("true") || str.equals("1") || str.equals("yes");
    }

    public static Long readDate(String str) {
        if (str == null || str.trim().isEmpty()) return null;

        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                dateFormat.setLenient(false);
                Date date = dateFormat.parse(str.trim());
                return date.getTime(); // Trả về timestamp (milliseconds)
            } catch (ParseException ignored) {
                // Thử tiếp với format khác
            }
        }

        throw new RuntimeException("Không thể chuyển đổi ngày: " + str);
    }

    public static List<String> readList(String str) {
        return str != null && !str.trim().isEmpty()
                ? Arrays.asList(str.split(","))
                : null;
    }
}
