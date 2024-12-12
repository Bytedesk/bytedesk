package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.Map;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatBlockOptions extends BlockOptions {
    private List<Message> messages;
    private String model;
    private Map<String, Object> modelConfig;
    private String systemPrompt;
    private String errorMessage;
} 
