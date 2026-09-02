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
package com.bytedesk.ai.mcp_client;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.mcp_server.McpServerTestController;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP Client 测试控制器（HTTP 薄层）
 * <p>
 * 演示微语作为 MCP 客户端连接外部 MCP 服务器的能力，参考
 * spring-ai-examples/model-context-protocol 中客户端示例（brave 相关除外）：
 * </p>
 * <ul>
 *   <li>client-starter: ChatClient + ToolCallbackProvider，大模型自动调用 MCP tools（/chat）</li>
 *   <li>SampleClient: ping、listTools、callTool、prompts、resources、complete 等底层 McpSyncClient API</li>
 * </ul>
 *
 * <p>本控制器仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，
 * 真正的客户端解析、MCP 协议调用与 LLM 编排均委托给 {@link McpClientTestService}。</p>
 *
 * <p>测试前提：{@code bytedesk.debug=true}，且启用任一 MCP 客户端来源：
 * 管理后台 McpServerEntity（bytedesk.ai.mcp.client.enabled=true）或
 * properties 中 spring.ai.mcp.client.* 连接。</p>
 *
 * @see McpClientTestService
 * @see McpServerTestController
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/mcp/client")
@RequiredArgsConstructor
public class McpClientTestController {

    private final McpClientTestService mcpClientTestService;

    private final BytedeskProperties bytedeskProperties;

    //
    // ============ 请求体定义 ============
    //

    /**
     * 调用工具请求体
     */
    public record CallToolBody(
            String client,
            String toolName,
            Map<String, Object> arguments,
            Object progressToken) {
    }

    /**
     * 获取提示词请求体
     */
    public record GetPromptBody(
            String client,
            String promptName,
            Map<String, Object> arguments) {
    }

    /**
     * 读取资源请求体
     */
    public record ReadResourceBody(
            String client,
            String uri) {
    }

    /**
     * MCP 工具对话请求体
     */
    public record ChatWithToolsBody(
            String message,
            String client) {
    }

    //
    // ============ 客户端管理 ============
    //

