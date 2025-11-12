package com.bytedesk.ticket.ticket_settings.sub.converter;

import com.bytedesk.ticket.ticket_settings.sub.model.PrioritySettingsData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = false)
public class PrioritySettingsConverter implements AttributeConverter<PrioritySettingsData, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(PrioritySettingsData attribute) {
        try {
            if (attribute == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception ex) {
            throw new RuntimeException("Serialize PrioritySettingsData failed: " + ex.getMessage(), ex);
        }
    }
    @Override
    public PrioritySettingsData convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return PrioritySettingsData.builder().build();
            }
            return objectMapper.readValue(dbData, PrioritySettingsData.class);
        } catch (Exception ex) {
            throw new RuntimeException("Deserialize PrioritySettingsData failed: " + ex.getMessage(), ex);
        }
    }
}