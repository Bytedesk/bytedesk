package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import java.util.List;

@Data
public class ChatMessage {
    private String role;  // system, user, assistant
    private String content;
    private List<ChatFunction> functions;  // 函数调用
} 
