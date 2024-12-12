package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.FieldMapping;
import com.bytedesk.core.workflow.flow.model.block.model.options.IntegrationBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IntegrationBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    
    public IntegrationBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.INTEGRATION.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        IntegrationBlockOptions options = objectMapper.convertValue(block.getOptions(), IntegrationBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            Map<String, Object> mappedData = processMappings(options.getFieldMappings(), context);
            
            // 执行集成
            Object integrationResult = handleIntegration(options, mappedData);
            
            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), integrationResult);
            }
            
            result.put("integrationResult", integrationResult);
            result.put("success", true);
            
        } catch (Exception e) {
            log.error("Integration failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            IntegrationBlockOptions options = objectMapper.convertValue(block.getOptions(), IntegrationBlockOptions.class);
            return options.getIntegrationType() != null && 
                   options.getAction() != null &&
                   options.getCredentials() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> processMappings(List<FieldMapping> mappings, Map<String, Object> context) {
        Map<String, Object> result = new HashMap<>();
        for (FieldMapping mapping : mappings) {
            String from = mapping.getFrom();
            String to = mapping.getTo();
            
            Object value = context.get(from);
            if (value != null) {
                result.put(to, value);
            }
        }
        return result;
    }

    private Object handleIntegration(IntegrationBlockOptions options, Map<String, Object> data) {
        // TODO: 实现具体的集成逻辑
        log.info("Executing integration: type={}, action={}, data={}", 
            options.getIntegrationType(), options.getAction(), data);
        return data;
    }

    private String processTemplate(String template, Map<String, Object> context) {
        if (template == null) return null;
        
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (template.contains(placeholder)) {
                template = template.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return template;
    }
} 
