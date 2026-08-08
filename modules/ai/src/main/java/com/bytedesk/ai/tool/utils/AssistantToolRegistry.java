package com.bytedesk.ai.tool.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.tool.ToolRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantToolRegistry {

    private final ApplicationContext applicationContext;

    private final ToolRestService toolRestService;

    public ToolCallback[] resolveEnabledCallbacks(String... runtimeToolNames) {
        if (runtimeToolNames == null || runtimeToolNames.length == 0) {
            return new ToolCallback[0];
        }

        List<ToolCallback> callbacks = new ArrayList<>();
        for (String runtimeToolName : runtimeToolNames) {
            if (!StringUtils.hasText(runtimeToolName)) {
                continue;
            }

            String normalizedToolName = runtimeToolName.trim();
            if (!toolRestService.isRuntimeToolEnabled(normalizedToolName)) {
                log.info("Assistant tool disabled by registry: {}", normalizedToolName);
                continue;
            }

            try {
                ToolCallback callback = applicationContext.getBean(normalizedToolName, ToolCallback.class);
                callbacks.add(callback);
            } catch (Exception ex) {
                log.warn("Assistant tool callback not found: {}, cause={}", normalizedToolName, ex.getMessage());
            }
        }

        return callbacks.toArray(new ToolCallback[0]);
    }
}