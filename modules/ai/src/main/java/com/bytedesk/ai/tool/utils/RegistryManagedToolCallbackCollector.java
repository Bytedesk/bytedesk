package com.bytedesk.ai.tool.utils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.tool.ToolEntity;
import com.bytedesk.ai.tool.ToolRestService;
import com.bytedesk.core.constant.BytedeskConsts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistryManagedToolCallbackCollector {

    private final ApplicationContext applicationContext;

    private final ToolRestService toolRestService;

    public ToolCallback[] collectEnabledLocalCallbacks() {
        return collectCallbacks((tool, runtimeToolName) -> !Boolean.FALSE.equals(tool.getEnabled()));
    }

    public ToolCallback[] collectMcpExposedCallbacks() {
        return collectCallbacks(toolRestService::isToolExposedToMcp);
    }

    private ToolCallback[] collectCallbacks(BiPredicate<ToolEntity, String> predicate) {
        List<ToolEntity> toolEntities = toolRestService.listPlatformTools(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        if (toolEntities.isEmpty()) {
            return new ToolCallback[0];
        }

        Map<String, ToolCallback> uniqueCallbacks = new LinkedHashMap<>();
        addAnnotatedToolCallbacks(toolEntities, predicate, uniqueCallbacks);
        addFunctionBeanCallbacks(toolEntities, predicate, uniqueCallbacks);
        return uniqueCallbacks.values().toArray(new ToolCallback[0]);
    }

    private void addAnnotatedToolCallbacks(List<ToolEntity> toolEntities, BiPredicate<ToolEntity, String> predicate,
            Map<String, ToolCallback> uniqueCallbacks) {
        Set<Object> toolObjects = new LinkedHashSet<>();
        for (ToolEntity toolEntity : toolEntities) {
            if (toolEntity == null || !StringUtils.hasText(toolEntity.getBeanName())) {
                continue;
            }
            if ("FUNCTION_BEAN".equalsIgnoreCase(toolEntity.getBindingType())) {
                continue;
            }
            try {
                toolObjects.add(applicationContext.getBean(toolEntity.getBeanName()));
            } catch (Exception ex) {
                log.debug("Skip annotated tool bean {}: {}", toolEntity.getBeanName(), ex.getMessage());
            }
        }

        // Filter out objects that have no @Tool-annotated methods (e.g. @McpTool-only beans).
        // MethodToolCallbackProvider throws IllegalArgumentException if no @Tool methods are found.
        Set<Object> filteredObjects = new LinkedHashSet<>();
        for (Object toolObject : toolObjects) {
            if (hasToolAnnotatedMethod(toolObject)) {
                filteredObjects.add(toolObject);
            } else {
                log.debug("Skip bean {}: no @Tool-annotated methods found (may use @McpTool instead)",
                        toolObject.getClass().getSimpleName());
            }
        }

        if (filteredObjects.isEmpty()) {
            return;
        }

        ToolCallback[] callbacks = MethodToolCallbackProvider.builder()
                .toolObjects(filteredObjects.toArray())
                .build()
                .getToolCallbacks();
        for (ToolCallback callback : callbacks) {
            addIfEligible(callback, predicate, uniqueCallbacks);
        }
    }

    private void addFunctionBeanCallbacks(List<ToolEntity> toolEntities, BiPredicate<ToolEntity, String> predicate,
            Map<String, ToolCallback> uniqueCallbacks) {
        Map<String, ToolCallback> callbackBeans = applicationContext.getBeansOfType(ToolCallback.class);
        List<String> allowedBeanNames = toolEntities.stream()
                .filter(tool -> tool != null && "FUNCTION_BEAN".equalsIgnoreCase(tool.getBindingType()))
                .map(ToolEntity::getBeanName)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
        if (allowedBeanNames.isEmpty()) {
            return;
        }

        for (String beanName : allowedBeanNames) {
            ToolCallback callback = callbackBeans.get(beanName);
            addIfEligible(callback, predicate, uniqueCallbacks);
        }
    }

    private void addIfEligible(ToolCallback callback, BiPredicate<ToolEntity, String> predicate,
            Map<String, ToolCallback> uniqueCallbacks) {
        if (callback == null || callback.getToolDefinition() == null
                || !StringUtils.hasText(callback.getToolDefinition().name())) {
            return;
        }

        String runtimeToolName = callback.getToolDefinition().name().trim();
        ToolEntity toolEntity = toolRestService.resolveRuntimeTool(runtimeToolName, BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                .orElse(null);
        if (toolEntity == null || !predicate.test(toolEntity, runtimeToolName)) {
            return;
        }
        uniqueCallbacks.putIfAbsent(runtimeToolName, callback);
    }

    /**
     * Check whether an object has at least one method annotated with @Tool.
     * Objects annotated with @McpTool only (no @Tool methods) should be skipped
     * by MethodToolCallbackProvider to avoid IllegalArgumentException.
     */
    private boolean hasToolAnnotatedMethod(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Tool.class)) {
                return true;
            }
        }
        return false;
    }
}