package com.bytedesk.ticket.ticket_settings.sub.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List<Integer> <-> JSON converter for working days (1=Mon ... 7=Sun)
 */
@Converter(autoApply = false)
public class WorkingDaysConverter implements AttributeConverter<List<Integer>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final List<Integer> DEFAULT = Arrays.asList(1,2,3,4,5);

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        List<Integer> src = attribute == null || attribute.isEmpty() ? DEFAULT : attribute;
        try {
            return MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            return "[1,2,3,4,5]";
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>(DEFAULT);
        }
        try {
            return Arrays.asList(MAPPER.readValue(dbData, Integer[].class));
        } catch (Exception e) {
            return new ArrayList<>(DEFAULT);
        }
    }
}
