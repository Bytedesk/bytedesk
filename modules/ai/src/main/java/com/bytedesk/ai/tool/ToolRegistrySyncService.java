package com.bytedesk.ai.tool;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.tool.utils.McpExposureModeEnum;
import com.bytedesk.core.enums.LevelEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolRegistrySyncService {

    private final ApplicationContext applicationContext;

    private final ToolRestService toolRestService;

    public int syncPlatformTools(String orgUid) {
        Set<String> activeKeys = new LinkedHashSet<>();
        syncAnnotatedTools(orgUid, activeKeys);
        syncToolCallbackBeans(orgUid, activeKeys);
        toolRestService.disableStalePlatformSystemTools(orgUid, activeKeys);
        log.info("Tool registry sync complete, active platform tools={}", activeKeys.size());
        return activeKeys.size();
    }

    private void syncAnnotatedTools(String orgUid, Set<String> activeKeys) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean;
            try {
                bean = applicationContext.getBean(beanName);
            } catch (Exception ex) {
                log.debug("Skip tool scan bean {}: {}", beanName, ex.getMessage());
                continue;
            }

            Class<?> targetClass = AopUtils.getTargetClass(bean);
            if (targetClass == null) {
                continue;
            }

            for (Method method : targetClass.getDeclaredMethods()) {
                Tool tool = method.getAnnotation(Tool.class);
                if (tool != null) {
                    ToolRequest request = ToolRequest.builder()
                            .key(beanName + "." + method.getName())
                            .name(resolveToolName(tool.name(), method.getName()))
                            .description(tool.description())
                            .type(ToolTypeEnum.BUILTIN.name())
                            .bindingType("CLASS")
                            .beanName(beanName)
                            .className(targetClass.getName())
                            .methodName(method.getName())
                            .level(LevelEnum.PLATFORM.name())
                            .orgUid(orgUid)
                            .enabled(true)
                            .mcpExposureMode(defaultMcpExposureMode(method.getName()))
                            .metadata(buildMetadata("TOOL", targetClass, method, beanName))
                            .build();
                    toolRestService.syncSystemTool(request);
                    activeKeys.add(request.getKey());
                }

                McpTool mcpTool = method.getAnnotation(McpTool.class);
                if (mcpTool != null) {
                    ToolRequest request = ToolRequest.builder()
                            .key(beanName + "." + method.getName())
                            .name(method.getName())
                            .description(mcpTool.description())
                            .type(ToolTypeEnum.BUILTIN.name())
                            .bindingType("MCP_TOOL")
                            .beanName(beanName)
                            .className(targetClass.getName())
                            .methodName(method.getName())
                            .level(LevelEnum.PLATFORM.name())
                            .orgUid(orgUid)
                            .enabled(true)
                            .mcpExposureMode(McpExposureModeEnum.DUAL.name())
                            .metadata(buildMetadata("MCP_TOOL", targetClass, method, beanName))
                            .build();
                    toolRestService.syncSystemTool(request);
                    activeKeys.add(request.getKey());
                }
            }
        }
    }

    private void syncToolCallbackBeans(String orgUid, Set<String> activeKeys) {
        Map<String, ToolCallback> toolCallbacks = applicationContext.getBeansOfType(ToolCallback.class);
        for (Map.Entry<String, ToolCallback> entry : toolCallbacks.entrySet()) {
            String beanName = entry.getKey();
            ToolCallback toolCallback = entry.getValue();
            if (toolCallback == null || toolCallback.getToolDefinition() == null) {
                continue;
            }

            String key = beanName;
            String name = StringUtils.hasText(toolCallback.getToolDefinition().name())
                    ? toolCallback.getToolDefinition().name()
                    : beanName;
            ToolRequest request = ToolRequest.builder()
                    .key(key)
                    .name(name)
                    .description(toolCallback.getToolDefinition().description())
                    .type(ToolTypeEnum.BUILTIN.name())
                    .bindingType("FUNCTION_BEAN")
                    .beanName(beanName)
                    .className(AopUtils.getTargetClass(toolCallback) != null ? AopUtils.getTargetClass(toolCallback).getName() : toolCallback.getClass().getName())
                    .inputSchema(toolCallback.getToolDefinition().inputSchema())
                    .level(LevelEnum.PLATFORM.name())
                    .orgUid(orgUid)
                    .enabled(true)
                    .mcpExposureMode(defaultMcpExposureMode(name))
                    .metadata(buildToolCallbackMetadata(beanName, toolCallback))
                    .build();
            toolRestService.syncSystemTool(request);
            activeKeys.add(key);
        }
    }

    private String resolveToolName(String annotationName, String methodName) {
        return StringUtils.hasText(annotationName) ? annotationName : methodName;
    }

    private String defaultMcpExposureMode(String runtimeToolName) {
        if (!StringUtils.hasText(runtimeToolName)) {
            return McpExposureModeEnum.NONE.name();
        }
        String normalizedToolName = runtimeToolName.trim();
        return normalizedToolName.matches("(?i).*(Query|Search|Find|Get|List|Count).*")
                ? McpExposureModeEnum.READONLY.name()
                : McpExposureModeEnum.NONE.name();
    }

    private String buildMetadata(String sourceType, Class<?> targetClass, Method method, String beanName) {
        JSONObject metadata = new JSONObject();
        metadata.put("registrySource", "CODE_SYNC");
        metadata.put("sourceType", sourceType);
        metadata.put("sourceAnnotation", sourceType);
        metadata.put("sourceBean", beanName);
        metadata.put("sourceClass", targetClass.getName());
        metadata.put("sourceMethod", method.getName());
        metadata.put("sourceSignature", targetClass.getName() + "#" + method.getName());
        metadata.put("stale", false);
        return metadata.toJSONString();
    }

    private String buildToolCallbackMetadata(String beanName, ToolCallback toolCallback) {
        JSONObject metadata = new JSONObject();
        metadata.put("registrySource", "CODE_SYNC");
        metadata.put("sourceType", "FUNCTION_BEAN");
        metadata.put("sourceAnnotation", "TOOL_CALLBACK");
        metadata.put("sourceBean", beanName);
        metadata.put("sourceClass", toolCallback.getClass().getName());
        metadata.put("sourceSignature", beanName);
        metadata.put("stale", false);
        return metadata.toJSONString();
    }
}