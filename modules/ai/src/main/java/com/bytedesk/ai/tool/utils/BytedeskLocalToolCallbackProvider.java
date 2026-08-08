package com.bytedesk.ai.tool.utils;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("bytedeskLocalToolCallbackProvider")
@RequiredArgsConstructor
public class BytedeskLocalToolCallbackProvider {

    private final RegistryManagedToolCallbackCollector collector;

    public ToolCallback[] getToolCallbacks() {
        return collector.collectEnabledLocalCallbacks();
    }
}