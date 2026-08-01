package com.bytedesk.ticket.ticket_settings_category;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TicketCategorySettingsConverter implements AttributeConverter<TicketCategorySettingsData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TicketCategorySettingsData attribute) {
        try {
            if (attribute == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception ex) {
            throw new RuntimeException("Serialize TicketCategorySettingsData failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public TicketCategorySettingsData convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return TicketCategorySettingsData.builder().build();
            }
            TicketCategorySettingsData data = objectMapper.readValue(dbData, TicketCategorySettingsData.class);
            if (data == null) {
                data = TicketCategorySettingsData.builder().build();
            }
            data.normalize();
            return data;
        } catch (Exception ex) {
            throw new RuntimeException("Deserialize CategorySettingsData failed: " + ex.getMessage(), ex);
        }
    }
}