/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-26 11:17:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 11:18:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.mcp_server;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.client.McpTransport;
import org.springframework.ai.mcp.client.sse.SseServerSentEventMcpTransport;
import org.springframework.ai.mcp.client.stdio.ServerSentEventMcpTransport;
import org.springframework.ai.mcp.spec.McpCallToolRequest;
import org.springframework.ai.mcp.spec.McpCallToolResult;
import org.springframework.ai.mcp.spec.McpGetPromptRequest;
import org.springframework.ai.mcp.spec.McpGetPromptResult;
import org.springframework.ai.mcp.spec.McpInitializeRequest;
import org.springframework.ai.mcp.spec.McpInitializeResult;
import org.springframework.ai.mcp.spec.McpListPromptsRequest;
import org.springframework.ai.mcp.spec.McpListPromptsResult;
import org.springframework.ai.mcp.spec.McpListResourcesRequest;
import org.springframework.ai.mcp.spec.McpListResourcesResult;
import org.springframework.ai.mcp.spec.McpListToolsRequest;
import org.springframework.ai.mcp.spec.McpListToolsResult;
import org.springframework.ai.mcp.spec.McpReadResourceRequest;
import org.springframework.ai.mcp.spec.McpReadResourceResult;
import org.springframework.ai.mcp.spec.McpResource;
import org.springframework.ai.mcp.spec.McpTool;
import org.springframework.ai.mcp.spec.McpToolCall;
import org.springframework.ai.mcp.spec.McpPrompt;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.alibaba.fastjson2.JSON;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP Server 客户端服务
 * 负责与第三方 MCP 服务器通信，调用工具、获取资源、管理连接等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpServerService {

    private final McpServerRestService mcpServerRestService;
    private final WebClient.Builder webClientBuilder;
    
    // MCP 客户端缓存，避免重复创建连接
    private final Map<String, McpClient> clientCache = new ConcurrentHashMap<>();
    
    // 服务器状态缓存
    private final Map<String, ServerStatus> serverStatusCache = new ConcurrentHashMap<>();

    /**
     * 服务器状态类
     */
    public static class ServerStatus {
        private boolean connected;
        private ZonedDateTime lastConnected;
        private String lastError;
        private List<McpTool> tools;
        private List<McpResource> resources;
        private List<McpPrompt> prompts;

        // 构造函数和 getter/setter
        public ServerStatus() {
            this.tools = new ArrayList<>();
            this.resources = new ArrayList<>();
            this.prompts = new ArrayList<>();
        }

        public boolean isConnected() { return connected; }
        public void setConnected(boolean connected) { this.connected = connected; }
        public ZonedDateTime getLastConnected() { return lastConnected; }
        public void setLastConnected(ZonedDateTime lastConnected) { this.lastConnected = lastConnected; }
        public String getLastError() { return lastError; }
        public void setLastError(String lastError) { this.lastError = lastError; }
        public List<McpTool> getTools() { return tools; }
        public void setTools(List<McpTool> tools) { this.tools = tools; }
        public List<McpResource> getResources() { return resources; }
        public void setResources(List<McpResource> resources) { this.resources = resources; }
        public List<McpPrompt> getPrompts() { return prompts; }
        public void setPrompts(List<McpPrompt> prompts) { this.prompts = prompts; }
    }

    /**
     * 工具调用请求类
     */
    public static class ToolCallRequest {
        private String serverUid;
        private String toolName;
        private Map<String, Object> arguments;

        public ToolCallRequest() {}
        
        public ToolCallRequest(String serverUid, String toolName, Map<String, Object> arguments) {
            this.serverUid = serverUid;
            this.toolName = toolName;
            this.arguments = arguments;
        }

        public String getServerUid() { return serverUid; }
        public void setServerUid(String serverUid) { this.serverUid = serverUid; }
        public String getToolName() { return toolName; }
        public void setToolName(String toolName) { this.toolName = toolName; }
        public Map<String, Object> getArguments() { return arguments; }
        public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
    }

    /**
     * 工具调用结果类
     */
    public static class ToolCallResponse {
        private boolean success;
        private Object result;
        private String error;

        public ToolCallResponse(boolean success, Object result, String error) {
            this.success = success;
            this.result = result;
            this.error = error;
        }

        public boolean isSuccess() { return success; }
        public Object getResult() { return result; }
        public String getError() { return error; }
    }

    /**
     * 连接到 MCP 服务器
     */
    public CompletableFuture<Boolean> connectToServer(String serverUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<McpServerEntity> serverOpt = mcpServerRestService.findByUid(serverUid);
                if (serverOpt.isEmpty()) {
                    log.error("MCP server not found: {}", serverUid);
                    return false;
                }

                McpServerEntity server = serverOpt.get();
                if (!server.getEnabled()) {
                    log.warn("MCP server is disabled: {}", serverUid);
                    return false;
                }

                // 检查是否已经连接
                McpClient existingClient = clientCache.get(serverUid);
                if (existingClient != null) {
                    log.debug("MCP client already exists for server: {}", serverUid);
                    return true;
                }

                // 创建新的 MCP 客户端
                McpClient client = createMcpClient(server);
                if (client == null) {
                    return false;
                }

                // 初始化连接
                boolean initialized = initializeConnection(client, server);
                if (initialized) {
                    clientCache.put(serverUid, client);
                    updateServerStatus(serverUid, true, null);
                    updateEntityStatus(server, McpServerStatusEnum.ACTIVE, null);
                    
                    // 异步加载服务器能力
                    loadServerCapabilities(serverUid, client);
                }

                return initialized;
            } catch (Exception e) {
                log.error("Failed to connect to MCP server {}: {}", serverUid, e.getMessage(), e);
                updateServerStatus(serverUid, false, e.getMessage());
                updateEntityStatus(serverUid, McpServerStatusEnum.ERROR, e.getMessage());
                return false;
            }
        });
    }

    /**
     * 断开与 MCP 服务器的连接
     */
    public CompletableFuture<Boolean> disconnectFromServer(String serverUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                McpClient client = clientCache.remove(serverUid);
                if (client != null) {
                    client.close();
                }
                
                serverStatusCache.remove(serverUid);
                updateEntityStatus(serverUid, McpServerStatusEnum.INACTIVE, null);
                
                log.info("Disconnected from MCP server: {}", serverUid);
                return true;
            } catch (Exception e) {
                log.error("Failed to disconnect from MCP server {}: {}", serverUid, e.getMessage(), e);
                return false;
            }
        });
    }

    /**
     * 调用 MCP 工具
     */
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public CompletableFuture<ToolCallResponse> callTool(ToolCallRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                McpClient client = clientCache.get(request.getServerUid());
                if (client == null) {
                    // 尝试连接
                    boolean connected = connectToServer(request.getServerUid()).get();
                    if (!connected) {
                        return new ToolCallResponse(false, null, "Failed to connect to MCP server");
                    }
                    client = clientCache.get(request.getServerUid());
                }

                if (client == null) {
                    return new ToolCallResponse(false, null, "MCP client not available");
                }

                // 构建工具调用请求
                McpCallToolRequest toolRequest = McpCallToolRequest.builder()
                    .params(McpCallToolRequest.Params.builder()
                        .name(request.getToolName())
                        .arguments(request.getArguments() != null ? request.getArguments() : new HashMap<>())
                        .build())
                    .build();

                // 执行工具调用
                McpCallToolResult result = client.callTool(toolRequest);
                
                log.debug("Tool call result for {}: {}", request.getToolName(), result);
                return new ToolCallResponse(true, result, null);

            } catch (Exception e) {
                log.error("Failed to call tool {} on server {}: {}", 
                    request.getToolName(), request.getServerUid(), e.getMessage(), e);
                return new ToolCallResponse(false, null, e.getMessage());
            }
        });
    }

    /**
     * 获取服务器可用工具列表
     */
    public CompletableFuture<List<McpTool>> getAvailableTools(String serverUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                McpClient client = getOrCreateClient(serverUid);
                if (client == null) {
                    return Collections.emptyList();
                }

                McpListToolsRequest request = McpListToolsRequest.builder()
                    .params(McpListToolsRequest.Params.builder().build())
                    .build();

                McpListToolsResult result = client.listTools(request);
                List<McpTool> tools = result.getResult().getTools();
                
                // 更新缓存
                ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
                status.setTools(tools);
                
                return tools;
            } catch (Exception e) {
                log.error("Failed to get tools from server {}: {}", serverUid, e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    /**
     * 获取服务器可用资源列表
     */
    public CompletableFuture<List<McpResource>> getAvailableResources(String serverUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                McpClient client = getOrCreateClient(serverUid);
                if (client == null) {
                    return Collections.emptyList();
                }

                McpListResourcesRequest request = McpListResourcesRequest.builder()
                    .params(McpListResourcesRequest.Params.builder().build())
                    .build();

                McpListResourcesResult result = client.listResources(request);
                List<McpResource> resources = result.getResult().getResources();
                
                // 更新缓存
                ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
                status.setResources(resources);
                
                return resources;
            } catch (Exception e) {
                log.error("Failed to get resources from server {}: {}", serverUid, e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    /**
     * 获取服务器可用提示列表
     */
    public CompletableFuture<List<McpPrompt>> getAvailablePrompts(String serverUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                McpClient client = getOrCreateClient(serverUid);
                if (client == null) {
                    return Collections.emptyList();
                }

                McpListPromptsRequest request = McpListPromptsRequest.builder()
                    .params(McpListPromptsRequest.Params.builder().build())
                    .build();

                McpListPromptsResult result = client.listPrompts(request);
                List<McpPrompt> prompts = result.getResult().getPrompts();
                
                // 更新缓存
                ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
                status.setPrompts(prompts);
                
                return prompts;
            } catch (Exception e) {
                log.error("Failed to get prompts from server {}: {}", serverUid, e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    /**
     * 读取资源内容
     */
    public CompletableFuture<Object> readResource(String serverUid, String resourceUri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                McpClient client = getOrCreateClient(serverUid);
                if (client == null) {
                    return null;
                }

                McpReadResourceRequest request = McpReadResourceRequest.builder()
                    .params(McpReadResourceRequest.Params.builder()
                        .uri(resourceUri)
                        .build())
                    .build();

                McpReadResourceResult result = client.readResource(request);
                return result.getResult();
            } catch (Exception e) {
                log.error("Failed to read resource {} from server {}: {}", resourceUri, serverUid, e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * 获取提示内容
     */
    public CompletableFuture<Object> getPrompt(String serverUid, String promptName, Map<String, Object> arguments) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                McpClient client = getOrCreateClient(serverUid);
                if (client == null) {
                    return null;
                }

                McpGetPromptRequest request = McpGetPromptRequest.builder()
                    .params(McpGetPromptRequest.Params.builder()
                        .name(promptName)
                        .arguments(arguments != null ? arguments : new HashMap<>())
                        .build())
                    .build();

                McpGetPromptResult result = client.getPrompt(request);
                return result.getResult();
            } catch (Exception e) {
                log.error("Failed to get prompt {} from server {}: {}", promptName, serverUid, e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * 健康检查
     */
    public CompletableFuture<Boolean> healthCheck(String serverUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<McpServerEntity> serverOpt = mcpServerRestService.findByUid(serverUid);
                if (serverOpt.isEmpty()) {
                    return false;
                }

                McpServerEntity server = serverOpt.get();
                
                // 检查连接状态
                McpClient client = clientCache.get(serverUid);
                boolean isHealthy = client != null;
                
                // 如果有健康检查 URL，使用它
                if (StringUtils.hasText(server.getHealthCheckUrl())) {
                    isHealthy = performHttpHealthCheck(server);
                }

                // 更新状态
                McpServerStatusEnum status = isHealthy ? McpServerStatusEnum.ACTIVE : McpServerStatusEnum.INACTIVE;
                String error = isHealthy ? null : "Health check failed";
                
                updateServerStatus(serverUid, isHealthy, error);
                updateEntityStatus(server, status, error);
                
                return isHealthy;
            } catch (Exception e) {
                log.error("Health check failed for server {}: {}", serverUid, e.getMessage(), e);
                updateServerStatus(serverUid, false, e.getMessage());
                updateEntityStatus(serverUid, McpServerStatusEnum.ERROR, e.getMessage());
                return false;
            }
        });
    }

    /**
     * 获取服务器状态
     */
    public ServerStatus getServerStatus(String serverUid) {
        return serverStatusCache.get(serverUid);
    }

    /**
     * 获取所有活跃的服务器
     */
    public List<String> getActiveServers() {
        return new ArrayList<>(clientCache.keySet());
    }

    // ============ 私有方法 ============

    private McpClient createMcpClient(McpServerEntity server) {
        try {
            McpTransport transport;
            
            // 根据协议创建不同的传输方式
            if (McpServerProtocolEnum.SSE.equals(server.getProtocol()) || 
                McpServerProtocolEnum.HTTP.equals(server.getProtocol()) || 
                McpServerProtocolEnum.HTTPS.equals(server.getProtocol())) {
                // 使用 SSE 传输
                WebClient webClient = webClientBuilder
                    .baseUrl(server.getServerUrl())
                    .defaultHeaders(headers -> {
                        if (StringUtils.hasText(server.getAuthToken())) {
                            String authType = server.getAuthType() != null ? 
                                server.getAuthType().getSchemePrefix() : McpServerAuthTypeEnum.BEARER.getSchemePrefix();
                            headers.set("Authorization", authType + server.getAuthToken());
                        }
                        // 添加额外的认证头
                        if (StringUtils.hasText(server.getAuthHeaders())) {
                            try {
                                Map<String, String> authHeaders = JSON.parseObject(server.getAuthHeaders(), Map.class);
                                authHeaders.forEach(headers::set);
                            } catch (Exception e) {
                                log.warn("Failed to parse auth headers for server {}: {}", server.getUid(), e.getMessage());
                            }
                        }
                    })
                    .build();
                
                transport = new SseServerSentEventMcpTransport(webClient);
            } else {
                log.warn("Unsupported protocol {} for server {}, using SSE as default", 
                    server.getProtocol(), server.getUid());
                
                WebClient webClient = webClientBuilder
                    .baseUrl(server.getServerUrl())
                    .build();
                transport = new SseServerSentEventMcpTransport(webClient);
            }

            return McpClient.using(transport)
                .requestTimeout(Duration.ofMillis(server.getReadTimeout()))
                .build();

        } catch (Exception e) {
            log.error("Failed to create MCP client for server {}: {}", server.getUid(), e.getMessage(), e);
            return null;
        }
    }

    private boolean initializeConnection(McpClient client, McpServerEntity server) {
        try {
            McpInitializeRequest initRequest = McpInitializeRequest.builder()
                .params(McpInitializeRequest.Params.builder()
                    .protocolVersion("2024-11-05")
                    .clientInfo(McpInitializeRequest.ClientInfo.builder()
                        .name("Bytedesk MCP Client")
                        .version("1.0.0")
                        .build())
                    .capabilities(McpInitializeRequest.ClientCapabilities.builder()
                        .roots(McpInitializeRequest.RootsCapability.builder()
                            .listChanged(true)
                            .build())
                        .sampling(new HashMap<>())
                        .build())
                    .build())
                .build();

            McpInitializeResult result = client.initialize(initRequest);
            
            log.info("MCP server {} initialized successfully. Server info: {}", 
                server.getUid(), result.getResult().getServerInfo());
            
            return true;
        } catch (Exception e) {
            log.error("Failed to initialize connection to MCP server {}: {}", server.getUid(), e.getMessage(), e);
            return false;
        }
    }

    private void loadServerCapabilities(String serverUid, McpClient client) {
        CompletableFuture.allOf(
            getAvailableTools(serverUid),
            getAvailableResources(serverUid),
            getAvailablePrompts(serverUid)
        ).thenRun(() -> {
            // 更新数据库中的能力信息
            try {
                ServerStatus status = serverStatusCache.get(serverUid);
                if (status != null) {
                    updateServerCapabilities(serverUid, status);
                }
            } catch (Exception e) {
                log.error("Failed to update server capabilities for {}: {}", serverUid, e.getMessage(), e);
            }
        });
    }

    private void updateServerCapabilities(String serverUid, ServerStatus status) {
        Optional<McpServerEntity> serverOpt = mcpServerRestService.findByUid(serverUid);
        if (serverOpt.isPresent()) {
            McpServerEntity server = serverOpt.get();
            
            // 更新能力信息
            if (!status.getTools().isEmpty()) {
                server.setAvailableTools(JSON.toJSONString(status.getTools()));
            }
            if (!status.getResources().isEmpty()) {
                server.setAvailableResources(JSON.toJSONString(status.getResources()));
            }
            if (!status.getPrompts().isEmpty()) {
                server.setAvailablePrompts(JSON.toJSONString(status.getPrompts()));
            }
            
            mcpServerRestService.save(server);
        }
    }

    private McpClient getOrCreateClient(String serverUid) {
        McpClient client = clientCache.get(serverUid);
        if (client == null) {
            // 尝试连接
            try {
                boolean connected = connectToServer(serverUid).get();
                if (connected) {
                    client = clientCache.get(serverUid);
                }
            } catch (Exception e) {
                log.error("Failed to create client for server {}: {}", serverUid, e.getMessage(), e);
            }
        }
        return client;
    }

    private void updateServerStatus(String serverUid, boolean connected, String error) {
        ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
        status.setConnected(connected);
        status.setLastConnected(connected ? ZonedDateTime.now() : status.getLastConnected());
        status.setLastError(error);
    }

    private void updateEntityStatus(McpServerEntity server, McpServerStatusEnum status, String error) {
        server.setStatus(status);
        server.setLastError(error);
        if (McpServerStatusEnum.ACTIVE.equals(status)) {
            server.setLastConnected(ZonedDateTime.now());
        }
        mcpServerRestService.save(server);
    }

    private void updateEntityStatus(String serverUid, McpServerStatusEnum status, String error) {
        Optional<McpServerEntity> serverOpt = mcpServerRestService.findByUid(serverUid);
        if (serverOpt.isPresent()) {
            updateEntityStatus(serverOpt.get(), status, error);
        }
    }

    private boolean performHttpHealthCheck(McpServerEntity server) {
        try {
            WebClient webClient = webClientBuilder
                .baseUrl(server.getHealthCheckUrl())
                .build();
                
            String response = webClient.get()
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(server.getConnectionTimeout()))
                .block();
                
            return StringUtils.hasText(response);
        } catch (Exception e) {
            log.debug("HTTP health check failed for server {}: {}", server.getUid(), e.getMessage());
            return false;
        }
    }
}
