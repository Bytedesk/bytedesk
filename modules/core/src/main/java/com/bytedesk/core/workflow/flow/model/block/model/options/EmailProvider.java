package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import java.util.Map;

@Data
public class EmailProvider {
    private String type;
    private Map<String, String> config;
} 
