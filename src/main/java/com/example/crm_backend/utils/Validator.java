package com.example.crm_backend.utils;

import java.util.regex.Pattern;

public class Validator {
    public static boolean isEmpty(String str){
        return str == null || str.isEmpty();
    }

    public static boolean isValidStr(String str, String regex){
        return !Validator.isEmpty(str) && Pattern.matches(regex, str);
    }

    public static boolean isValidHexColor(String color) {
        if (color == null || !color.startsWith("#")) return false;
        String hex = color.substring(1);
        if (hex.length() != 3 && hex.length() != 6) return false;

        try {
            Integer.parseInt(hex, 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
