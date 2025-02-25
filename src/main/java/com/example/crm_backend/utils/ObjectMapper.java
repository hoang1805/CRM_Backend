package com.example.crm_backend.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ObjectMapper {
    public static void mapAll(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and Target cannot be null");
        }
        BeanUtils.copyProperties(source, target);
    }

    public static void mapFields(Object source, Object target, List<String> fields) {
        if (source == null || target == null || fields == null) {
            throw new IllegalArgumentException("Source, Target, and FieldNames cannot be null");
        }

        for (String field : fields) {
            try {
                Field source_field = source.getClass().getDeclaredField(field);
                Field target_field = target.getClass().getDeclaredField(field);

                source_field.setAccessible(true);
                target_field.setAccessible(true);

                Object value = source_field.get(source);
                target_field.set(target, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Bỏ qua nếu field không tồn tại hoặc không thể truy cập
            }
        }
    }
}
