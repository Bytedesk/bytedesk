package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.ChatBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    
    public ChatBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.CHAT.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        ChatBlockOptions options = objectMapper.convertValue(block.getOptions(), ChatBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            // 处理历史消息中的变量
            List<Message> processedMessages = options.getMessages().stream()
                .<Message>map(msg -> processMessage(msg, context))
                .collect(Collectors.toList());
            
            // 执行聊天请求
            result.put("messages", processedMessages);
            result.put("success", true);
        } catch (Exception e) {
            log.error("Chat processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            ChatBlockOptions options = objectMapper.convertValue(block.getOptions(), ChatBlockOptions.class);
            return options.getMessages() != null && 
                   !options.getMessages().isEmpty() &&
                   options.getModel() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private Message processMessage(Message message, Map<String, Object> context) {
        Message processed = new Message();
        processed.setRole(message.getRole());
        processed.setContent(processTemplate(message.getContent(), context));
        processed.setFunctions(message.getFunctions());
        return processed;
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