    /**
     * 列出所有 MCP 客户端（连接的外部 MCP 服务器）
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/clients
     */
    @GetMapping("/clients")
    public ResponseEntity<JsonResult<?>> listClients() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            List<Map<String, Object>> clients = mcpClientTestService.listClients();
            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "total", clients.size(),
                    "clients", clients)));
        } catch (Exception e) {
            log.error("Failed to list MCP clients: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
        }
    }

    /**
     * 刷新 bytedesk MCP 客户端连接（从数据库重新加载 McpServerEntity 配置）
     * POST http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/clients/refresh
     */
    @PostMapping("/clients/refresh")
    public ResponseEntity<JsonResult<?>> refreshClients() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.refreshClients()));
        } catch (Exception e) {
            log.error("Failed to refresh MCP clients: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Refresh failed: " + e.getMessage()));
        }
    }

    /**
     * ping 测试客户端连通性
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/ping
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/ping?client=deepthinking
     */
    @GetMapping("/ping")
    public ResponseEntity<JsonResult<?>> ping(@RequestParam(value = "client", required = false) String client) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.ping(client)));
        } catch (Exception e) {
            log.error("Failed to ping MCP clients: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Ping failed: " + e.getMessage()));
        }
    }

    //
    // ============ Tools 工具 ============
    //

    /**
     * 列出 MCP 服务器提供的工具
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/tools
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/tools?client=deepthinking
     */
    @GetMapping("/tools")
    public ResponseEntity<JsonResult<?>> listTools(@RequestParam(value = "client", required = false) String client) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.listTools(client)));
        } catch (Exception e) {
            log.error("Failed to list MCP tools: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
        }
    }

    /**
     * 调用 MCP 服务器上的工具
     * POST http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/tools/call
     *
     * 请求体示例：
     * {
     *   "client": "deepthinking",
     *   "toolName": "getTemperature",
     *   "arguments": { "latitude": "47.6062", "longitude": "-122.3321", "city": "Seattle" },
     *   "progressToken": 666
     * }
     */
    @PostMapping("/tools/call")
    public ResponseEntity<JsonResult<?>> callTool(@RequestBody CallToolBody body) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.callTool(
                    body.client(), body.toolName(), body.arguments(), body.progressToken())));
        } catch (Exception e) {
            log.error("Failed to call MCP tool: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Call tool failed: " + e.getMessage()));
        }
    }

    //
    // ============ Prompts 提示词 ============
    //

    /**
     * 列出 MCP 服务器提供的提示词模板
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/prompts
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/prompts?client=deepthinking
     */
    @GetMapping("/prompts")
    public ResponseEntity<JsonResult<?>> listPrompts(@RequestParam(value = "client", required = false) String client) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.listPrompts(client)));
        } catch (Exception e) {
            log.error("Failed to list MCP prompts: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
        }
    }

    /**
     * 获取（渲染）指定提示词模板
     * POST http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/prompts/get
     *
     * 请求体示例：
     * {
     *   "client": "deepthinking",
     *   "promptName": "personalized-message",
     *   "arguments": { "name": "Alice", "age": "14", "interests": "AI" }
     * }
     */
    @PostMapping("/prompts/get")
    public ResponseEntity<JsonResult<?>> getPrompt(@RequestBody GetPromptBody body) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.getPrompt(
                    body.client(), body.promptName(), body.arguments())));
        } catch (Exception e) {
            log.error("Failed to get MCP prompt: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Get prompt failed: " + e.getMessage()));
        }
    }

    //
    // ============ Resources 资源 ============
    //

    /**
     * 列出 MCP 服务器提供的资源
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/resources
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/resources?client=deepthinking
     */
    @GetMapping("/resources")
    public ResponseEntity<JsonResult<?>> listResources(
            @RequestParam(value = "client", required = false) String client) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.listResources(client)));
        } catch (Exception e) {
            log.error("Failed to list MCP resources: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
        }
    }

    /**
     * 读取指定资源内容
     * POST http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/resources/read
     *
     * 请求体示例：
     * {
     *   "client": "deepthinking",
     *   "uri": "user-status://alice"
     * }
     */
    @PostMapping("/resources/read")
    public ResponseEntity<JsonResult<?>> readResource(@RequestBody ReadResourceBody body) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(
                    mcpClientTestService.readResource(body.client(), body.uri())));
        } catch (Exception e) {
            log.error("Failed to read MCP resource: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Read resource failed: " + e.getMessage()));
        }
    }

    //
    // ============ Completion 自动补全 ============
    //

    /**
     * 提示词/资源参数自动补全
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/complete?refType=prompt&ref=personalized-message&argumentName=name&argumentValue=J&client=deepthinking
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/complete?refType=resource&ref=user-status://%7Busername%7D&argumentName=username&argumentValue=a
     */
    @GetMapping("/complete")
    public ResponseEntity<JsonResult<?>> completeCompletion(
            @RequestParam(value = "refType", defaultValue = "prompt") String refType,
            @RequestParam(value = "ref") String ref,
            @RequestParam(value = "argumentName") String argumentName,
            @RequestParam(value = "argumentValue") String argumentValue,
            @RequestParam(value = "client", required = false) String client) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpClientTestService.completeCompletion(
                    refType, ref, argumentName, argumentValue, client)));
        } catch (Exception e) {
            log.error("Failed to complete MCP completion: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Complete failed: " + e.getMessage()));
        }
    }

    //
    // ============ Chat 对话（LLM + MCP Tools）============
    //

    /**
     * 大模型对话 + MCP 工具自动调用
     * 参考 client-starter 示例：ChatClient + ToolCallbackProvider，大模型自动决策调用 MCP 工具
     * POST http://127.0.0.1:9003/spring/ai/api/v1/mcp/client/chat
     *
     * 请求体示例：
     * {
     *   "message": "What tools are available? 请列出可用的工具",
     *   "client": "deepthinking"  // 可选，不传则使用全部客户端的工具
     * }
     */
    @PostMapping("/chat")
    public ResponseEntity<JsonResult<?>> chatWithTools(@RequestBody ChatWithToolsBody body) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(
                    mcpClientTestService.chatWithTools(body.message(), body.client())));
        } catch (Exception e) {
            log.error("Error in MCP chat: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Chat failed: " + e.getMessage()));
        }
    }
}
