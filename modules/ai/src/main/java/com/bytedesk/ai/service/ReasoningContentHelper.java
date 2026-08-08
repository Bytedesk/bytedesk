/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-07 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-07 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Extracts reasoning content from AI model responses via reflection.
 * <p>
 * Extracted from BaseSpringAIService to reduce class size.
 */
@Component
public class ReasoningContentHelper {

    /**
     * Tries to extract reasoning content from an assistant message or generation,
     * checking multiple sources in order.
     */
    public String extractReasoningContent(Generation generation, AssistantMessage assistantMessage) {
        String reasoningContent = extractReasoningContentFromObject(assistantMessage);
        if (StringUtils.hasText(reasoningContent)) {
            return reasoningContent;
        }

        reasoningContent = extractReasoningContentFromObject(generation);
        if (StringUtils.hasText(reasoningContent)) {
            return reasoningContent;
        }

        if (generation != null) {
            reasoningContent = extractReasoningContentFromMetadataObject(generation.getMetadata());
            if (StringUtils.hasText(reasoningContent)) {
                return reasoningContent;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractReasoningContentFromMetadataObject(Object metadata) {
        if (metadata instanceof Map<?, ?> map) {
            return extractReasoningContentFromMetadata((Map<String, Object>) map);
        }
        return extractReasoningContentFromObject(metadata);
    }

    @SuppressWarnings("unchecked")
    private String extractReasoningContentFromObject(Object target) {
        if (target == null) {
            return null;
        }

        for (String methodName : List.of("getReasoningContent", "getReasonContent")) {
            try {
                Object value = target.getClass().getMethod(methodName).invoke(target);
                if (value instanceof String text && StringUtils.hasText(text)) {
                    return text;
                }
            } catch (Exception ignore) {
                // ignore reflection failures and fall back to metadata inspection
            }
        }

        try {
            Object metadata = target.getClass().getMethod("getMetadata").invoke(target);
            if (metadata instanceof Map<?, ?> map) {
                return extractReasoningContentFromMetadata((Map<String, Object>) map);
            }
        } catch (Exception ignore) {
            // ignore reflection failures and fall back to metadata inspection
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractReasoningContentFromMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }

        for (String key : List.of("reasoningContent", "reasoning_content", "reasonContent")) {
            Object value = metadata.get(key);
            if (value instanceof String text && StringUtils.hasText(text)) {
                return text;
            }
        }

        for (Object value : metadata.values()) {
            if (value instanceof Map<?, ?> nestedMap) {
                String nestedReasoning = extractReasoningContentFromMetadata((Map<String, Object>) nestedMap);
                if (StringUtils.hasText(nestedReasoning)) {
                    return nestedReasoning;
                }
            }
        }

        return null;
    }
}
