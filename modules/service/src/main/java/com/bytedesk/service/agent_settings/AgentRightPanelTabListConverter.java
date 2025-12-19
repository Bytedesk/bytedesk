package com.bytedesk.service.agent_settings;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class AgentRightPanelTabListConverter implements AttributeConverter<List<AgentRightPanelTab>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<AgentRightPanelTab> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting AgentRightPanelTab list to JSON: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<AgentRightPanelTab> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || "[]".equals(dbData)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<AgentRightPanelTab>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to AgentRightPanelTab list: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
