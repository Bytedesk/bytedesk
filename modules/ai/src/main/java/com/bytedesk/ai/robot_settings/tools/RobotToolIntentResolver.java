package com.bytedesk.ai.robot_settings.tools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.tool.ToolEntity;
import com.bytedesk.ai.tool.ToolRepository;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;

@Component
public class RobotToolIntentResolver {

    private final ToolRepository toolRepository;
    private final LlmProviderRestService llmProviderRestService;

    public RobotToolIntentResolver(ToolRepository toolRepository, LlmProviderRestService llmProviderRestService) {
        this.toolRepository = toolRepository;
        this.llmProviderRestService = llmProviderRestService;
    }

    public RobotToolIntentContext resolve(String orgUid, RobotToolsSettingsEntity toolsSettings) {
        if (toolsSettings == null || Boolean.FALSE.equals(toolsSettings.getEnabled())) {
            return RobotToolIntentContext.empty();
        }

        List<ResolvedRobotToolIntent> resolvedTools = new ArrayList<>();
        for (RobotToolConfig toolConfig : safeToolConfigs(toolsSettings)) {
            if (toolConfig == null || Boolean.FALSE.equals(toolConfig.getEnabled())) {
                continue;
            }

            ToolEntity toolEntity = resolveToolEntity(orgUid, toolConfig);
            for (String toolName : resolveToolNames(toolConfig)) {
                if (!StringUtils.hasText(toolName)) {
                    continue;
                }
                resolvedTools.add(ResolvedRobotToolIntent.builder()
                        .toolName(toolName.trim())
                        .toolKey(firstNonBlank(toolConfig.getKey(), toolEntity != null ? toolEntity.getKey() : null))
                        .description(firstNonBlank(toolConfig.getDescription(),
                                toolEntity != null ? toolEntity.getDescription() : null))
                        .bindingType(firstNonBlank(toolConfig.getBindingType(),
                                toolEntity != null ? toolEntity.getBindingType() : null))
                        .intentKeywords(resolveIntentKeywords(toolConfig, toolEntity))
                        .intentMatchMode(resolveIntentMatchMode(toolConfig, toolEntity))
                        .orderIndex(toolConfig.getOrderIndex() != null
                                ? toolConfig.getOrderIndex()
                                : (toolEntity != null ? toolEntity.getOrderIndex() : 0))
                        .metadata(resolveMetadata(toolConfig, toolEntity))
                        .build());
            }
        }

        return new RobotToolIntentContext(
                Boolean.TRUE.equals(toolsSettings.getIntentRecognitionEnabled()),
                resolveProviderType(toolsSettings.getIntentProviderUid()),
                sanitizeOptionalText(toolsSettings.getIntentModel()),
                resolveProviderType(toolsSettings.getToolProviderUid()),
                sanitizeOptionalText(toolsSettings.getToolModel()),
                toolsSettings.getIntentTimeoutMs(),
                resolvedTools);
    }

    private String resolveProviderType(String configuredProviderUid) {
        String normalized = sanitizeOptionalText(configuredProviderUid);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }

        LlmProviderEntity provider = llmProviderRestService.findByUid(normalized).orElse(null);
        if (provider != null && StringUtils.hasText(provider.getType())) {
            return provider.getType().trim();
        }

