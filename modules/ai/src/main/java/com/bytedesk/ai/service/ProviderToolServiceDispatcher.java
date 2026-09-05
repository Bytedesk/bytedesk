/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-07 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-03 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 * 
 */
package com.bytedesk.ai.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotProtobuf;

/**
 * Provider 原生工具调用分发 SPI（社区版接口 / 企业版实现）。
 *
 * <p>工具注册表（ToolEntity/ToolRestService）与工具调用记录（ToolCallEntity/ToolCallRestService）
 * 已迁移至 enterprise/ai 模块（企业版/平台版功能）。本接口保留在 modules/ai 中，
 * 供 {@code BaseSpringAIService} 与 {@code IntentRecognitionHelper} 在社区版下编译与运行：
 * 社区版无实现 bean 时（ObjectProvider 优雅降级），机器人对话不走 provider-native 工具链路，
 * 其余 LLM 问答链路不受影响。</p>
 *
 * <p>企业版实现：{@code com.bytedesk.ai.tool.ProviderToolServiceDispatcherImpl}
 * （enterprise/ai 模块，bytedesk-enterprise-ai）。</p>
 */
public interface ProviderToolServiceDispatcher {

    /**
     * Synchronously dispatches a tool call to the appropriate provider service.
     *
     * @param robot          the robot (with llm config)
     * @param message        the user message text
     * @param runtimeContext optional runtime context propagated into tool call records
     * @param callback       optional callback for model-based intent recognition;
     *                       pass null to skip model-based recognition entirely
     * @return the tool reply text, or null if the request should not use
     *         provider-level tool routing
     */
    String tryDispatch(RobotProtobuf robot, String message, Map<String, Object> runtimeContext,
            IntentRecognitionHelper.StructuredSyncCallback callback);

    /**
     * Extracts the actual user question text from a message that may be wrapped
     * in a JSON structure or prefixed with a question label.
     */
    String extractLikelyUserMessage(String message);

    /**
     * Resolves the provider used for provider-native tool execution.
     */
    String resolveToolExecutionProvider(RobotProtobuf robot);

    /**
     * Resolves the model used for provider-native tool execution.
     */
    String resolveToolExecutionModel(RobotProtobuf robot);

    // ──────────────────── shared static utilities ───────────────────────────
    // 供 IntentRecognitionHelper 等社区版代码复用；企业版实现同样通过
    // ProviderToolServiceDispatcher.xxx(...) 限定调用，避免逻辑重复。

    /**
     * Computes elapsed milliseconds since the given start timestamp (nanos).
     */
    static long elapsedMillis(long startedAtNanos) {
        long elapsedNanos = System.nanoTime() - startedAtNanos;
        return elapsedNanos > 0 ? elapsedNanos / 1_000_000L : 0L;
    }

    /**
     * Returns true if the (lowercased) text contains any of the given keywords.
     */
    static boolean containsAnyKeyword(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the text contains any of the given keyword list
     * (case-insensitive, blank keywords ignored).
     */
    static boolean containsAnyKeyword(String text, List<String> keywords) {
        if (!StringUtils.hasText(text) || keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.hasText(keyword) && text.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
