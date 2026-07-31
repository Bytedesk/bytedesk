package com.bytedesk.ticket.ticket_settings_visibility;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TicketVisibilitySettingsConverter implements AttributeConverter<TicketVisibilitySettingsData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TicketVisibilitySettingsData attribute) {
        try {
            if (attribute == null) {
                return "{}";
            }
            attribute.normalize();
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception ex) {
            throw new RuntimeException("Serialize TicketVisibilitySettingsData failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public TicketVisibilitySettingsData convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return TicketVisibilitySettingsData.builder().build();
            }
            TicketVisibilitySettingsData data = objectMapper.readValue(dbData, TicketVisibilitySettingsData.class);
            if (data == null) {
                data = TicketVisibilitySettingsData.builder().build();
            }
            data.normalize();
            return data;
        } catch (Exception ex) {
            throw new RuntimeException("Deserialize TicketVisibilitySettingsData failed: " + ex.getMessage(), ex);
        }
    }
}