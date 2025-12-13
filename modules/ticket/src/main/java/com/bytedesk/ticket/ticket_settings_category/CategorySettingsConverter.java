package com.bytedesk.ticket.ticket_settings_category;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CategorySettingsConverter implements AttributeConverter<CategorySettingsData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CategorySettingsData attribute) {
        try {
            if (attribute == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception ex) {
            throw new RuntimeException("Serialize CategorySettingsData failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public CategorySettingsData convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return CategorySettingsData.builder().build();
            }
            CategorySettingsData data = objectMapper.readValue(dbData, CategorySettingsData.class);
            if (data == null) {
                data = CategorySettingsData.builder().build();
            }
            data.normalize();
            return data;
        } catch (Exception ex) {
            throw new RuntimeException("Deserialize CategorySettingsData failed: " + ex.getMessage(), ex);
        }
    }
}