package com.bytedesk.ticket.ticket_settings.sub.converter;

import com.bytedesk.ticket.ticket_settings.sub.model.EmailTemplateDef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List<EmailTemplateDef> <-> JSON converter
 */
@Converter(autoApply = false)
public class EmailTemplateListConverter implements AttributeConverter<List<EmailTemplateDef>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<EmailTemplateDef> attribute) {
        List<EmailTemplateDef> src = attribute == null ? new ArrayList<>() : attribute;
        try {
            return MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<EmailTemplateDef> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return Arrays.asList(MAPPER.readValue(dbData, EmailTemplateDef[].class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
