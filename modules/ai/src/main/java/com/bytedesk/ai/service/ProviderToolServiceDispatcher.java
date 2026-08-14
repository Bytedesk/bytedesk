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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.providers.dashscope.tool.DashScopeToolCallingResult;
import com.bytedesk.ai.providers.dashscope.tool.DashScopeToolService;
import com.bytedesk.ai.robot_settings.tools.ResolvedRobotToolIntent;
import com.bytedesk.ai.providers.zhipuai.tool.ZhipuaiToolService;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot_settings.tools.RobotToolIntentContext;
import com.bytedesk.ai.tool.ToolEntity;
import com.bytedesk.ai.tool.ToolRestService;
import com.bytedesk.ai.tool_call.ToolCallEntity;
import com.bytedesk.ai.tool_call.ToolCallRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dispatches tool execution requests to provider-specific tool services
 * (DashScope, ZhipuAI) and manages tool call record lifecycle.
 * <p>
 * Extracted from BaseSpringAIService to reduce class size and improve
 * separation of concerns.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderToolServiceDispatcher {

    // dashscope / zhipuai 工具服务可选注入：对应 provider 未启用时 bean 可能不存在，
    // 使用 ObjectProvider 优雅降级（调用处均有 null 检查），避免启动失败。
    private final ObjectProvider<DashScopeToolService> dashScopeToolServiceProvider;
    private final ObjectProvider<ZhipuaiToolService> zhipuaiToolServiceProvider;
    private final ToolCallRestService toolCallRestService;
    private final ToolRestService toolRestService;
    private final IntentRecognitionHelper intentRecognitionHelper;

    private static final Pattern MATH_EXPRESSION_PATTERN = Pattern.compile(
            "\\b\\d+(?:\\.\\d+)?\\s*[+\\-*/xX]\\s*\\d+(?:\\.\\d+)?\\b");

    // 可选依赖的便捷访问器：provider 未启用时返回 null。
    private DashScopeToolService dashScopeToolService() {
        return dashScopeToolServiceProvider.getIfAvailable();
    }

    private ZhipuaiToolService zhipuaiToolService() {
        return zhipuaiToolServiceProvider.getIfAvailable();
    }

    // ────────────────────────── public entry points ──────────────────────────

    /**
     * Synchronously dispatches a tool call to the appropriate provider service.
     *
     * @param callback optional callback for model-based intent recognition;
     *                 pass null to skip model-based recognition entirely
     * @return the tool reply text, or null if the request should not use
     *         provider-level tool routing
     */
    public String tryDispatch(RobotProtobuf robot, String message, Map<String, Object> runtimeContext,
            IntentRecognitionHelper.StructuredSyncCallback callback) {
        String userMessage = extractLikelyUserMessage(message);
        if (!shouldUseProviderToolService(robot, userMessage, callback)) {
            return null;
        }

        log.info("Use provider tool service, provider={}, toolChoice={}, message={}",
                robot.getLlm().getTextProvider(), robot.getLlm().getToolChoice(), userMessage);

        String provider = resolveToolExecutionProvider(robot);
        String model = resolveToolExecutionModel(robot);
        String runtimeToolName = resolveProviderRuntimeToolName(robot, userMessage, callback);

        // ToolEntity 治理校验：禁用的工具、不满足 allowedMethods 的工具不走 provider-native 链路
        if (!isProviderToolCallAllowed(robot, runtimeToolName)) {
            log.debug("Skip provider tool service: tool not allowed by registry governance, runtimeToolName={}, message={}",
                    runtimeToolName, userMessage);
            return null;
        }

        ResolvedRobotToolIntent resolvedToolIntent = findResolvedToolIntent(robot, runtimeToolName);
        Map<String, Object> toolContext = buildProviderToolCallContext(robot, provider, model, runtimeContext);
        enrichProviderToolCallContext(toolContext, resolvedToolIntent, userMessage);
        ToolCallEntity toolCall = createProviderToolCallRecord(runtimeToolName, userMessage, toolContext);
        long startedAtNanos = System.nanoTime();

        try {
            DashScopeToolService dashScopeToolService = dashScopeToolService();
            ZhipuaiToolService zhipuaiToolService = zhipuaiToolService();
            if (LlmProviderConstants.DASHSCOPE.equalsIgnoreCase(provider) && dashScopeToolService != null) {
                DashScopeToolCallingResult result = dashScopeToolService.chat(userMessage, model);
                String reply = result != null ? result.getFinalReply() : null;
                completeProviderToolCallRecord(toolCall, reply, startedAtNanos);
                return reply;
            }
            if (LlmProviderConstants.ZHIPUAI.equalsIgnoreCase(provider) && zhipuaiToolService != null) {
                String reply = isWebSearchTool(resolvedToolIntent, runtimeToolName)
                    ? zhipuaiToolService.chatWithWebSearch(userMessage,
                        resolveWebSearchQuery(resolvedToolIntent, userMessage))
                    : zhipuaiToolService.chat(userMessage, model);
                completeProviderToolCallRecord(toolCall, reply, startedAtNanos);
                return reply;
            }
        } catch (Exception e) {
            failProviderToolCallRecord(toolCall, e, startedAtNanos);
            log.warn("Provider tool service failed for provider {}: {}", provider, e.getMessage());
        }
        return null;
    }

    // ──────────────────── provider tool guard ────────────────────────────────

    private boolean shouldUseProviderToolService(RobotProtobuf robot, String message,
            IntentRecognitionHelper.StructuredSyncCallback callback) {
        if (robot == null || robot.getLlm() == null || !Boolean.TRUE.equals(robot.getLlm().getEnabled())) {
            log.debug("Skip provider tool service: llm disabled or missing, message={}", message);
            return false;
        }

        String provider = resolveToolExecutionProvider(robot);
        String toolChoice = robot.getLlm().getToolChoice();
        List<String> tools = resolveEnabledRuntimeTools(robot);

        if (tools == null || tools.isEmpty()) {
            log.debug("Skip provider tool service: no enabled tools configured, provider={}, toolChoice={}, message={}",
                    provider, toolChoice, message);
            return false;
        }

        if (LlmProviderConstants.DASHSCOPE.equalsIgnoreCase(provider)) {
            DashScopeToolService dashScopeToolService = dashScopeToolService();
            if (dashScopeToolService == null || !dashScopeToolService.isSupported()) {
                log.debug("Skip provider tool service: DashScopeToolService unavailable, toolChoice={}, tools={}, message={}",
                        toolChoice, tools, message);
                return false;
            }
            return shouldUseProviderToolServiceInternal(robot, message, callback);
        }
        if (LlmProviderConstants.ZHIPUAI.equalsIgnoreCase(provider)) {
            ZhipuaiToolService zhipuaiToolService = zhipuaiToolService();
            if (zhipuaiToolService == null || !zhipuaiToolService.isSupported()) {
                log.debug("Skip provider tool service: ZhipuaiToolService unavailable, toolChoice={}, tools={}, message={}",
                        toolChoice, tools, message);
                return false;
            }
            return shouldUseProviderToolServiceInternal(robot, message, callback);
        }

        log.debug("Skip provider tool service: provider {} does not use native tool routing, toolChoice={}, tools={}, message={}",
                provider, toolChoice, tools, message);
        return false;
    }

    private boolean shouldUseProviderToolServiceInternal(RobotProtobuf robot, String message,
            IntentRecognitionHelper.StructuredSyncCallback callback) {
        String provider = resolveToolExecutionProvider(robot);
        if (!StringUtils.hasText(provider)
                || !(LlmProviderConstants.DASHSCOPE.equalsIgnoreCase(provider)
                        || LlmProviderConstants.ZHIPUAI.equalsIgnoreCase(provider))) {
            return false;
        }

        String toolChoice = robot.getLlm().getToolChoice();
        if ("none".equalsIgnoreCase(toolChoice)) {
            log.debug("Skip provider tool service: toolChoice=none, provider={}, tools={}, message={}",
                    provider, resolveEnabledRuntimeTools(robot), message);
            return false;
        }
        if ("required".equalsIgnoreCase(toolChoice)) {
            return true;
        }

        boolean likelyToolIntent = intentRecognitionHelper.isLikelyToolIntent(robot, message, callback);
        if (!likelyToolIntent) {
            log.debug("Skip provider tool service: toolChoice=auto but message did not match tool intent, provider={}, tools={}, message={}",
                    provider, resolveEnabledRuntimeTools(robot), message);
        }
        return likelyToolIntent;
    }

    // ──────────────────── user message extraction ───────────────────────────

    /**
     * Extracts the actual user question text from a message that may be wrapped
     * in a JSON structure or prefixed with a question label.
     */
    public String extractLikelyUserMessage(String message) {
        if (!StringUtils.hasText(message)) {
            return null;
        }

        String trimmed = message.trim();
        try {
            JSONObject jsonObject = JSON.parseObject(trimmed);
            if (jsonObject != null) {
                String text = jsonObject.getString("text");
                if (StringUtils.hasText(text)) {
                    return text.trim();
                }
            }
        } catch (Exception ignore) {
        }

        int questionIndex = trimmed.lastIndexOf(I18Consts.I18N_QUESTION_LABEL);
        if (questionIndex >= 0) {
            String question = trimmed.substring(questionIndex + I18Consts.I18N_QUESTION_LABEL.length()).trim();
            if (StringUtils.hasText(question)) {
                return question;
            }
        }

        return trimmed;
    }

    // ──────────────────── tool name resolution ──────────────────────────────

    /**
     * Resolves which specific tool to invoke.
     *
     * @param callback optional callback for model-based intent recognition
     */
    public String resolveProviderRuntimeToolName(RobotProtobuf robot, String message,
            IntentRecognitionHelper.StructuredSyncCallback callback) {
        List<String> tools = resolveEnabledRuntimeTools(robot);
        if (tools.isEmpty()) {
            return null;
        }

        RobotToolIntentContext intentContext = intentRecognitionHelper.resolveToolIntentContext(robot);
        String matchedByIntent = intentRecognitionHelper.resolveRuntimeToolNameFromIntent(
                robot, intentContext, message, callback);
        if (StringUtils.hasText(matchedByIntent) && tools.stream().anyMatch(tool -> tool.equalsIgnoreCase(matchedByIntent.trim()))) {
            return matchedByIntent;
        }

        if (tools.size() == 1 && StringUtils.hasText(tools.get(0))) {
            return tools.get(0).trim();
        }

        String normalized = StringUtils.hasText(message) ? message.toLowerCase(Locale.ROOT).trim() : null;
        if (StringUtils.hasText(normalized)) {
            if (containsAnyTool(tools, "getCurrentDateTime", "getCurrentDateTimeMethodToolCallback")
                    && containsAnyKeyword(normalized,
                            "现在时间", "当前时间", "几点", "几号", "日期", "今天", "明天", "后天",
                            "time", "date", "day", "today", "tomorrow", "current time", "current date", "what time")) {
                return firstMatchingTool(tools, "getCurrentDateTime", "getCurrentDateTimeMethodToolCallback");
            }
            if (containsAnyTool(tools, "setAlarm")
                    && containsAnyKeyword(normalized, "闹钟", "提醒", "定时", "alarm", "remind", "reminder", "wake me")) {
                return firstMatchingTool(tools, "setAlarm");
            }
            if (containsAnyTool(tools, "currentWeather")
                    && containsAnyKeyword(normalized,
                            "天气", "温度", "下雨", "下雪", "晴", "阴", "多云", "weather", "forecast", "temperature", "hot", "cold")) {
                return firstMatchingTool(tools, "currentWeather");
            }
            if (containsAnyTool(tools, "add", "subtract", "multiply", "divide")
                    && (MATH_EXPRESSION_PATTERN.matcher(normalized).find()
                            || containsAnyKeyword(normalized,
                                    "计算", "算一下", "加", "减", "乘", "除", "等于",
                                    "plus", "minus", "multiply", "multiplied", "times", "divide", "divided", "calculate", "sum"))) {
                return firstMatchingTool(tools, "add", "subtract", "multiply", "divide");
            }
        }

        return tools.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .findFirst()
                .orElse(null);
    }

    private List<String> resolveEnabledRuntimeTools(RobotProtobuf robot) {
        if (robot == null || robot.getLlm() == null || robot.getLlm().getTools() == null) {
            return List.of();
        }

        String orgUid = robot.getOrgUid();
        return robot.getLlm().getTools().stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .filter(toolName -> toolRestService.isRuntimeToolEnabled(toolName, orgUid))
                .distinct()
                .toList();
    }

    /**
     * ToolEntity 治理校验：provider-native 工具调用前，检查目标工具是否被注册表允许调用。
     *
     * <p>校验规则（与 {@link ToolRestService#isRuntimeToolEnabled} 互补）：
     * <ol>
     *   <li>解析 {@link ToolEntity}；未注册的工具默认放行（向后兼容，保持原有行为）。</li>
     *   <li>{@code enabled=false} 的工具拒绝调用。</li>
     *   <li>配置了 {@code allowedMethods} 且当前 runtimeToolName 不在白名单内的，拒绝调用。</li>
     * </ol>
     *
     * <p>注意：{@code mcpExposureMode} 仅约束 MCP 对外暴露，不影响内部 provider-native 链路，
     * 因此本方法不检查 {@code mcpExposureMode}。
     *
     * @param robot           机器人（用于获取 orgUid）
     * @param runtimeToolName 运行时工具名（可能为 null，此时直接放行）
     * @return true 表示允许调用，false 表示被治理策略拒绝
     */
    private boolean isProviderToolCallAllowed(RobotProtobuf robot, String runtimeToolName) {
        if (!StringUtils.hasText(runtimeToolName)) {
            return true;
        }

        String orgUid = robot != null ? robot.getOrgUid() : null;
        java.util.Optional<ToolEntity> toolEntityOpt = toolRestService.resolveRuntimeTool(runtimeToolName, orgUid);
        if (toolEntityOpt.isEmpty()) {
            // 未注册到 ToolEntity 表的工具保持原有行为（向后兼容）
            return true;
        }

        ToolEntity toolEntity = toolEntityOpt.get();

        // enabled=false 直接拒绝
        if (Boolean.FALSE.equals(toolEntity.getEnabled())) {
            log.info("Provider tool call blocked: tool disabled by registry, tool={}, orgUid={}",
                    runtimeToolName, orgUid);
            return false;
        }

        // allowedMethods 白名单校验（委托 ToolRestService 已有的 isToolExposedToMcp 中的逻辑不适用，
        // 因为 mcpExposureMode 仅约束 MCP；这里单独检查 allowedMethods）
        if (!isAllowedMethodForProvider(toolEntity, runtimeToolName)) {
            log.info("Provider tool call blocked: method not in allowedMethods, tool={}, allowedMethods={}, orgUid={}",
                    runtimeToolName, toolEntity.getAllowedMethods(), orgUid);
            return false;
        }

        return true;
    }

    /**
     * 检查 runtimeToolName 是否在 ToolEntity.allowedMethods 白名单内。
     * allowedMethods 为空时视为不限制（向后兼容）。
     */
    private boolean isAllowedMethodForProvider(ToolEntity toolEntity, String runtimeToolName) {
        String allowedMethods = toolEntity.getAllowedMethods();
        if (!StringUtils.hasText(allowedMethods)) {
            return true;
        }

        java.util.Set<String> allowed = java.util.Arrays.stream(allowedMethods.split("[,\n\r]+"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());
        if (allowed.isEmpty()) {
            return true;
        }

        String candidate = runtimeToolName.trim().toLowerCase();
        return allowed.contains(candidate)
                || (StringUtils.hasText(toolEntity.getMethodName()) && allowed.contains(toolEntity.getMethodName().trim().toLowerCase()))
                || (StringUtils.hasText(toolEntity.getName()) && allowed.contains(toolEntity.getName().trim().toLowerCase()))
                || (StringUtils.hasText(toolEntity.getKey()) && allowed.contains(toolEntity.getKey().trim().toLowerCase()));
    }

    private ResolvedRobotToolIntent findResolvedToolIntent(RobotProtobuf robot, String runtimeToolName) {
        if (!StringUtils.hasText(runtimeToolName)) {
            return null;
        }

        RobotToolIntentContext intentContext = intentRecognitionHelper.resolveToolIntentContext(robot);
        if (intentContext == null || intentContext.tools() == null || intentContext.tools().isEmpty()) {
            return null;
        }

        return intentContext.tools().stream()
                .filter(tool -> tool != null)
                .filter(tool -> equalsIgnoreCase(tool.toolName(), runtimeToolName)
                        || equalsIgnoreCase(tool.toolKey(), runtimeToolName))
                .findFirst()
                .orElse(null);
    }

    private void enrichProviderToolCallContext(Map<String, Object> context, ResolvedRobotToolIntent resolvedToolIntent,
            String userMessage) {
        if (context == null || resolvedToolIntent == null) {
            return;
        }

        if (StringUtils.hasText(resolvedToolIntent.bindingType())) {
            context.put("bindingType", resolvedToolIntent.bindingType().trim());
        }

        Map<String, Object> metadata = resolvedToolIntent.metadata();
        if (metadata == null || metadata.isEmpty()) {
            return;
        }

        Object mcpServerUid = metadata.get("mcpServerUid");
        if (mcpServerUid != null && StringUtils.hasText(String.valueOf(mcpServerUid))) {
            context.put("mcpServerUid", String.valueOf(mcpServerUid).trim());
        }

        Object searchResultLimit = metadata.get("searchResultLimit");
        if (searchResultLimit != null) {
            context.put("searchResultLimit", searchResultLimit);
        }

        String searchQuery = resolveWebSearchQuery(resolvedToolIntent, userMessage);
        if (StringUtils.hasText(searchQuery)) {
            context.put("searchQuery", searchQuery);
        }
    }

    // ──────────────────── tool call record management ───────────────────────

    private Map<String, Object> buildProviderToolCallContext(RobotProtobuf robot, String provider, String model,
            Map<String, Object> runtimeContext) {
        Map<String, Object> context = new LinkedHashMap<>();
        if (StringUtils.hasText(provider)) {
            context.put("provider", provider);
        }
        if (StringUtils.hasText(model)) {
            context.put("model", model);
        }
        if (robot != null) {
            if (StringUtils.hasText(robot.getUid())) {
                context.put("robotUid", robot.getUid());
            }
            if (StringUtils.hasText(robot.getOrgUid())) {
                context.put("orgUid", robot.getOrgUid());
            }
        }

        if (runtimeContext != null && !runtimeContext.isEmpty()) {
            context.putAll(runtimeContext);
        }
        return context;
    }

    public ToolCallEntity createProviderToolCallRecord(String runtimeToolName, String requestPayload,
            Map<String, Object> context) {
        if (toolCallRestService == null || !StringUtils.hasText(runtimeToolName)) {
            return null;
        }

        try {
            ToolEntity toolEntity = toolCallRestService.resolveRuntimeToolEntity(runtimeToolName,
                    stringValue(context, "orgUid"));
            return toolCallRestService.createRuntimeRecord(toolEntity, runtimeToolName, requestPayload, context);
        } catch (Exception e) {
            log.warn("Create provider tool call record failed, runtimeToolName={}, error={}", runtimeToolName,
                    e.getMessage());
            return null;
        }
    }

    public void completeProviderToolCallRecord(ToolCallEntity toolCall, String responsePayload, long startedAtNanos) {
        if (toolCallRestService == null || toolCall == null || !StringUtils.hasText(toolCall.getUid())) {
            return;
        }

        try {
            toolCallRestService.completeRuntimeRecord(toolCall.getUid(), responsePayload,
                    elapsedMillis(startedAtNanos));
        } catch (Exception e) {
            log.warn("Complete provider tool call record failed, uid={}, error={}", toolCall.getUid(), e.getMessage());
        }
    }

    public void failProviderToolCallRecord(ToolCallEntity toolCall, Exception exception, long startedAtNanos) {
        if (toolCallRestService == null || toolCall == null || !StringUtils.hasText(toolCall.getUid())) {
            return;
        }

        try {
            toolCallRestService.failRuntimeRecord(toolCall.getUid(),
                    exception != null ? exception.getMessage() : null, elapsedMillis(startedAtNanos));
        } catch (Exception e) {
            log.warn("Fail provider tool call record failed, uid={}, error={}", toolCall.getUid(), e.getMessage());
        }
    }

    // ──────────────────── provider/model resolution ─────────────────────────

    public String resolveToolExecutionProvider(RobotProtobuf robot) {
        RobotToolIntentContext toolContext = robot != null && robot.getLlm() != null
                ? robot.getLlm().getToolIntentContext()
                : null;
        if (toolContext != null && StringUtils.hasText(toolContext.toolProvider())) {
            return toolContext.toolProvider().trim();
        }
        return robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextProvider())
                ? robot.getLlm().getTextProvider().trim()
                : null;
    }

    public String resolveToolExecutionModel(RobotProtobuf robot) {
        RobotToolIntentContext toolContext = robot != null && robot.getLlm() != null
                ? robot.getLlm().getToolIntentContext()
                : null;
        if (toolContext != null && StringUtils.hasText(toolContext.toolModel())) {
            return toolContext.toolModel().trim();
        }
        return robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextModel())
                ? robot.getLlm().getTextModel().trim()
                : null;
    }

    private boolean isWebSearchTool(ResolvedRobotToolIntent resolvedToolIntent, String runtimeToolName) {
        if (resolvedToolIntent != null && StringUtils.hasText(resolvedToolIntent.bindingType())
                && "WEB_SEARCH".equalsIgnoreCase(resolvedToolIntent.bindingType().trim())) {
            return true;
        }
        return equalsIgnoreCase(runtimeToolName, "builtin.web_search")
                || equalsIgnoreCase(runtimeToolName, "web_search");
    }

    private String resolveWebSearchQuery(ResolvedRobotToolIntent resolvedToolIntent, String userMessage) {
        String template = metadataStringValue(resolvedToolIntent != null ? resolvedToolIntent.metadata() : null,
                "searchQueryTemplate");
        if (!StringUtils.hasText(template)) {
            return userMessage;
        }

        String normalizedMessage = StringUtils.hasText(userMessage) ? userMessage.trim() : "";
        String resolved = template
                .replace("{{query}}", normalizedMessage)
                .replace("{query}", normalizedMessage)
                .replace("{{message}}", normalizedMessage)
                .replace("{message}", normalizedMessage);
        return StringUtils.hasText(resolved) ? resolved.trim() : normalizedMessage;
    }

    // ──────────────────── utility ───────────────────────────────────────────

    static long elapsedMillis(long startedAtNanos) {
        long elapsedNanos = System.nanoTime() - startedAtNanos;
        return elapsedNanos > 0 ? elapsedNanos / 1_000_000L : 0L;
    }

    private static String stringValue(Map<String, Object> context, String key) {
        if (context == null || !context.containsKey(key) || context.get(key) == null) {
            return null;
        }
        return String.valueOf(context.get(key));
    }

    private static String metadataStringValue(Map<String, Object> metadata, String key) {
        if (metadata == null || !metadata.containsKey(key) || metadata.get(key) == null) {
            return null;
        }
        return String.valueOf(metadata.get(key));
    }

    private static boolean equalsIgnoreCase(String left, String right) {
        return StringUtils.hasText(left) && StringUtils.hasText(right) && left.trim().equalsIgnoreCase(right.trim());
    }

    static String firstMatchingTool(List<String> tools, String... expectedTools) {
        if (tools == null || tools.isEmpty()) {
            return null;
        }
        for (String tool : tools) {
            if (!StringUtils.hasText(tool)) {
                continue;
            }
            for (String expectedTool : expectedTools) {
                if (tool.trim().equalsIgnoreCase(expectedTool)) {
                    return tool.trim();
                }
            }
        }
        return null;
    }

    static boolean containsAnyTool(List<String> tools, String... expectedTools) {
        return firstMatchingTool(tools, expectedTools) != null;
    }

    static boolean containsAnyKeyword(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

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
