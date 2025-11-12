package com.bytedesk.ticket.ticket_settings.sub.converter;

import com.bytedesk.ticket.ticket_settings.sub.model.StatusFlowSettingsData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JPA Converter: StatusFlowSettingsData <-> JSON String
 */
@Converter(autoApply = false)
public class StatusFlowSettingsConverter implements AttributeConverter<StatusFlowSettingsData, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(StatusFlowSettingsData attribute) {
        try {
            if (attribute == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception ex) {
            throw new RuntimeException("Serialize StatusFlowSettingsData failed: " + ex.getMessage(), ex);
        }
    }
    @Override
    public StatusFlowSettingsData convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return StatusFlowSettingsData.builder().build();
            }
            return objectMapper.readValue(dbData, StatusFlowSettingsData.class);
        } catch (Exception ex) {
            throw new RuntimeException("Deserialize StatusFlowSettingsData failed: " + ex.getMessage(), ex);
        }
    }
}