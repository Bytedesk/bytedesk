package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.InputBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
public class InputBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private static final Map<String, Pattern> VALIDATION_PATTERNS = new HashMap<>();
    private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();
    
    static {
        VALIDATION_PATTERNS.put("email", Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$"));
        VALIDATION_PATTERNS.put("phone", Pattern.compile("^\\+?\\d{10,}$"));
        VALIDATION_PATTERNS.put("url", Pattern.compile("^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$"));
        VALIDATION_PATTERNS.put("number", Pattern.compile("^-?\\d*\\.?\\d+$"));
        
        ERROR_MESSAGES.put("email", "Please enter a valid email address");
        ERROR_MESSAGES.put("phone", "Please enter a valid phone number");
        ERROR_MESSAGES.put("url", "Please enter a valid URL");
        ERROR_MESSAGES.put("number", "Please enter a valid number");
        ERROR_MESSAGES.put("date", "Please enter a valid date");
    }

    public InputBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.TEXT_INPUT.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        InputBlockOptions options = objectMapper.convertValue(block.getOptions(), InputBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        String input = (String) context.get("input");
        if (input != null && options.getVariableName() != null) {
            ValidationResult validationResult = validateInput(input, options.getType(), options.getValidation());
            
            if (validationResult.isValid()) {
                Object processedValue = processValue(input, options.getType());
                result.put(options.getVariableName(), processedValue);
                result.put("isValid", true);
            } else {
                result.put("isValid", false);
                result.put("error", validationResult.getError());
            }
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            InputBlockOptions options = objectMapper.convertValue(block.getOptions(), InputBlockOptions.class);
            return options.getType() != null && options.getVariableName() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private ValidationResult validateInput(String input, String type, String customValidation) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, "This field is required");
        }

        if (customValidation != null && !customValidation.isEmpty()) {
            try {
                boolean matches = Pattern.compile(customValidation).matcher(input).matches();
                return new ValidationResult(matches, matches ? null : "Invalid format");
            } catch (Exception e) {
                log.error("Custom validation pattern error", e);
                return new ValidationResult(false, "Invalid validation pattern");
            }
        }

        switch (type.toLowerCase()) {
            case "date":
                return validateDate(input);
            case "number":
                return validateNumber(input);
            default:
                return validateWithPattern(input, type);
        }
    }

    private ValidationResult validateDate(String input) {
        try {
            LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
            return new ValidationResult(true, null);
        } catch (DateTimeParseException e) {
            return new ValidationResult(false, ERROR_MESSAGES.get("date"));
        }
    }

    private ValidationResult validateNumber(String input) {
        try {
            Double.parseDouble(input);
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, ERROR_MESSAGES.get("number"));
        }
    }

    private ValidationResult validateWithPattern(String input, String type) {
        Pattern pattern = VALIDATION_PATTERNS.get(type);
        if (pattern != null) {
            boolean matches = pattern.matcher(input).matches();
            return new ValidationResult(matches, matches ? null : ERROR_MESSAGES.get(type));
        }
        return new ValidationResult(true, null);
    }

    private Object processValue(String input, String type) {
        switch (type.toLowerCase()) {
            case "number":
                return Double.parseDouble(input);
            case "date":
                return LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
            default:
                return input;
        }
    }

    @Data
    private static class ValidationResult {
        private final boolean valid;
        private final String error;
        
        public boolean isValid() {
            return valid;
        }
    }
} 
