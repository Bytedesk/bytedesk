/*
 * @Author: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-26 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.tool.utils.RegistryManagedToolCallbackCollector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP Server 测试服务
 * <p>
 * 演示 bytedesk 自身作为 MCP Server（{@code spring.ai.mcp.server.*}）对外暴露的能力：
 * </p>
 * <ul>
 *   <li>服务器配置概览：名称、版本、协议、端点、认证等（读取 Environment 中
 *       {@code spring.ai.mcp.server.*} 与 {@code bytedesk.ai.mcp.*} 配置）</li>
 *   <li>列出对外暴露的 MCP 工具：经 {@link RegistryManagedToolCallbackCollector} 收集，
 *       由 ToolEntity.enabled / mcpExposureMode / allowedMethods 决定暴露范围</li>
 *   <li>直接调用本地已暴露工具回调：模拟 MCP 客户端经协议调用工具的效果（无网络往返），
 *       便于联调时快速验证工具实现</li>
 * </ul>
 *
 * <p>{@link McpServerTestController} 仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，
 * 真正的配置读取、工具收集与回调调用均在本服务中完成。</p>
 *
 * <p>外部 MCP 客户端（如 mcp-inspector、Claude 等）可通过 STREAMABLE 协议端点
 * {@code /mcp} 连接本服务器；若开启 {@code bytedesk.ai.mcp.auth.enabled}，
 * 需携带 {@code Authorization: Bearer <token>} 头。</p>
 *
 * @see McpServerTestController
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpServerTestService {

    private static final String SERVER_PREFIX = "spring.ai.mcp.server";
    private static final String AUTH_PREFIX = "bytedesk.ai.mcp.auth";

    private final ObjectProvider<RegistryManagedToolCallbackCollector> collectorProvider;

    private final Environment environment;

    //
    // ============ 服务器信息 ============
    //

    /**
     * MCP Server 配置概览
     * <p>
     * 汇总 {@code spring.ai.mcp.server.*}（Spring AI MCP Server 自动配置）与
     * {@code bytedesk.ai.mcp.*}（bytedesk 工具桥/认证）两部分配置。
     * </p>
     */
    public Map<String, Object> serverInfo() {
        Map<String, Object> server = new LinkedHashMap<>();
        server.put("enabled", environment.getProperty(SERVER_PREFIX + ".enabled", Boolean.class, false));
        server.put("name", environment.getProperty(SERVER_PREFIX + ".name", "bytedesk-mcp-server"));
        server.put("version", environment.getProperty(SERVER_PREFIX + ".version", "unknown"));
        server.put("type", environment.getProperty(SERVER_PREFIX + ".type", "SYNC"));
        server.put("protocol", environment.getProperty(SERVER_PREFIX + ".protocol", "STREAMABLE"));
        server.put("stdio", environment.getProperty(SERVER_PREFIX + ".stdio", Boolean.class, false));
        server.put("sseMessageEndpoint", environment.getProperty(SERVER_PREFIX + ".sse-message-endpoint", "/mcp/message"));
        server.put("exposeMcpClientTools",
                environment.getProperty(SERVER_PREFIX + ".expose-mcp-client-tools", Boolean.class, false));
        server.put("toolChangeNotification",
                environment.getProperty(SERVER_PREFIX + ".tool-change-notification", Boolean.class, false));
        server.put("resourceChangeNotification",
                environment.getProperty(SERVER_PREFIX + ".resource-change-notification", Boolean.class, false));
        server.put("promptChangeNotification",
                environment.getProperty(SERVER_PREFIX + ".prompt-change-notification", Boolean.class, false));

        // STREAMABLE 协议对外端点固定为 /mcp，外部客户端通过该端点连接
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("streamableHttp", "/mcp");
        server.put("endpoint", endpoint);

        // bytedesk 工具桥：注册表管理的工具暴露开关
        Map<String, Object> toolsBridge = new LinkedHashMap<>();
        toolsBridge.put("enabled",
                environment.getProperty("bytedesk.ai.mcp.tools.enabled", Boolean.class, true));
        server.put("toolsBridge", toolsBridge);

        // bytedesk MCP 认证配置（token 脱敏展示）
        Map<String, Object> auth = new LinkedHashMap<>();
        auth.put("enabled", environment.getProperty(AUTH_PREFIX + ".enabled", Boolean.class, false));
        auth.put("bearerToken", maskToken(environment.getProperty(AUTH_PREFIX + ".bearer-token")));
        server.put("auth", auth);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("server", server);
        return result;
    }

    //
    // ============ Tools 工具 ============
    //

    /**
     * 列出 bytedesk MCP Server 对外暴露的工具
     * <p>
     * 暴露范围由 ToolEntity.enabled、ToolEntity.mcpExposureMode、
     * ToolEntity.allowedMethods 在平台同步后决定。
     * </p>
     */
    public Map<String, Object> listExposedTools() {
        List<Map<String, Object>> tools = new ArrayList<>();
        for (ToolCallback callback : exposedCallbacks()) {
            var definition = callback.getToolDefinition();
            if (definition == null || !StringUtils.hasText(definition.name())) {
                continue;
            }
            Map<String, Object> toolView = new LinkedHashMap<>();
            toolView.put("name", definition.name());
            toolView.put("description", definition.description());
            toolView.put("inputSchema", definition.inputSchema());
            tools.add(toolView);
        }
        return Map.of(
                "total", tools.size(),
                "tools", tools);
    }

    /**
     * 直接调用本地已暴露的 MCP 工具回调
     * <p>
     * 模拟外部 MCP 客户端经协议调用工具的效果（进程内直调，无网络往返），
     * 便于联调时快速验证工具实现。外部真实调用走 {@code POST /mcp} 端点。
     * </p>
     *
     * @param toolName  工具名称（见 listExposedTools）
     * @param arguments 工具入参，将被序列化为 JSON 字符串传给回调
     */
    public Map<String, Object> callExposedTool(String toolName, Map<String, Object> arguments) {
        if (!StringUtils.hasText(toolName)) {
            throw new IllegalArgumentException("toolName is required");
        }

        ToolCallback target = null;
        for (ToolCallback callback : exposedCallbacks()) {
            if (callback.getToolDefinition() != null && toolName.equals(callback.getToolDefinition().name())) {
                target = callback;
                break;
            }
        }
        if (target == null) {
            throw new IllegalArgumentException(
                    "Exposed MCP tool not found: " + toolName + ". Check /spring/ai/api/v1/mcp/server/tools");
        }

        String toolInput = arguments != null && !arguments.isEmpty() ? JSON.toJSONString(arguments) : "{}";
        String output = target.call(toolInput);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tool", toolName);
        response.put("arguments", arguments);
        response.put("result", output);
        return response;
    }

    //
    // ============ 私有辅助方法 ============
    //

    /**
     * 收集当前对外暴露的 MCP 工具回调
     * 未启用工具桥（bytedesk.ai.mcp.tools.enabled=false）时返回空数组
     */
    private ToolCallback[] exposedCallbacks() {
        RegistryManagedToolCallbackCollector collector = collectorProvider.getIfAvailable();
        if (collector == null) {
            return new ToolCallback[0];
        }
        return collector.collectMcpExposedCallbacks();
    }

    /**
     * 脱敏展示 bearer token：仅保留前 8 个字符
     */
    private String maskToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return token.length() <= 8 ? "****" : token.substring(0, 8) + "****";
    }
}
