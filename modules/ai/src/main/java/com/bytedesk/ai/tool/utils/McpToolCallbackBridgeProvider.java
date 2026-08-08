package com.bytedesk.ai.tool.utils;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Lazy
@Component("bytedeskMcpToolCallbackProvider")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.ai.mcp.tools", name = "enabled", havingValue = "true", matchIfMissing = true)
public class McpToolCallbackBridgeProvider implements ToolCallbackProvider {

    private final RegistryManagedToolCallbackCollector collector;

    @Override
    public ToolCallback[] getToolCallbacks() {
        ToolCallback[] callbacks = collector.collectMcpExposedCallbacks();
        log.debug("Resolved {} registry-managed MCP tool callbacks", callbacks.length);
        return callbacks;
    }
}