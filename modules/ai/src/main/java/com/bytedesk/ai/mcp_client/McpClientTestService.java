/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-26 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-26 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.CompleteRequest;
import io.modelcontextprotocol.spec.McpSchema.GetPromptRequest;
import io.modelcontextprotocol.spec.McpSchema.PromptReference;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ResourceReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP Client 测试服务
 * <p>
 * 封装 spring-ai-examples/model-context-protocol 中客户端示例（brave 相关除外）的实现细节：
 * </p>
 * <ul>
 *   <li>client-starter: {@code ChatClient + ToolCallbackProvider}，让大模型自动调用 MCP tools</li>
 *   <li>mcp-annotations-client / SampleClient: ping、listTools、callTool、listPrompts、getPrompt、
 *       listResources、readResource、completeCompletion 等底层 {@link McpSyncClient} API</li>
 * </ul>
 *
 * <p>客户端来源：</p>
 * <ol>
 *   <li>bytedesk-manager: 管理后台 {@code McpServerEntity} 中配置的外部 MCP 服务器
 *       （{@code bytedesk.ai.mcp.client.enabled=true} 时经 {@link McpClientManager} 加载）</li>
 *   <li>spring-ai-autoconfig: properties 中 {@code spring.ai.mcp.client.*} 配置的 MCP 连接</li>
 * </ol>
 *
 * <p>{@link McpClientTestController} 仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，
 * 真正的客户端解析、MCP 协议调用与 LLM 编排均在本服务中完成。</p>
 *
 * @see McpClientTestController
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpClientTestService {

    private static final String SOURCE_BYTEDSK_MANAGER = "bytedesk-manager";
    private static final String SOURCE_SPRING_AI = "spring-ai-autoconfig";

    private final ObjectProvider<McpClientManager> mcpClientManagerProvider;

    private final ObjectProvider<ChatModel> chatModelProvider;

    private final ApplicationContext applicationContext;

    //
    // ============ 客户端管理 ============
    //

    /**
     * 列出所有 MCP 客户端（连接的外部 MCP 服务器）
     */
    public List<Map<String, Object>> listClients() {
        List<Map<String, Object>> clients = new ArrayList<>();

        // 1. bytedesk 管理后台配置的外部 MCP 服务器（未启用时给出提示）
        McpClientManager manager = mcpClientManager();
        if (manager == null) {
            Map<String, Object> hint = new LinkedHashMap<>();
            hint.put("source", SOURCE_BYTEDSK_MANAGER);
            hint.put("enabled", false);
            hint.put("hint", "set bytedesk.ai.mcp.client.enabled=true and configure McpServerEntity in admin");
            clients.add(hint);
        }

        // 2. 汇总所有客户端（bytedesk manager + Spring AI autoconfig）
        for (NamedClient namedClient : allNamedClients()) {
            clients.add(clientView(namedClient));
        }
        return clients;
    }

    /**
     * 刷新 bytedesk MCP 客户端连接（从数据库重新加载 McpServerEntity 配置）
     */
    public Map<String, Object> refreshClients() {
        McpClientManager manager = mcpClientManager();
        if (manager == null) {
            throw new IllegalArgumentException(
                    "bytedesk MCP client is disabled, set bytedesk.ai.mcp.client.enabled=true");
        }
        manager.refreshConnectionConfigs();
        return Map.of(
                "refreshed", true,
                "clients", manager.listSyncClients().size());
    }

    /**
     * ping 测试客户端连通性；未指定 client 时测试全部客户端
     */
    public Map<String, Object> ping(String client) {
        List<NamedClient> targets = resolveTargets(client);
        if (targets.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        Map<String, Object> results = new LinkedHashMap<>();
        for (NamedClient target : targets) {
            try {
                target.client().ping();
                results.put(target.name(), "pong");
            } catch (Exception e) {
                results.put(target.name(), "error: " + e.getMessage());
            }
        }
        return results;
    }

    //
    // ============ Tools 工具 ============
    //

    /**
     * 列出 MCP 服务器提供的工具
     */
    public Map<String, Object> listTools(String client) {
        List<NamedClient> targets = resolveTargets(client);
        if (targets.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        List<Map<String, Object>> tools = new ArrayList<>();
        for (NamedClient target : targets) {
            try {
                target.client().listTools().tools().forEach(tool -> {
                    Map<String, Object> toolView = new LinkedHashMap<>();
                    toolView.put("client", target.name());
                    toolView.put("name", tool.name());
                    toolView.put("description", tool.description());
                    toolView.put("inputSchema", tool.inputSchema());
                    tools.add(toolView);
                });
            } catch (Exception e) {
                log.error("Failed to list tools for client {}: {}", target.name(), e.getMessage());
                Map<String, Object> errorView = new LinkedHashMap<>();
                errorView.put("client", target.name());
                errorView.put("error", e.getMessage());
                tools.add(errorView);
            }
        }
        return Map.of(
                "total", tools.size(),
                "tools", tools);
    }

    /**
     * 调用 MCP 服务器上的工具（支持 progressToken 进度通知）
     */
    public Map<String, Object> callTool(String client, String toolName, Map<String, Object> arguments,
            Object progressToken) {
        if (!StringUtils.hasText(toolName)) {
            throw new IllegalArgumentException("toolName is required");
        }

        Optional<NamedClient> target = resolveClient(client);
        if (target.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        CallToolRequest.Builder builder = CallToolRequest.builder(toolName);
        if (arguments != null && !arguments.isEmpty()) {
            builder.arguments(arguments);
        }
        if (progressToken != null) {
            builder.progressToken(progressToken);
        }

        CallToolResult result = target.get().client().callTool(builder.build());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("client", target.get().name());
        response.put("tool", toolName);
        response.put("isError", result.isError());
        response.put("content", result.content());
        response.put("structuredContent", result.structuredContent());
        return response;
    }

    //
    // ============ Prompts 提示词 ============
    //

    /**
     * 列出 MCP 服务器提供的提示词模板
     */
    public Map<String, Object> listPrompts(String client) {
        List<NamedClient> targets = resolveTargets(client);
        if (targets.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        Map<String, Object> promptsByClient = new LinkedHashMap<>();
        for (NamedClient target : targets) {
            try {
                promptsByClient.put(target.name(), target.client().listPrompts().prompts());
            } catch (Exception e) {
                promptsByClient.put(target.name(), "error: " + e.getMessage());
            }
        }
        return promptsByClient;
    }

    /**
     * 获取（渲染）指定提示词模板
     */
    public Map<String, Object> getPrompt(String client, String promptName, Map<String, Object> arguments) {
        if (!StringUtils.hasText(promptName)) {
            throw new IllegalArgumentException("promptName is required");
        }

        Optional<NamedClient> target = resolveClient(client);
        if (target.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        GetPromptRequest.Builder builder = GetPromptRequest.builder(promptName);
        if (arguments != null && !arguments.isEmpty()) {
            builder.arguments(arguments);
        }

        var result = target.get().client().getPrompt(builder.build());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("client", target.get().name());
        response.put("prompt", promptName);
        response.put("description", result.description());
        response.put("messages", result.messages());
        return response;
    }

    //
    // ============ Resources 资源 ============
    //

    /**
     * 列出 MCP 服务器提供的资源
     */
    public Map<String, Object> listResources(String client) {
        List<NamedClient> targets = resolveTargets(client);
        if (targets.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        Map<String, Object> resourcesByClient = new LinkedHashMap<>();
        for (NamedClient target : targets) {
            try {
                resourcesByClient.put(target.name(), target.client().listResources().resources());
            } catch (Exception e) {
                resourcesByClient.put(target.name(), "error: " + e.getMessage());
            }
        }
        return resourcesByClient;
    }

    /**
     * 读取指定资源内容
     */
    public Map<String, Object> readResource(String client, String uri) {
        if (!StringUtils.hasText(uri)) {
            throw new IllegalArgumentException("uri is required");
        }

        Optional<NamedClient> target = resolveClient(client);
        if (target.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        var result = target.get().client().readResource(ReadResourceRequest.builder(uri).build());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("client", target.get().name());
        response.put("uri", uri);
        response.put("contents", result.contents());
        return response;
    }

    //
    // ============ Completion 自动补全 ============
    //

    /**
     * 提示词/资源参数自动补全
     *
     * @param refType prompt 或 resource
     * @param ref 提示词名称或资源 URI 模板（如 user-status://{username}）
     */
    public Map<String, Object> completeCompletion(String refType, String ref, String argumentName,
            String argumentValue, String client) {
        if (!StringUtils.hasText(ref)) {
            throw new IllegalArgumentException("ref is required");
        }

        Optional<NamedClient> target = resolveClient(client);
        if (target.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        McpSchema.CompleteReference reference = "resource".equalsIgnoreCase(refType)
                ? new ResourceReference(ref)
                : new PromptReference(ref);

        CompleteRequest request = CompleteRequest.builder(reference,
                new CompleteRequest.CompleteArgument(argumentName, argumentValue)).build();

        var result = target.get().client().completeCompletion(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("client", target.get().name());
        response.put("refType", refType);
        response.put("ref", ref);
        response.put("completion", result.completion());
        return response;
    }

    //
    // ============ Chat 对话（LLM + MCP Tools）============
    //

    /**
     * 大模型对话 + MCP 工具自动调用
     * <p>
     * 参考 client-starter 示例：{@code ChatClient + ToolCallbackProvider}，
     * 大模型自动决策调用 MCP 服务器上的工具。
     * </p>
     *
     * @param client 可选，不传则使用全部客户端的工具
     */
    public Map<String, Object> chatWithTools(String message, String client) {
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException("message is required");
        }

        ChatModel chatModel = chatModelProvider.getIfAvailable();
        if (chatModel == null) {
            throw new IllegalStateException("ChatModel is not available");
        }

        List<NamedClient> targets = resolveTargets(client);
        if (targets.isEmpty()) {
            throw new IllegalArgumentException(noClientMessage(client));
        }

        // 将 MCP 客户端封装为 Spring AI 工具回调，大模型可自动调用 MCP 服务器上的工具
        List<McpSyncClient> targetClients = targets.stream().map(NamedClient::client).toList();
        SyncMcpToolCallbackProvider toolCallbackProvider = SyncMcpToolCallbackProvider.builder()
                .mcpClients(targetClients)
                .build();

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultTools(toolCallbackProvider)
                .build();

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", message);
        result.put("clients", targets.stream().map(NamedClient::name).toList());
        result.put("response", response);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    //
    // ============ 私有辅助方法 ============
    //

    /**
     * 带来源与配置信息的 MCP 客户端视图：
     * bytedesk manager 客户端未设置 clientInfo，需从连接配置中补齐 uid/name
     */
    record NamedClient(String uid, String name, String source, McpSyncClient client) {
    }

    /**
     * 获取 bytedesk MCP 客户端管理器（可能未启用）
     */
    private McpClientManager mcpClientManager() {
        return mcpClientManagerProvider.getIfAvailable();
    }

    /**
     * 获取 Spring AI 自动配置的 MCP 客户端（spring.ai.mcp.client.* properties 配置的连接）
     * 通过 ResolvableType 查找 List&lt;McpSyncClient&gt; 类型的 bean，避免直接注入集合的歧义
     */
    private List<McpSyncClient> autoConfiguredClients() {
        ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, McpSyncClient.class);
        String[] beanNames = applicationContext.getBeanNamesForType(listType);
        List<McpSyncClient> clients = new ArrayList<>();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            if (bean instanceof List<?> list) {
                for (Object element : list) {
                    if (element instanceof McpSyncClient mcpSyncClient) {
                        clients.add(mcpSyncClient);
                    }
                }
            }
        }
        return clients;
    }

    /**
     * 获取全部客户端（bytedesk manager + Spring AI autoconfig），携带 uid/名称/来源信息
     * 注意：bytedesk manager 客户端未设置 clientInfo，名称需从连接配置（McpClientConnectionConfig）获取
     */
    private List<NamedClient> allNamedClients() {
        List<NamedClient> clients = new ArrayList<>();

        // 1. bytedesk manager 客户端：通过连接配置关联 uid/name 与运行时客户端
        McpClientManager manager = mcpClientManager();
        if (manager != null) {
            manager.listEnabledConnectionConfigs().forEach(config -> manager.findSyncClient(config.uid())
                    .ifPresent(client -> clients.add(
                            new NamedClient(config.uid(), config.name(), SOURCE_BYTEDSK_MANAGER, client))));
        }

        // 2. Spring AI autoconfig 客户端：clientInfo.name 即连接配置名称
        for (McpSyncClient client : autoConfiguredClients()) {
            clients.add(new NamedClient(null, clientName(client), SOURCE_SPRING_AI, client));
        }
        return clients;
    }

    /**
     * 解析目标客户端列表：
     * 1. 指定 client（uid 或名称）时仅返回该客户端
     * 2. 未指定时返回全部客户端
     */
    private List<NamedClient> resolveTargets(String client) {
        if (!StringUtils.hasText(client)) {
            return allNamedClients();
        }
        return resolveClient(client).map(List::of).orElse(List.of());
    }

    /**
     * 按 uid（bytedesk manager 客户端）或名称解析单个客户端
     */
    private Optional<NamedClient> resolveClient(String client) {
        if (!StringUtils.hasText(client)) {
            return Optional.empty();
        }
        return allNamedClients().stream()
                .filter(named -> client.equals(named.uid()) || client.equals(named.name()))
                .findFirst();
    }

    /**
     * 获取客户端名称（clientInfo.name，即 MCP 连接配置的名称）
     * bytedesk manager 客户端未设置 clientInfo 时返回 SDK 默认名称
     */
    private String clientName(McpSyncClient client) {
        try {
            return client.getClientInfo() != null ? client.getClientInfo().name() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 构建客户端概览信息
     */
    private Map<String, Object> clientView(NamedClient namedClient) {
        McpSyncClient client = namedClient.client();
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("source", namedClient.source());
        view.put("uid", namedClient.uid());
        view.put("name", namedClient.name());
        try {
            var clientInfo = client.getClientInfo();
            view.put("clientInfo", clientInfo != null
                    ? Map.of("name", clientInfo.name(), "version", String.valueOf(clientInfo.version()))
                    : null);
        } catch (Exception e) {
            view.put("clientInfo", null);
        }
        try {
            var serverInfo = client.getServerInfo();
            view.put("serverInfo", serverInfo != null
                    ? Map.of("name", serverInfo.name(), "version", String.valueOf(serverInfo.version()))
                    : null);
        } catch (Exception e) {
            view.put("serverInfo", null);
        }
        try {
            view.put("serverCapabilities", client.getServerCapabilities());
        } catch (Exception e) {
            view.put("serverCapabilities", null);
        }
        return view;
    }

    /**
     * 构造"无可用客户端"错误提示
     */
    private String noClientMessage(String client) {
        if (StringUtils.hasText(client)) {
            return "MCP client not found: " + client
                    + ". Check /spring/ai/api/v1/mcp/client/clients for available clients";
        }
        return "No MCP clients available. Either:"
                + " (1) set bytedesk.ai.mcp.client.enabled=true and configure McpServerEntity in admin console,"
                + " or (2) configure spring.ai.mcp.client.* connections in properties";
    }
}
