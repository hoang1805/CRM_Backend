package com.example.crm_backend.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;

public class ObjectMapper {
    public static void mapAll(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and Target cannot be null");
        }
        BeanUtils.copyProperties(source, target);
    }
}
