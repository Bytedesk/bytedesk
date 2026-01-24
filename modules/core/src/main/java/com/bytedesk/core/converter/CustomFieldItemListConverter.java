package com.bytedesk.core.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bytedesk.core.model.CustomFieldItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 通用 CustomFieldItem List <-> JSON converter
 */
@Converter(autoApply = false)
public class CustomFieldItemListConverter implements AttributeConverter<List<CustomFieldItem>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<CustomFieldItem> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<CustomFieldItem> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || "[]".equals(dbData)) {
            return new ArrayList<>();
        }
        try {
            CustomFieldItem[] items = MAPPER.readValue(dbData, CustomFieldItem[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
