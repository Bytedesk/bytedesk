package com.bytedesk.ai.robot_settings.tools;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Persist {@link RobotToolConfig} lists as JSON in a TEXT column so that we can
 * evolve the schema without altering table structures.
 */
@Slf4j
@Converter
public class RobotToolConfigListConverter implements AttributeConverter<List<RobotToolConfig>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RobotToolConfig> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize RobotToolConfig list: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<RobotToolConfig> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || "[]".equals(dbData.trim())) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<RobotToolConfig>>() {
            });
        } catch (Exception e) {
            log.error("Failed to deserialize RobotToolConfig list: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
