package com.bytedesk.ai.tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bytedesk.ai.tool.utils.BytedeskLocalToolCallbackProvider;
import com.bytedesk.ai.tool_audit.ToolAuditEntity;
import com.bytedesk.ai.tool_audit.ToolAuditRestService;
import com.bytedesk.ai.tool_call.ToolCallEntity;
import com.bytedesk.ai.tool_call.ToolCallRestService;
import com.bytedesk.ai.tool_call.ToolCallStatusEnum;

@Service
public class ToolInvocationAuditService {

    private final ToolRestService toolRestService;

    private final ToolCallRestService toolCallRestService;

    private final ToolAuditRestService toolAuditRestService;

    private final BytedeskLocalToolCallbackProvider localToolCallbackProvider;

    private final ToolCallbackProvider builtinRobotToolCallbackProvider;

    private final ToolCallbackProvider externalMcpToolCallbackProvider;

    public ToolInvocationAuditService(
            ToolRestService toolRestService,
            ToolCallRestService toolCallRestService,
            ToolAuditRestService toolAuditRestService,
            BytedeskLocalToolCallbackProvider localToolCallbackProvider,
            @Qualifier("builtinRobotToolCallbackProvider") ObjectProvider<ToolCallbackProvider> builtinRobotToolCallbackProvider,
            @Qualifier("externalMcpToolCallbackProvider") ObjectProvider<ToolCallbackProvider> externalMcpToolCallbackProvider) {
        this.toolRestService = toolRestService;
        this.toolCallRestService = toolCallRestService;
        this.toolAuditRestService = toolAuditRestService;
        this.localToolCallbackProvider = localToolCallbackProvider;
        this.builtinRobotToolCallbackProvider = builtinRobotToolCallbackProvider.getIfAvailable();
        this.externalMcpToolCallbackProvider = externalMcpToolCallbackProvider.getIfAvailable();
    }

    public ToolCallback wrap(ToolCallback delegate) {
        if (delegate == null || delegate.getToolDefinition() == null
                || !StringUtils.hasText(delegate.getToolDefinition().name())) {
            return delegate;
        }
        return new AuditedToolCallback(delegate, this);
    }

