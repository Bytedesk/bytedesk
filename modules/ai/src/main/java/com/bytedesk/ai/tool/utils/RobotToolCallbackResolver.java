package com.bytedesk.ai.tool.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.tool.ToolInvocationAuditService;
import com.bytedesk.ai.tool.ToolRestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RobotToolCallbackResolver {

    private final ToolRestService toolRestService;

    private final ToolInvocationAuditService toolInvocationAuditService;

    private final BytedeskLocalToolCallbackProvider localToolCallbackProvider;

    private final ToolCallbackProvider builtinRobotToolCallbackProvider;

    private final ToolCallbackProvider externalMcpToolCallbackProvider;

    public RobotToolCallbackResolver(
            ToolRestService toolRestService,
            ToolInvocationAuditService toolInvocationAuditService,
            BytedeskLocalToolCallbackProvider localToolCallbackProvider,
            @Qualifier("builtinRobotToolCallbackProvider") ObjectProvider<ToolCallbackProvider> builtinRobotToolCallbackProvider,
            @Qualifier("externalMcpToolCallbackProvider") ObjectProvider<ToolCallbackProvider> externalMcpToolCallbackProvider) {
        this.toolRestService = toolRestService;
        this.toolInvocationAuditService = toolInvocationAuditService;
        this.localToolCallbackProvider = localToolCallbackProvider;
        this.builtinRobotToolCallbackProvider = builtinRobotToolCallbackProvider.getIfAvailable();
        this.externalMcpToolCallbackProvider = externalMcpToolCallbackProvider.getIfAvailable();
    }

    public List<ToolCallback> resolveToolCallbacks(List<String> requestedToolNames) {
        if (requestedToolNames == null || requestedToolNames.isEmpty()) {
            return List.of();
        }

        Map<String, ToolCallback> availableCallbacks = new LinkedHashMap<>();
        addProviderCallbacks(builtinRobotToolCallbackProvider, availableCallbacks);
        addCallbacks(localToolCallbackProvider.getToolCallbacks(), availableCallbacks);
        addProviderCallbacks(externalMcpToolCallbackProvider, availableCallbacks);

        List<ToolCallback> resolvedCallbacks = new ArrayList<>();
        List<String> missingToolNames = new ArrayList<>();

        for (String requestedToolName : requestedToolNames) {
            if (!StringUtils.hasText(requestedToolName)) {
                continue;
            }

            String normalizedToolName = requestedToolName.trim();
            ToolCallback callback = availableCallbacks.get(normalizedToolName);
            if (callback != null) {
                if (!toolRestService.isRuntimeToolEnabled(normalizedToolName)) {
                    log.info("Requested robot tool is disabled by registry: {}", normalizedToolName);
                    missingToolNames.add(normalizedToolName);
                    continue;
                }
                resolvedCallbacks.add(toolInvocationAuditService.wrap(callback));
            } else {
                missingToolNames.add(normalizedToolName);
            }
        }

        if (!missingToolNames.isEmpty()) {
            log.warn("Requested robot tools are not available: {}", missingToolNames);
        }
        return resolvedCallbacks;
    }

    private void addProviderCallbacks(ToolCallbackProvider provider, Map<String, ToolCallback> callbacks) {
        if (provider == null) {
            return;
        }

        addCallbacks(provider.getToolCallbacks(), callbacks);
    }

    private void addCallbacks(ToolCallback[] toolCallbacks, Map<String, ToolCallback> callbacks) {
        if (toolCallbacks == null || toolCallbacks.length == 0) {
            return;
        }

        for (ToolCallback callback : toolCallbacks) {
            if (callback == null || callback.getToolDefinition() == null || !StringUtils.hasText(callback.getToolDefinition().name())) {
                continue;
            }
            callbacks.putIfAbsent(callback.getToolDefinition().name(), callback);
        }
    }
}