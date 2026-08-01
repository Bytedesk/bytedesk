package com.bytedesk.ai.mcp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.lang.reflect.Method;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BytedeskMcpToolProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.mcp.server", name = "enabled", havingValue = "true")
public class BytedeskMcpToolConfiguration {

    private final ListableBeanFactory beanFactory;

    private final BytedeskMcpToolProperties properties;

    @Bean
    @ConditionalOnProperty(prefix = "bytedesk.ai.mcp.tools", name = "enabled", havingValue = "true", matchIfMissing = true)
    ToolCallbackProvider bytedeskMcpToolCallbackProvider() {
        List<Object> toolObjects = findToolObjects();
        if (toolObjects.isEmpty()) {
            log.info("No Bytedesk MCP tool objects matched the configured packages");
            return ToolCallbackProvider.from(List.of());
        }

        ToolCallback[] callbacks = MethodToolCallbackProvider.builder()
                .toolObjects(toolObjects.toArray())
                .build()
                .getToolCallbacks();
        List<ToolCallback> filteredCallbacks = filterCallbacks(callbacks);

        log.info("Registered {} Bytedesk MCP tools from {} tool objects", filteredCallbacks.size(), toolObjects.size());
        return ToolCallbackProvider.from(filteredCallbacks);
    }

    private List<Object> findToolObjects() {
        List<Object> toolObjects = new ArrayList<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            Class<?> beanType = beanFactory.getType(beanName, false);
            if (beanType == null) {
                continue;
            }
            Class<?> userClass = ClassUtils.getUserClass(beanType);
            if (!isIncludedPackage(userClass) || !hasExposedToolMethod(userClass)) {
                continue;
            }
            toolObjects.add(beanFactory.getBean(beanName));
        }
        return toolObjects;
    }

    private List<ToolCallback> filterCallbacks(ToolCallback[] callbacks) {
        Pattern readOnlyPattern = Pattern.compile(properties.getReadOnlyIncludePattern());
        Pattern excludePattern = Pattern.compile(properties.getExcludePattern());
        Map<String, ToolCallback> uniqueCallbacks = new LinkedHashMap<>();

        for (ToolCallback callback : callbacks) {
            String toolName = callback.getToolDefinition().name();
            boolean writeToolAllowed = properties.getWriteAllowNames().contains(toolName);
            if (!properties.getAllowNames().isEmpty() && !properties.getAllowNames().contains(toolName)) {
                continue;
            }
            if (properties.getDenyNames().contains(toolName)) {
                continue;
            }
            if (!writeToolAllowed && excludePattern.matcher(toolName).matches()) {
                continue;
            }
            if (properties.isReadOnly() && !writeToolAllowed && !readOnlyPattern.matcher(toolName).matches()) {
                continue;
            }
            uniqueCallbacks.putIfAbsent(toolName, callback);
        }
        return new ArrayList<>(uniqueCallbacks.values());
    }

    private boolean isIncludedPackage(Class<?> beanType) {
        Package beanPackage = beanType.getPackage();
        if (beanPackage == null) {
            return false;
        }
        String packageName = beanPackage.getName();
        return properties.getIncludePackages().stream().anyMatch(packageName::startsWith);
    }

    private boolean hasExposedToolMethod(Class<?> beanType) {
        Pattern readOnlyPattern = Pattern.compile(properties.getReadOnlyIncludePattern());
        Pattern excludePattern = Pattern.compile(properties.getExcludePattern());

        return List.of(beanType.getMethods()).stream()
                .filter(method -> AnnotationUtils.findAnnotation(method, Tool.class) != null)
                .map(Method::getName)
                .anyMatch(toolName -> isExposedToolName(toolName, readOnlyPattern, excludePattern));
    }

    private boolean isExposedToolName(String toolName, Pattern readOnlyPattern, Pattern excludePattern) {
        boolean writeToolAllowed = properties.getWriteAllowNames().contains(toolName);
        if (!properties.getAllowNames().isEmpty() && !properties.getAllowNames().contains(toolName)) {
            return false;
        }
        if (properties.getDenyNames().contains(toolName)) {
            return false;
        }
        if (!writeToolAllowed && excludePattern.matcher(toolName).matches()) {
            return false;
        }
        if (properties.isReadOnly() && !writeToolAllowed && !readOnlyPattern.matcher(toolName).matches()) {
            return false;
        }
        return true;
    }
}
