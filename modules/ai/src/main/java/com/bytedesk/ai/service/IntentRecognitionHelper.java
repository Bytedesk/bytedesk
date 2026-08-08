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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot_settings.tools.ResolvedRobotToolIntent;
import com.bytedesk.ai.robot_settings.tools.RobotToolIntentContext;
import com.bytedesk.ai.robot_settings.tools.ToolIntentMatchMode;
import com.bytedesk.ai.springai.service.ChatClientInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles intent recognition (model-based or keyword-based) to determine
 * whether a user message should trigger tool execution.
 * <p>
 * Extracted from BaseSpringAIService to reduce class size and improve
 * separation of concerns.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IntentRecognitionHelper {

    private final ChatClientInfoService chatClientInfoService;
    // private final LlmProviderRestService llmProviderRestService;

    private static final Pattern MATH_EXPRESSION_PATTERN = Pattern.compile(
            "\\b\\d+(?:\\.\\d+)?\\s*[+\\-*/xX]\\s*\\d+(?:\\.\\d+)?\\b");

    /**
     * Callback interface for invoking the structured sync request processor
     * (delegates to BaseSpringAIService.processSyncRequest).
     */
    @FunctionalInterface
    public interface StructuredSyncCallback {
        <T> T invoke(String query, RobotProtobuf robot, String provider, String model, Class<T> outputClass);
    }

    // ──────────────────── public API ────────────────────────────────────────

    /**
     * Determines whether the user message is likely a tool invocation intent.
     */
    public boolean isLikelyToolIntent(RobotProtobuf robot, String message) {
        if (!StringUtils.hasText(message)) {
            return false;
        }

        RobotToolIntentContext intentContext = resolveToolIntentContext(robot);
        String normalized = message.toLowerCase(Locale.ROOT).trim();

        IntentRecognitionResult modelResult = maybeRecognizeToolIntent(robot, intentContext, message, null);
        if (intentContext != null && intentContext.tools() != null && !intentContext.tools().isEmpty()) {
            for (ResolvedRobotToolIntent toolIntent : intentContext.tools()) {
                if (matchesToolIntent(toolIntent, normalized, modelResult)) {
                    return true;
                }
            }
            return Boolean.TRUE.equals(modelResult != null ? modelResult.getShouldCallTool() : null)
                    && hasModelMatchedConfiguredTool(intentContext, modelResult);
        }

        if (ProviderToolServiceDispatcher.containsAnyKeyword(normalized, fallbackKeywords("add"))
                || MATH_EXPRESSION_PATTERN.matcher(normalized).find()) {
            return true;
        }
        return false;
    }

    /**
     * Version that accepts a pre-configured callback for model invocation.
     */
    public boolean isLikelyToolIntent(RobotProtobuf robot, String message, StructuredSyncCallback callback) {
        if (!StringUtils.hasText(message)) {
            return false;
        }

        RobotToolIntentContext intentContext = resolveToolIntentContext(robot);
        String normalized = message.toLowerCase(Locale.ROOT).trim();

        IntentRecognitionResult modelResult = maybeRecognizeToolIntent(robot, intentContext, message, callback);
        if (intentContext != null && intentContext.tools() != null && !intentContext.tools().isEmpty()) {
            for (ResolvedRobotToolIntent toolIntent : intentContext.tools()) {
                if (matchesToolIntent(toolIntent, normalized, modelResult)) {
                    return true;
                }
            }
            return Boolean.TRUE.equals(modelResult != null ? modelResult.getShouldCallTool() : null)
                    && hasModelMatchedConfiguredTool(intentContext, modelResult);
        }

        return ProviderToolServiceDispatcher.containsAnyKeyword(normalized, fallbackKeywords("add"))
                || MATH_EXPRESSION_PATTERN.matcher(normalized).find();
    }

    /**
     * Resolves a specific tool name from intent context using configured match
     * modes (KEYWORD / MODEL / HYBRID).
     *
     * @param callback optional callback for model-based intent recognition
     */
    public String resolveRuntimeToolNameFromIntent(RobotProtobuf robot,
            RobotToolIntentContext intentContext, String message, StructuredSyncCallback callback) {
        if (intentContext == null || intentContext.tools() == null || intentContext.tools().isEmpty()
                || !StringUtils.hasText(message)) {
            return null;
        }

        IntentRecognitionResult modelResult = maybeRecognizeToolIntent(robot, intentContext, message, callback);
        String matchedByModel = resolveMatchedToolNameFromModel(intentContext, modelResult);
        if (StringUtils.hasText(matchedByModel)) {
            return matchedByModel;
        }

        String normalized = message.toLowerCase(Locale.ROOT).trim();
        for (ResolvedRobotToolIntent toolIntent : intentContext.tools()) {
            if (matchesToolIntent(toolIntent, normalized, modelResult)
                    && StringUtils.hasText(toolIntent.toolName())) {
                return toolIntent.toolName().trim();
            }
        }
        return null;
    }

    /**
     * Resolves the tool intent context from robot configuration.
     */
    public RobotToolIntentContext resolveToolIntentContext(RobotProtobuf robot) {
        if (robot == null || robot.getLlm() == null || robot.getLlm().getToolIntentContext() == null) {
            return RobotToolIntentContext.empty();
        }
        return robot.getLlm().getToolIntentContext();
    }

    // ──────────────────── intent matching ───────────────────────────────────

    private boolean matchesToolIntent(ResolvedRobotToolIntent toolIntent, String normalizedMessage,
            IntentRecognitionResult modelResult) {
        if (toolIntent == null || !StringUtils.hasText(normalizedMessage)) {
            return false;
        }

        String matchMode = ToolIntentMatchMode.normalize(toolIntent.intentMatchMode());
        boolean matchedByModel = matchesToolIntentByModel(toolIntent, modelResult);
        if (ToolIntentMatchMode.MODEL.name().equalsIgnoreCase(matchMode)) {
            return matchedByModel;
        }

        List<String> keywords = new ArrayList<>();
        if (toolIntent.intentKeywords() != null && !toolIntent.intentKeywords().isEmpty()) {
            keywords.addAll(toolIntent.intentKeywords());
        }
        keywords.addAll(fallbackKeywords(toolIntent.toolName()));

        boolean matchedByKeyword = ProviderToolServiceDispatcher.containsAnyKeyword(normalizedMessage, keywords)
                || (isMathTool(toolIntent.toolName())
                        && MATH_EXPRESSION_PATTERN.matcher(normalizedMessage).find());

        if (ToolIntentMatchMode.HYBRID.name().equalsIgnoreCase(matchMode)) {
            return matchedByModel || matchedByKeyword;
        }
        return matchedByKeyword;
    }

    private boolean matchesToolIntentByModel(ResolvedRobotToolIntent toolIntent,
            IntentRecognitionResult modelResult) {
        if (toolIntent == null || modelResult == null || !Boolean.TRUE.equals(modelResult.getShouldCallTool())) {
            return false;
        }
        if (modelResult.getMatchedTools() == null || modelResult.getMatchedTools().isEmpty()) {
            return false;
        }

        for (String matchedTool : modelResult.getMatchedTools()) {
            if (!StringUtils.hasText(matchedTool)) {
                continue;
            }
            String normalizedMatchedTool = matchedTool.trim();
            if (equalsIgnoreCase(normalizedMatchedTool, toolIntent.toolName())
                    || equalsIgnoreCase(normalizedMatchedTool, toolIntent.toolKey())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasModelMatchedConfiguredTool(RobotToolIntentContext intentContext,
            IntentRecognitionResult modelResult) {
        if (intentContext == null || intentContext.tools() == null || intentContext.tools().isEmpty()) {
            return false;
        }
        if (modelResult == null || !Boolean.TRUE.equals(modelResult.getShouldCallTool())) {
            return false;
        }
        if (modelResult.getMatchedTools() == null || modelResult.getMatchedTools().isEmpty()) {
            return intentContext.tools().size() == 1;
        }

        for (ResolvedRobotToolIntent toolIntent : intentContext.tools()) {
            if (matchesToolIntentByModel(toolIntent, modelResult)) {
                return true;
            }
        }
        return false;
    }

    private String resolveMatchedToolNameFromModel(RobotToolIntentContext intentContext,
            IntentRecognitionResult modelResult) {
        if (intentContext == null || intentContext.tools() == null || intentContext.tools().isEmpty()) {
            return null;
        }
        if (modelResult == null || !Boolean.TRUE.equals(modelResult.getShouldCallTool())) {
            return null;
        }
        for (ResolvedRobotToolIntent toolIntent : intentContext.tools()) {
            if (matchesToolIntentByModel(toolIntent, modelResult)
                    && StringUtils.hasText(toolIntent.toolName())) {
                return toolIntent.toolName().trim();
            }
        }
        if (intentContext.tools().size() == 1
                && StringUtils.hasText(intentContext.tools().get(0).toolName())) {
            return intentContext.tools().get(0).toolName().trim();
        }
        return null;
    }

    // ──────────────────── model-based intent recognition ────────────────────

    private IntentRecognitionResult maybeRecognizeToolIntent(RobotProtobuf robot,
            RobotToolIntentContext intentContext, String message, StructuredSyncCallback callback) {
        if (robot == null || robot.getLlm() == null || !StringUtils.hasText(message)) {
            return null;
        }
        if (intentContext == null || !Boolean.TRUE.equals(intentContext.intentRecognitionEnabled())
                || intentContext.tools() == null || intentContext.tools().isEmpty()) {
            return null;
        }
        if (intentContext.tools().stream().noneMatch(this::requiresModelRecognition)) {
            return null;
        }

        return callIntentRecognitionModel(robot, intentContext, message, callback);
    }

    private boolean requiresModelRecognition(ResolvedRobotToolIntent toolIntent) {
        if (toolIntent == null) {
            return false;
        }
        String matchMode = ToolIntentMatchMode.normalize(toolIntent.intentMatchMode());
        return ToolIntentMatchMode.MODEL.name().equalsIgnoreCase(matchMode)
                || ToolIntentMatchMode.HYBRID.name().equalsIgnoreCase(matchMode);
    }

    private IntentRecognitionResult callIntentRecognitionModel(RobotProtobuf robot,
            RobotToolIntentContext intentContext, String message, StructuredSyncCallback callback) {
        String provider = resolveIntentRecognitionProvider(robot, intentContext);
        String model = resolveIntentRecognitionModel(robot, intentContext);
        if (!StringUtils.hasText(provider) || !StringUtils.hasText(model) || chatClientInfoService == null) {
            log.debug("Skip intent recognition model: provider/model unavailable, provider={}, model={}, robot={}",
                    provider, model, robot != null ? robot.getUid() : null);
            return null;
        }
        if (callback == null) {
            log.debug("Skip intent recognition model: no callback (no sync processor available)");
            return null;
        }

        BeanOutputConverter<IntentRecognitionResult> outputConverter = new BeanOutputConverter<>(
                IntentRecognitionResult.class);
        String query = buildIntentRecognitionPrompt(message, intentContext, outputConverter.getFormat());
        Integer timeoutMs = sanitizeIntentTimeout(intentContext.intentTimeoutMs());
        long startedAtNanos = System.nanoTime();

        try {
            IntentRecognitionResult result;
            if (timeoutMs != null && timeoutMs > 0) {
                result = CompletableFuture
                        .supplyAsync(() -> callback.invoke(query, robot, provider, model,
                                IntentRecognitionResult.class))
                        .get(timeoutMs, TimeUnit.MILLISECONDS);
            } else {
                result = callback.invoke(query, robot, provider, model, IntentRecognitionResult.class);
            }

            IntentRecognitionResult sanitized = sanitizeIntentRecognitionResult(result, intentContext);
            log.info(
                    "Intent recognition finished: provider={}, model={}, shouldCallTool={}, matchedTools={}, latencyMs={}, reason={}",
                    provider, model,
                    sanitized != null ? sanitized.getShouldCallTool() : null,
                    sanitized != null ? sanitized.getMatchedTools() : null,
                    ProviderToolServiceDispatcher.elapsedMillis(startedAtNanos),
                    sanitized != null ? sanitized.getReason() : null);
            return sanitized;
        } catch (TimeoutException e) {
            log.warn("Intent recognition timed out: provider={}, model={}, timeoutMs={}, robot={}",
                    provider, model, timeoutMs, robot.getUid());
            return null;
        } catch (Exception e) {
            log.warn("Intent recognition failed: provider={}, model={}, robot={}, error={}",
                    provider, model, robot.getUid(), e.getMessage());
            return null;
        }
    }

    private Integer sanitizeIntentTimeout(Integer timeoutMs) {
        return timeoutMs != null && timeoutMs > 0 ? timeoutMs : null;
    }

    // ──────────────────── provider / model resolution ───────────────────────

    public String resolveIntentRecognitionProvider(RobotProtobuf robot,
            RobotToolIntentContext intentContext) {
        if (intentContext != null && StringUtils.hasText(intentContext.intentProvider())) {
            return intentContext.intentProvider().trim();
        }
        return robot != null && robot.getLlm() != null
                && StringUtils.hasText(robot.getLlm().getTextProvider())
                        ? robot.getLlm().getTextProvider().trim()
                        : null;
    }

    public String resolveIntentRecognitionModel(RobotProtobuf robot,
            RobotToolIntentContext intentContext) {
        if (intentContext != null && StringUtils.hasText(intentContext.intentModel())) {
            return intentContext.intentModel().trim();
        }
        return robot != null && robot.getLlm() != null
                && StringUtils.hasText(robot.getLlm().getTextModel())
                        ? robot.getLlm().getTextModel().trim()
                        : null;
    }

    // private String resolveProviderTypeByUidOrLegacyValue(String configuredProviderUid) {
    //     String normalized = StringUtils.hasText(configuredProviderUid) ? configuredProviderUid.trim() : null;
    //     if (!StringUtils.hasText(normalized)) {
    //         return null;
    //     }
    //     if (llmProviderRestService != null) {
    //         LlmProviderEntity provider = llmProviderRestService.findByUid(normalized).orElse(null);
    //         if (provider != null && StringUtils.hasText(provider.getType())) {
    //             return provider.getType().trim();
    //         }
    //     }
    //     return looksLikeNumericUid(normalized) ? null : normalized;
    // }

    // private static boolean looksLikeNumericUid(String value) {
    //     if (!StringUtils.hasText(value)) {
    //         return false;
    //     }
    //     for (int index = 0; index < value.length(); index++) {
    //         if (!Character.isDigit(value.charAt(index))) {
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    // ──────────────────── prompt building ───────────────────────────────────

    private String buildIntentRecognitionPrompt(String message, RobotToolIntentContext intentContext,
            String format) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                "You are a tool intent classifier. Decide whether the user message should trigger one or more configured tools. ");
        builder.append("Return only the structured result in the requested format.\n\n");
        builder.append("User message:\n");
        builder.append(message.trim());
        builder.append("\n\nConfigured tools:\n");

        int index = 1;
        for (ResolvedRobotToolIntent toolIntent : intentContext.tools()) {
            builder.append(index++).append(". ")
                    .append("toolName=").append(nullSafe(toolIntent.toolName()))
                    .append(", toolKey=").append(nullSafe(toolIntent.toolKey()))
                    .append(", description=").append(nullSafe(toolIntent.description()))
                    .append(", matchMode=").append(nullSafe(toolIntent.intentMatchMode()))
                    .append(", keywords=")
                    .append(toolIntent.intentKeywords() != null ? toolIntent.intentKeywords() : List.of())
                    .append("\n");
        }

        builder.append("\nDecision rules:\n");
        builder.append(
                "- Set shouldCallTool to true only when at least one configured tool clearly matches the user intent.\n");
        builder.append("- matchedTools must contain only configured toolName or toolKey values.\n");
        builder.append(
                "- If no tool matches, set shouldCallTool to false and return an empty matchedTools list.\n");
        builder.append("- Keep reason short.\n\n");
        builder.append(format);
        return builder.toString();
    }

    private IntentRecognitionResult sanitizeIntentRecognitionResult(IntentRecognitionResult result,
            RobotToolIntentContext intentContext) {
        if (result == null) {
            return null;
        }

        Set<String> allowedToolNames = new HashSet<>();
        if (intentContext != null && intentContext.tools() != null) {
            for (ResolvedRobotToolIntent toolIntent : intentContext.tools()) {
                if (StringUtils.hasText(toolIntent.toolName())) {
                    allowedToolNames.add(toolIntent.toolName().trim().toLowerCase(Locale.ROOT));
                }
                if (StringUtils.hasText(toolIntent.toolKey())) {
                    allowedToolNames.add(toolIntent.toolKey().trim().toLowerCase(Locale.ROOT));
                }
            }
        }

        List<String> matchedTools = new ArrayList<>();
        if (result.getMatchedTools() != null) {
            for (String matchedTool : result.getMatchedTools()) {
                if (!StringUtils.hasText(matchedTool)) {
                    continue;
                }
                String normalized = matchedTool.trim().toLowerCase(Locale.ROOT);
                if (allowedToolNames.isEmpty() || allowedToolNames.contains(normalized)) {
                    matchedTools.add(matchedTool.trim());
                }
            }
        }

        return new IntentRecognitionResult(
                Boolean.TRUE.equals(result.getShouldCallTool()),
                matchedTools,
                StringUtils.hasText(result.getReason()) ? result.getReason().trim() : null);
    }

    // ──────────────────── utility ───────────────────────────────────────────

    private static String nullSafe(String value) {
        return value != null ? value : "";
    }

    private static boolean isMathTool(String toolName) {
        return StringUtils.hasText(toolName)
                && List.of("add", "subtract", "multiply", "divide").stream()
                        .anyMatch(expected -> expected.equalsIgnoreCase(toolName.trim()));
    }

    static List<String> fallbackKeywords(String toolName) {
        if (!StringUtils.hasText(toolName)) {
            return List.of();
        }

        String normalizedToolName = toolName.trim().toLowerCase(Locale.ROOT);
        if (List.of("getcurrentdatetime", "getcurrentdatetimemethodtoolcallback")
                .contains(normalizedToolName)) {
            return List.of("现在时间", "当前时间", "几点", "几号", "日期", "今天", "明天", "后天", "北京时间",
                    "time", "date", "day", "today", "tomorrow", "current time", "current date", "what time");
        }
        if ("setalarm".equals(normalizedToolName)) {
            return List.of("闹钟", "提醒", "定时", "alarm", "remind", "reminder", "wake me");
        }
        if ("currentweather".equals(normalizedToolName)) {
            return List.of("天气", "温度", "下雨", "下雪", "晴", "阴", "多云", "weather", "forecast", "temperature",
                    "hot", "cold");
        }
        if (isMathTool(toolName)) {
            return List.of("计算", "算一下", "加", "减", "乘", "除", "等于",
                    "plus", "minus", "multiply", "multiplied", "times", "divide", "divided", "calculate", "sum");
        }
        return List.of();
    }

    private static boolean equalsIgnoreCase(String left, String right) {
        return left != null && left.equalsIgnoreCase(right);
    }
}
