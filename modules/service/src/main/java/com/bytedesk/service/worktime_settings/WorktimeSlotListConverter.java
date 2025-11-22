package com.bytedesk.service.worktime_settings;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Persist {@link WorktimeSlotValue} collections as JSON text columns.
 */
@Slf4j
@Converter(autoApply = false)
public class WorktimeSlotListConverter implements AttributeConverter<List<WorktimeSlotValue>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<WorktimeSlotValue> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.warn("Unable to serialize worktime slots: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<WorktimeSlotValue> convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData) || "[]".equals(dbData)) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, new TypeReference<List<WorktimeSlotValue>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Unable to deserialize worktime slots: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
