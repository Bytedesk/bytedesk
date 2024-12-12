package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.AudioBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.TextToSpeechConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AudioBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    
    public AudioBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.AUDIO.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        AudioBlockOptions options = objectMapper.convertValue(block.getOptions(), AudioBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            switch (options.getSourceType().toUpperCase()) {
                case "URL":
                    handleUrlAudio(options, result);
                    break;
                case "UPLOAD":
                    handleUploadedAudio(options, result);
                    break;
                case "TEXT_TO_SPEECH":
                    handleTextToSpeech(options, result, context);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported audio source type: " + options.getSourceType());
            }
            
            result.put("autoplay", options.isAutoplay());
            
            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), true);
            }
            
            result.put("success", true);
            result.put("blockType", "audio");
            
        } catch (Exception e) {
            log.error("Audio block processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            AudioBlockOptions options = objectMapper.convertValue(block.getOptions(), AudioBlockOptions.class);
            return options.getSourceType() != null && 
                   (("URL".equalsIgnoreCase(options.getSourceType()) && options.getUrl() != null) ||
                    ("UPLOAD".equalsIgnoreCase(options.getSourceType()) && options.getContent() != null) ||
                    ("TEXT_TO_SPEECH".equalsIgnoreCase(options.getSourceType()) && options.getTtsConfig() != null));
        } catch (Exception e) {
            return false;
        }
    }

    private void handleUrlAudio(AudioBlockOptions options, Map<String, Object> result) {
        result.put("audioUrl", options.getUrl());
        result.put("mimeType", options.getMimeType());
    }

    private void handleUploadedAudio(AudioBlockOptions options, Map<String, Object> result) {
        result.put("audioContent", options.getContent());
        result.put("mimeType", options.getMimeType());
    }

    private void handleTextToSpeech(AudioBlockOptions options, Map<String, Object> result, Map<String, Object> context) {
        TextToSpeechConfig ttsConfig = options.getTtsConfig();
        
        // 处理文本中的变量
        String processedText = processTemplate(ttsConfig.getText(), context);
        
        // TODO: 实现文字转语音的具体逻辑
        throw new UnsupportedOperationException("Text-to-speech not implemented yet");
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
