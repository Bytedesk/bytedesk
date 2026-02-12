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
public class AgentQuickReplyButtonListConverter implements AttributeConverter<List<AgentQuickReplyButton>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<AgentQuickReplyButton> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting AgentQuickReplyButton list to JSON: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<AgentQuickReplyButton> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || "[]".equals(dbData)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<AgentQuickReplyButton>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to AgentQuickReplyButton list: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
