package com.example.crm_backend.utils;

import java.util.Base64;

public class Decoder {
    public static String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}