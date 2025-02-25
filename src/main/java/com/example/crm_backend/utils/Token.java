package com.example.crm_backend.utils;

import java.util.UUID;

public class Token {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
