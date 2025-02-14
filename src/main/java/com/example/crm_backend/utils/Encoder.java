package com.example.crm_backend.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Base64;

public class Encoder {
    public static String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
