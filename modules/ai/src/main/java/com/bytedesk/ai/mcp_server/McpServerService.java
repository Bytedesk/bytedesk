/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-26 11:17:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 12:23:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

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

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.alibaba.fastjson2.JSON;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP Server 客户端服务 - 简化版本
 * 负责与第三方 MCP 服务器通信，调用工具、获取资源、管理连接等
 * 
 * 注意：当前版本为简化实现，主要用于配置管理和接口定义
 * Spring AI 1.0.1 的 MCP 客户端 API 正在发展中，未来版本将提供完整的客户端功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpServerService {

    private final McpServerRestService mcpServerRestService;
    
    // WebClient 构建器用于 HTTP 连接
    private final WebClient.Builder webClientBuilder;

    // 服务器连接状态缓存
    private final Map<String, Boolean> connectionCache = new ConcurrentHashMap<>();
    
    // 服务器状态缓存
    private final Map<String, ServerStatus> serverStatusCache = new ConcurrentHashMap<>();

    /**
     * 服务器状态信息
     */
    @lombok.Data
    public static class ServerStatus {
        private boolean connected;
        private ZonedDateTime lastConnected;
        private String lastError;
        private List<String> tools;
        private List<String> resources;
        private List<String> prompts;

        public ServerStatus() {
            this.tools = new ArrayList<>();
            this.resources = new ArrayList<>();
            this.prompts = new ArrayList<>();
        }

        // Getters and setters
        public boolean isConnected() { return connected; }
        public void setConnected(boolean connected) { this.connected = connected; }
        public ZonedDateTime getLastConnected() { return lastConnected; }
        public void setLastConnected(ZonedDateTime lastConnected) { this.lastConnected = lastConnected; }
        public String getLastError() { return lastError; }
        public void setLastError(String lastError) { this.lastError = lastError; }
        public List<String> getTools() { return tools; }
        public void setTools(List<String> tools) { this.tools = tools; }
        public List<String> getResources() { return resources; }
        public void setResources(List<String> resources) { this.resources = resources; }
        public List<String> getPrompts() { return prompts; }
        public void setPrompts(List<String> prompts) { this.prompts = prompts; }
    }

    // ============ 公共接口方法 ============

    /**
     * 连接到 MCP 服务器
     */
    public CompletableFuture<Boolean> connectToServer(String serverUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<McpServerEntity> serverOpt = mcpServerRestService.findByUid(serverUid);
                if (serverOpt.isEmpty()) {
                    log.error("Server not found: {}", serverUid);
                    return false;
                }

                McpServerEntity server = serverOpt.get();

                // 检查是否已连接
                Boolean existingConnection = connectionCache.get(serverUid);
                if (Boolean.TRUE.equals(existingConnection)) {
                    log.info("Already connected to MCP server: {}", serverUid);
                    return true;
                }

                // 执行连接逻辑（当前为模拟实现）
                boolean connected = performConnection(server);
                
                if (connected) {
                    connectionCache.put(serverUid, true);
                    updateServerStatus(serverUid, true, null);
                    updateEntityStatus(server, McpServerStatusEnum.ACTIVE, null);
                    
                    // 异步加载服务器能力
                    loadServerCapabilities(serverUid);
                    
                    log.info("Connected to MCP server: {}", serverUid);
                    return true;
                } else {
                    updateServerStatus(serverUid, false, "Connection failed");
                    updateEntityStatus(server, McpServerStatusEnum.ERROR, "Connection failed");
                    return false;
                }
            } catch (Exception e) {
                log.error("Failed to connect to MCP server {}: {}", serverUid, e.getMessage(), e);
                updateServerStatus(serverUid, false, e.getMessage());
                updateEntityStatus(serverUid, McpServerStatusEnum.ERROR, e.getMessage());
                return false;
            }
        });
    }

    /**
     * 断开与指定MCP服务器的连接
     */
    public CompletableFuture<Void> disconnectFromServer(String serverUid) {
        return CompletableFuture.runAsync(() -> {
            try {
                connectionCache.remove(serverUid);
                log.info("Disconnected from MCP server: {}", serverUid);
            } catch (Exception e) {
                log.error("Error disconnecting from MCP server {}: {}", serverUid, e.getMessage());
                throw new RuntimeException("Failed to disconnect from MCP server", e);
            }
        });
    }    /**
     * 调用 MCP 工具
     */
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public CompletableFuture<ToolCallResponse> callTool(ToolCallRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Boolean connected = connectionCache.get(request.getServerUid());
                if (!Boolean.TRUE.equals(connected)) {
                    // 尝试连接
                    boolean connectionResult = connectToServer(request.getServerUid()).get();
                    if (!connectionResult) {
                        return new ToolCallResponse(false, null, "Failed to connect to MCP server");
                    }
                }

                // 当前为模拟实现 - 实际实现需要使用 MCP 协议
                Map<String, Object> mockResult = new HashMap<>();
                mockResult.put("tool", request.getToolName());
                mockResult.put("arguments", request.getArguments());
                mockResult.put("result", "Tool call simulated - actual implementation pending");
                mockResult.put("timestamp", ZonedDateTime.now().toString());
                
                log.debug("Tool call simulated for {}: {}", request.getToolName(), mockResult);
                return new ToolCallResponse(true, mockResult, null);

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
                Boolean connected = connectionCache.get(serverUid);
                if (!Boolean.TRUE.equals(connected)) {
                    return Collections.emptyList();
                }

                // 当前为模拟实现 - 返回示例工具列表
                List<McpTool> tools = List.of(
                    new McpTool("file_reader", "Read files from the server"),
                    new McpTool("web_search", "Search the web for information"), 
                    new McpTool("calculator", "Perform mathematical calculations"),
                    new McpTool("weather_api", "Get weather information")
                );
                
                // 更新缓存
                ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
                List<String> toolNames = tools.stream().map(McpTool::getName).toList();
                status.setTools(toolNames);
                
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
                Boolean connected = connectionCache.get(serverUid);
                if (!Boolean.TRUE.equals(connected)) {
                    return Collections.emptyList();
                }

                // 当前为模拟实现 - 返回示例资源列表
                List<McpResource> resources = List.of(
                    new McpResource("file://documents/", "documents", "Document directory"),
                    new McpResource("file://images/", "images", "Image directory"),
                    new McpResource("file://config.json", "config.json", "Configuration file"),
                    new McpResource("file://api_specs.yaml", "api_specs.yaml", "API specifications")
                );
                
                // 更新缓存
                ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
                List<String> resourceNames = resources.stream().map(McpResource::getName).toList();
                status.setResources(resourceNames);
                
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
                Boolean connected = connectionCache.get(serverUid);
                if (!Boolean.TRUE.equals(connected)) {
                    return Collections.emptyList();
                }

                // 当前为模拟实现 - 返回示例提示列表
                List<McpPrompt> prompts = List.of(
                    new McpPrompt("summarize_text", "Summarize text content", "Please summarize: {text}"),
                    new McpPrompt("generate_code", "Generate code based on requirements", "Generate {language} code for: {requirements}"),
                    new McpPrompt("analyze_data", "Analyze data and provide insights", "Analyze this data: {data}"),
                    new McpPrompt("write_email", "Write professional emails", "Write an email about: {topic}")
                );
                
                // 更新缓存
                ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
                List<String> promptNames = prompts.stream().map(McpPrompt::getName).toList();
                status.setPrompts(promptNames);
                
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
                Boolean connected = connectionCache.get(serverUid);
                if (!Boolean.TRUE.equals(connected)) {
                    return null;
                }

                // 当前为模拟实现
                Map<String, Object> mockContent = new HashMap<>();
                mockContent.put("uri", resourceUri);
                mockContent.put("content", "Resource content simulation - actual implementation pending");
                mockContent.put("mimeType", "text/plain");
                mockContent.put("size", 1024);
                
                return mockContent;
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
                Boolean connected = connectionCache.get(serverUid);
                if (!Boolean.TRUE.equals(connected)) {
                    return null;
                }

                // 当前为模拟实现
                Map<String, Object> mockPrompt = new HashMap<>();
                mockPrompt.put("name", promptName);
                mockPrompt.put("arguments", arguments);
                mockPrompt.put("content", "Prompt content simulation - actual implementation pending");
                mockPrompt.put("generated", ZonedDateTime.now().toString());
                
                return mockPrompt;
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
                Boolean connected = connectionCache.get(serverUid);
                boolean isHealthy = Boolean.TRUE.equals(connected);
                
                // 如果有健康检查 URL，使用它
                String serverConfigJson = server.getServerConfig();
                if (StringUtils.hasText(serverConfigJson)) {
                    // 从 serverConfig JSON 中解析健康检查 URL
                    @SuppressWarnings("unchecked")
                    Map<String, Object> config = JSON.parseObject(serverConfigJson, Map.class);
                    String healthCheckUrl = (String) config.get("healthCheckUrl");
                    if (StringUtils.hasText(healthCheckUrl)) {
                        isHealthy = performHttpHealthCheck(server, healthCheckUrl);
                    }
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
        return connectionCache.entrySet().stream()
            .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .toList();
    }

    // ============ 私有方法 ============

    /**
     * 执行实际连接操作（当前为模拟实现）
     */
    private boolean performConnection(McpServerEntity server) {
        try {
            // 当前为模拟实现 - 检查服务器配置是否有效
            String serverConfigJson = server.getServerConfig();
            if (!StringUtils.hasText(serverConfigJson)) {
                return false;
            }
            
            // 解析配置
            @SuppressWarnings("unchecked")
            Map<String, Object> config = JSON.parseObject(serverConfigJson, Map.class);
            String serverUrl = (String) config.get("serverUrl");
            if (!StringUtils.hasText(serverUrl)) {
                return false;
            }
            
            // 可以添加简单的 HTTP 检查
            String healthCheckUrl = (String) config.get("healthCheckUrl");
            if (StringUtils.hasText(healthCheckUrl)) {
                return performHttpHealthCheck(server, healthCheckUrl);
            }
            
            // 如果没有健康检查 URL，假设连接成功
            return true;
            
        } catch (Exception e) {
            log.error("Connection to MCP server {} failed: {}", server.getUid(), e.getMessage(), e);
            return false;
        }
    }

    private void loadServerCapabilities(String serverUid) {
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
            
            // 更新 serverConfig 中的能力信息
            String configJson = server.getServerConfig();
            Map<String, Object> config;
            if (StringUtils.hasText(configJson)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> parsedConfig = JSON.parseObject(configJson, Map.class);
                config = parsedConfig;
            } else {
                config = new HashMap<>();
            }
            
            // 更新能力信息
            if (!status.getTools().isEmpty()) {
                config.put("availableTools", status.getTools());
            }
            if (!status.getResources().isEmpty()) {
                config.put("availableResources", status.getResources());
            }
            if (!status.getPrompts().isEmpty()) {
                config.put("availablePrompts", status.getPrompts());
            }
            
            server.setServerConfig(JSON.toJSONString(config));
            mcpServerRestService.save(server);
        }
    }

    private void updateServerStatus(String serverUid, boolean connected, String error) {
        ServerStatus status = serverStatusCache.computeIfAbsent(serverUid, k -> new ServerStatus());
        status.setConnected(connected);
        status.setLastConnected(connected ? ZonedDateTime.now() : status.getLastConnected());
        status.setLastError(error);
    }

    private void updateEntityStatus(McpServerEntity server, McpServerStatusEnum status, String error) {
        server.setStatus(status.name());
        // server.setLastError(error);
        // if (McpServerStatusEnum.ACTIVE.equals(status)) {
        //     server.setLastConnected(ZonedDateTime.now());
        // }
        mcpServerRestService.save(server);
    }

    private void updateEntityStatus(String serverUid, McpServerStatusEnum status, String error) {
        Optional<McpServerEntity> serverOpt = mcpServerRestService.findByUid(serverUid);
        if (serverOpt.isPresent()) {
            updateEntityStatus(serverOpt.get(), status, error);
        }
    }

    private boolean performHttpHealthCheck(McpServerEntity server, String healthCheckUrl) {
        try {
            // 从 serverConfig 中获取连接超时配置
            String configJson = server.getServerConfig();
            int connectionTimeout = 30000; // 默认值
            
            if (StringUtils.hasText(configJson)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> config = JSON.parseObject(configJson, Map.class);
                Object timeoutObj = config.get("connectionTimeout");
                if (timeoutObj instanceof Integer) {
                    connectionTimeout = (Integer) timeoutObj;
                }
            }
            
            WebClient webClient = webClientBuilder
                .baseUrl(healthCheckUrl)
                .build();
                
            String response = webClient.get()
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(connectionTimeout))
                .block();
                
            return StringUtils.hasText(response);
        } catch (Exception e) {
            log.debug("HTTP health check failed for server {}: {}", server.getUid(), e.getMessage());
            return false;
        }
    }
}