    String execute(ToolCallback delegate, String input, ToolContext toolContext) {
        String runtimeToolName = delegate.getToolDefinition().name();
        Map<String, Object> context = extractContext(toolContext);
        ToolEntity toolEntity = resolveToolEntity(runtimeToolName);
        ToolCallEntity toolCall = toolCallRestService.createRuntimeRecord(toolEntity, runtimeToolName, input, context);

        if (Boolean.TRUE.equals(toolCall.getRequiresApproval())) {
            ToolAuditEntity toolAudit = toolAuditRestService.createApprovalRequest(toolCall, context);
            toolCallRestService.linkAudit(toolCall.getUid(), toolAudit.getUid());
            return JSON.toJSONString(Map.of(
                    "status", ToolCallStatusEnum.PENDING_APPROVAL.name(),
                    "toolCallUid", toolCall.getUid(),
                    "toolAuditUid", toolAudit.getUid(),
                    "toolName", runtimeToolName,
                    "message", "Tool call requires approval before execution."));
        }

        long started = System.nanoTime();
        try {
            String response = toolContext != null ? delegate.call(input, toolContext) : delegate.call(input);
            toolCallRestService.completeRuntimeRecord(toolCall.getUid(), response,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
            return response;
        } catch (RuntimeException ex) {
            toolCallRestService.failRuntimeRecord(toolCall.getUid(), ex.getMessage(),
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
            throw ex;
        }
    }

    public String executeApprovedToolCall(String toolCallUid) {
        if (!StringUtils.hasText(toolCallUid)) {
            return null;
        }

        ToolCallEntity toolCall = toolCallRestService.findByUid(toolCallUid).orElse(null);
        if (toolCall == null) {
            return null;
        }

        String runtimeToolName = resolveStoredRuntimeToolName(toolCall);
        if (!StringUtils.hasText(runtimeToolName)) {
            toolCallRestService.failRuntimeRecord(toolCallUid, "Approved tool execution failed: runtime tool name is missing.", 0L);
            return null;
        }

        ToolCallback callback = resolveRawCallback(runtimeToolName);
        if (callback == null) {
            toolCallRestService.failRuntimeRecord(toolCallUid,
                    "Approved tool execution failed: callback not found for " + runtimeToolName + ".",
                    0L);
            return null;
        }

        toolCallRestService.beginApprovedExecution(toolCallUid);
        ToolContext toolContext = restoreToolContext(toolCall.getToolContext());
        long started = System.nanoTime();
        try {
            String response = toolContext != null
                    ? callback.call(toolCall.getRequestPayload(), toolContext)
                    : callback.call(toolCall.getRequestPayload());
            toolCallRestService.completeRuntimeRecord(toolCallUid, response,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
            return response;
        } catch (RuntimeException ex) {
            toolCallRestService.failRuntimeRecord(toolCallUid, ex.getMessage(),
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
            return null;
        }
    }

    private ToolEntity resolveToolEntity(String runtimeToolName) {
        return toolRestService.resolveRuntimeTool(runtimeToolName).orElse(null);
    }

    private Map<String, Object> extractContext(ToolContext toolContext) {
        if (toolContext == null || toolContext.getContext() == null || toolContext.getContext().isEmpty()) {
            return Map.of();
        }
        return new LinkedHashMap<>(toolContext.getContext());
    }

    private ToolCallback resolveRawCallback(String runtimeToolName) {
        if (!StringUtils.hasText(runtimeToolName)) {
            return null;
        }

        Map<String, ToolCallback> availableCallbacks = new LinkedHashMap<>();
        addProviderCallbacks(builtinRobotToolCallbackProvider, availableCallbacks);
        addCallbacks(localToolCallbackProvider.getToolCallbacks(), availableCallbacks);
        addProviderCallbacks(externalMcpToolCallbackProvider, availableCallbacks);
        return availableCallbacks.get(runtimeToolName.trim());
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
            if (callback == null || callback.getToolDefinition() == null
                    || !StringUtils.hasText(callback.getToolDefinition().name())) {
                continue;
            }
            callbacks.putIfAbsent(callback.getToolDefinition().name(), callback);
        }
    }

    private ToolContext restoreToolContext(String rawContext) {
        Map<String, Object> context = parseContext(rawContext);
        if (context.isEmpty()) {
            return null;
        }
        return new ToolContext(context);
    }

    private Map<String, Object> parseContext(String rawContext) {
        if (!StringUtils.hasText(rawContext)) {
            return Map.of();
        }
        try {
            Map<String, Object> context = JSON.parseObject(rawContext, new TypeReference<Map<String, Object>>() {
            });
            return context == null ? Map.of() : new LinkedHashMap<>(context);
        } catch (RuntimeException ex) {
            return Map.of();
        }
    }

    private String resolveStoredRuntimeToolName(ToolCallEntity toolCall) {
        if (toolCall == null) {
            return null;
        }

        List<String> candidates = new ArrayList<>();
        candidates.add(toolCall.getRuntimeToolName());
        candidates.add(toolCall.getToolKey());
        candidates.add(toolCall.getName());

        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate.trim();
            }
        }
        return null;
    }

    private static final class AuditedToolCallback implements ToolCallback {

        private final ToolCallback delegate;

        private final ToolInvocationAuditService auditService;

        private AuditedToolCallback(ToolCallback delegate, ToolInvocationAuditService auditService) {
            this.delegate = delegate;
            this.auditService = auditService;
        }

        @Override
        public ToolDefinition getToolDefinition() {
            return delegate.getToolDefinition();
        }

        @Override
        public ToolMetadata getToolMetadata() {
            return delegate.getToolMetadata();
        }

        @Override
        public String call(String toolInput) {
            return auditService.execute(delegate, toolInput, null);
        }

        @Override
        public String call(String toolInput, ToolContext toolContext) {
            return auditService.execute(delegate, toolInput, toolContext);
        }
    }
}