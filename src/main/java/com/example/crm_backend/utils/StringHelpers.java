package com.example.crm_backend.utils;

public class StringHelpers {
    public static String snakeToCamel(String str) {
        String[] parts = str.split("_");
        StringBuilder camel_case = new StringBuilder(parts[0]); // Giữ nguyên chữ đầu

        for (int i = 1; i < parts.length; i++) {
            camel_case.append(parts[i].substring(0, 1).toUpperCase()) // Viết hoa chữ cái đầu
                    .append(parts[i].substring(1)); // Giữ nguyên phần còn lại
        }

        return camel_case.toString();
    }
}