        // Backward compatibility: older rows stored provider type in the same column.
        return looksLikeNumericUid(normalized) ? null : normalized;
    }

    private boolean looksLikeNumericUid(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private String sanitizeOptionalText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private List<RobotToolConfig> safeToolConfigs(RobotToolsSettingsEntity toolsSettings) {
        return toolsSettings.getToolConfigs() != null ? toolsSettings.getToolConfigs() : List.of();
    }

    private ToolEntity resolveToolEntity(String orgUid, RobotToolConfig toolConfig) {
        if (toolConfig == null) {
            return null;
        }

        ToolEntity orgTool = findMatchingTool(orgUid, toolConfig);
        if (orgTool != null) {
            return orgTool;
        }

        ToolEntity platformTool = findMatchingTool(BytedeskConsts.DEFAULT_ORGANIZATION_UID, toolConfig);
        if (platformTool != null && LevelEnum.PLATFORM.name().equalsIgnoreCase(platformTool.getLevel())) {
            return platformTool;
        }

        return toolRepository.findAll().stream()
                .filter(tool -> tool != null && !tool.isDeleted())
                .filter(tool -> matchesFallback(tool, toolConfig))
                .findFirst()
                .orElse(null);
    }

    private ToolEntity findMatchingTool(String orgUid, RobotToolConfig toolConfig) {
        if (!StringUtils.hasText(orgUid) || toolConfig == null) {
            return null;
        }

        if (StringUtils.hasText(toolConfig.getKey()) && StringUtils.hasText(orgUid)) {
            ToolEntity tool = toolRepository.findByKeyAndOrgUidAndDeletedFalse(toolConfig.getKey().trim(), orgUid)
                    .orElse(null);
            if (tool != null) {
                return tool;
            }
        }

        if (StringUtils.hasText(toolConfig.getBeanName()) && StringUtils.hasText(orgUid)) {
            ToolEntity tool = toolRepository
                    .findByBeanNameAndOrgUidAndDeletedFalse(toolConfig.getBeanName().trim(), orgUid)
                    .orElse(null);
            if (tool != null) {
                return tool;
            }
        }

        if (StringUtils.hasText(toolConfig.getClassName())
                && StringUtils.hasText(toolConfig.getMethodName())
                && StringUtils.hasText(orgUid)) {
            ToolEntity tool = toolRepository.findByClassNameAndMethodNameAndOrgUidAndDeletedFalse(
                    toolConfig.getClassName().trim(), toolConfig.getMethodName().trim(), orgUid).orElse(null);
            if (tool != null) {
                return tool;
            }
        }

            return toolRepository.findAll().stream()
                .filter(tool -> tool != null && !tool.isDeleted())
                .filter(tool -> !StringUtils.hasText(tool.getOrgUid()) || orgUid.equals(tool.getOrgUid()))
                .filter(tool -> matchesFallback(tool, toolConfig))
                .findFirst()
                .orElse(null);
    }

    private boolean matchesFallback(ToolEntity tool, RobotToolConfig toolConfig) {
        return equalsIgnoreCase(tool.getKey(), toolConfig.getKey())
                || equalsIgnoreCase(tool.getName(), toolConfig.getName())
                || equalsIgnoreCase(tool.getBeanName(), toolConfig.getBeanName())
                || (StringUtils.hasText(toolConfig.getClassName())
                        && StringUtils.hasText(toolConfig.getMethodName())
                        && equalsIgnoreCase(tool.getClassName(), toolConfig.getClassName())
                        && equalsIgnoreCase(tool.getMethodName(), toolConfig.getMethodName()));
    }

    private List<String> resolveToolNames(RobotToolConfig toolConfig) {
        if (toolConfig == null || Boolean.FALSE.equals(toolConfig.getEnabled())) {
            return List.of();
        }

        String bindingType = toolConfig.getBindingType();
        if (!StringUtils.hasText(bindingType)) {
            return fallbackToolName(toolConfig);
        }

        return switch (bindingType.trim().toUpperCase()) {
            case "CLASS" -> resolveClassToolNames(toolConfig);
            case "SPRING_BEAN", "FUNCTION_BEAN" -> valueAsList(toolConfig.getBeanName(), toolConfig);
            case "MCP_TOOL" -> valueAsList(
                    StringUtils.hasText(toolConfig.getMethodName()) ? toolConfig.getMethodName() : toolConfig.getBeanName(),
                    toolConfig);
            case "WEB_SEARCH" -> fallbackToolName(toolConfig);
            default -> fallbackToolName(toolConfig);
        };
    }

    private List<String> resolveClassToolNames(RobotToolConfig toolConfig) {
        if (StringUtils.hasText(toolConfig.getMethodName())) {
            return List.of(toolConfig.getMethodName().trim());
        }
        return fallbackToolName(toolConfig);
    }

    private List<String> fallbackToolName(RobotToolConfig toolConfig) {
        if (StringUtils.hasText(toolConfig.getKey())) {
            return List.of(toolConfig.getKey().trim());
        }
        if (StringUtils.hasText(toolConfig.getName())) {
            return List.of(toolConfig.getName().trim());
        }
        return List.of();
    }

    private List<String> valueAsList(String value, RobotToolConfig toolConfig) {
        if (StringUtils.hasText(value)) {
            return List.of(value.trim());
        }
        return fallbackToolName(toolConfig);
    }

    private List<String> resolveIntentKeywords(RobotToolConfig toolConfig, ToolEntity toolEntity) {
        if (toolConfig != null && toolConfig.getIntentKeywords() != null && !toolConfig.getIntentKeywords().isEmpty()) {
            return sanitizeKeywords(toolConfig.getIntentKeywords());
        }
        if (toolEntity != null && StringUtils.hasText(toolEntity.getIntentKeywords())) {
            return parseKeywordJson(toolEntity.getIntentKeywords());
        }
        return List.of();
    }

    private String resolveIntentMatchMode(RobotToolConfig toolConfig, ToolEntity toolEntity) {
        if (toolConfig != null && StringUtils.hasText(toolConfig.getIntentMatchMode())) {
            return ToolIntentMatchMode.normalize(toolConfig.getIntentMatchMode());
        }
        if (toolEntity != null && StringUtils.hasText(toolEntity.getIntentMatchMode())) {
            return ToolIntentMatchMode.normalize(toolEntity.getIntentMatchMode());
        }
        return ToolIntentMatchMode.KEYWORD.name();
    }

    private Map<String, Object> resolveMetadata(RobotToolConfig toolConfig, ToolEntity toolEntity) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (toolEntity != null && StringUtils.hasText(toolEntity.getMetadata())) {
            merged.putAll(parseMetadata(toolEntity.getMetadata()));
        }
        if (toolConfig != null && toolConfig.getMetadata() != null && !toolConfig.getMetadata().isEmpty()) {
            merged.putAll(toolConfig.getMetadata());
        }
        if (toolConfig != null) {
            if (StringUtils.hasText(toolConfig.getMcpServerUid())) {
                merged.put("mcpServerUid", toolConfig.getMcpServerUid().trim());
            }
            if (StringUtils.hasText(toolConfig.getSearchQueryTemplate())) {
                merged.put("searchQueryTemplate", toolConfig.getSearchQueryTemplate().trim());
            }
            if (toolConfig.getSearchResultLimit() != null) {
                merged.put("searchResultLimit", toolConfig.getSearchResultLimit());
            }
        }
        return merged;
    }

    private List<String> parseKeywordJson(String rawKeywords) {
        if (!StringUtils.hasText(rawKeywords)) {
            return List.of();
        }
        try {
            JSONArray array = JSON.parseArray(rawKeywords);
            if (array == null || array.isEmpty()) {
                return List.of();
            }
            List<String> keywords = new ArrayList<>();
            for (Object item : array) {
                if (item != null) {
                    keywords.add(String.valueOf(item));
                }
            }
            return sanitizeKeywords(keywords);
        } catch (Exception ignore) {
            return sanitizeKeywords(List.of(rawKeywords));
        }
    }

    private Map<String, Object> parseMetadata(String rawMetadata) {
        if (!StringUtils.hasText(rawMetadata)) {
            return Map.of();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(rawMetadata);
            return jsonObject == null ? Map.of() : new LinkedHashMap<>(jsonObject);
        } catch (Exception ignore) {
            return Map.of();
        }
    }

    private List<String> sanitizeKeywords(List<String> keywords) {
        return keywords.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return StringUtils.hasText(left) && StringUtils.hasText(right) && left.trim().equalsIgnoreCase(right.trim());
    }

    private String firstNonBlank(String first, String second) {
        if (StringUtils.hasText(first)) {
            return first.trim();
        }
        if (StringUtils.hasText(second)) {
            return second.trim();
        }
        return null;
    }
}