package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.RedirectBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedirectBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private static final int MAX_DELAY = 30000; // 最大延迟30秒
    
    public RedirectBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.REDIRECT.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        RedirectBlockOptions options = objectMapper.convertValue(block.getOptions(), RedirectBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            // 处理URL中的变量
            String processedUrl = processTemplate(options.getUrl(), context);
            validateUrl(processedUrl);
            
            // 设置重定向配置
            result.put("redirectUrl", processedUrl);
            result.put("isNewTab", options.isNewTab());
            result.put("waitForRedirect", options.isWaitForRedirect());
            result.put("blockType", "redirect");
            
            // 处理延迟
            if (options.getDelay() != null && options.getDelay() > 0) {
                validateDelay(options.getDelay());
                result.put("delay", options.getDelay());
                
                if (!context.containsKey("skipDelay")) {
                    TimeUnit.MILLISECONDS.sleep(options.getDelay());
                }
            }
            
            // 设置变量
            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), true);
            }
            
            result.put("success", true);
            
        } catch (InterruptedException e) {
            log.warn("Redirect delay interrupted", e);
            Thread.currentThread().interrupt();
            result.put("error", "Redirect delay interrupted");
            result.put("success", false);
        } catch (Exception e) {
            log.error("Redirect processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            RedirectBlockOptions options = objectMapper.convertValue(block.getOptions(), RedirectBlockOptions.class);
            return options.getUrl() != null && !options.getUrl().trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Redirect URL is required");
        }
        
        // 简单的URL格式验证
        if (!url.matches("^(https?://|/).*")) {
            throw new IllegalArgumentException("Invalid URL format. Must start with http://, https:// or /");
        }
        
        // 检查URL长度
        if (url.length() > 2048) {
            throw new IllegalArgumentException("URL is too long (max 2048 characters)");
        }
    }

    private void validateDelay(Integer delay) {
        if (delay != null) {
            if (delay < 0) {
                throw new IllegalArgumentException("Delay cannot be negative");
            }
            if (delay > MAX_DELAY) {
                throw new IllegalArgumentException("Delay cannot exceed " + MAX_DELAY + " milliseconds");
            }
        }
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
