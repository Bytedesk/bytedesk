package com.bytedesk.ticket.ticket_settings.sub.converter;

import com.bytedesk.ticket.ticket_settings.sub.model.CustomFieldSettingsData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA converter for CustomFieldSettingsData <-> JSON string
 */
@Slf4j
@Converter(autoApply = false)
public class CustomFieldSettingsConverter implements AttributeConverter<CustomFieldSettingsData, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CustomFieldSettingsData attribute) {
        if (attribute == null) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize CustomFieldSettingsData", e);
            return "{}";
        }
    }

    @Override
    public CustomFieldSettingsData convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return CustomFieldSettingsData.builder().build();
        }
        try {
            return MAPPER.readValue(dbData, CustomFieldSettingsData.class);
        } catch (Exception e) {
            log.error("Failed to deserialize CustomFieldSettingsData: {}", dbData, e);
            return CustomFieldSettingsData.builder().build();
        }
    }
}
