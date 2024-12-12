package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import java.util.List;

@Data
public class ChatFunction {
    private String name;
    private String description;
    private List<FunctionParameter> parameters;
} 
