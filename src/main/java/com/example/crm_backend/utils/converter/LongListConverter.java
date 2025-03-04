package com.example.crm_backend.utils.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list to JSON", e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        try {
            return Arrays.asList(objectMapper.readValue(dbData, Long[].class));
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to list", e);
        }
    }
}
