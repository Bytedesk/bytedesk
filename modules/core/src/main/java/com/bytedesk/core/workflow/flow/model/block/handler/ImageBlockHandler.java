package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.ImageBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ImageBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    
    public ImageBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.IMAGE.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        ImageBlockOptions options = objectMapper.convertValue(block.getOptions(), ImageBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            switch (options.getSourceType().toUpperCase()) {
                case "URL":
                    handleUrlImage(options, result);
                    break;
                case "UPLOAD":
                    handleUploadedImage(options, result);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported image source type: " + options.getSourceType());
            }
            
            // 添加图片元数据
            if (options.getAlt() != null) {
                result.put("alt", processTemplate(options.getAlt(), context));
            }
            
            if (options.getCaption() != null) {
                result.put("caption", processTemplate(options.getCaption(), context));
            }
            
            // 添加尺寸配置
            if (options.getSize() != null) {
                result.put("size", options.getSize());
            }
            
            // 添加链接配置
            if (options.getLink() != null) {
                result.put("link", options.getLink());
            }
            
            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), true);
            }
            
            result.put("success", true);
            result.put("blockType", "image");
            
        } catch (Exception e) {
            log.error("Image block processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            ImageBlockOptions options = objectMapper.convertValue(block.getOptions(), ImageBlockOptions.class);
            return options.getSourceType() != null && 
                   (("URL".equalsIgnoreCase(options.getSourceType()) && options.getUrl() != null) ||
                    ("UPLOAD".equalsIgnoreCase(options.getSourceType()) && options.getContent() != null));
        } catch (Exception e) {
            return false;
        }
    }

    private void handleUrlImage(ImageBlockOptions options, Map<String, Object> result) {
        result.put("imageUrl", options.getUrl());
        result.put("mimeType", options.getMimeType());
    }

    private void handleUploadedImage(ImageBlockOptions options, Map<String, Object> result) {
        result.put("imageContent", options.getContent());
        result.put("mimeType", options.getMimeType());
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
