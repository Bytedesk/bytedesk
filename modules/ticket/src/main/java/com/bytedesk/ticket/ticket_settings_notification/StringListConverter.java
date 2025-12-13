package com.bytedesk.ticket.ticket_settings_notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通用 String List <-> JSON converter
 */
@Converter(autoApply = false)
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        List<String> src = attribute == null ? new ArrayList<>() : attribute;
        try {
            return MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return Arrays.asList(MAPPER.readValue(dbData, String[].class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
