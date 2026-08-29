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
package com.bytedesk.ai.mcp;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP Server 测试控制器（HTTP 薄层）
 * <p>
 * 演示微语自身作为 MCP Server（{@code spring.ai.mcp.server.*}）对外暴露的能力：
 * </p>
 * <ul>
 *   <li>{@code GET /info}：服务器配置概览（名称、版本、协议、端点、认证等）</li>
 *   <li>{@code GET /tools}：列出对外暴露的 MCP 工具（经 ToolEntity 注册表管理）</li>
 *   <li>{@code POST /tools/call}：进程内直调已暴露工具回调，模拟外部 MCP 客户端调用</li>
 * </ul>
 *
 * <p>本控制器仅负责 HTTP 参数绑定与 {@code bytedesk.debug} 开关校验，
 * 真正的配置读取、工具收集与回调调用均委托给 {@link McpServerTestService}。</p>
 *
 * <p>外部 MCP 客户端（如 mcp-inspector、Claude 等）可通过 STREAMABLE 协议端点
 * {@code /mcp} 连接本服务器；若开启 {@code bytedesk.ai.mcp.auth.enabled}，
 * 需携带 {@code Authorization: Bearer <token>} 头。</p>
 *
 * @see McpServerTestService
 * @see McpClientTestController
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/mcp/server")
@RequiredArgsConstructor
public class McpServerTestController {

    private final McpServerTestService mcpServerTestService;

    private final BytedeskProperties bytedeskProperties;

    //
    // ============ 请求体定义 ============
    //

    /**
     * 调用已暴露工具请求体
     */
    public record CallToolBody(
            String toolName,
            Map<String, Object> arguments) {
    }

    //
    // ============ 服务器信息 ============
    //

    /**
     * MCP Server 配置概览
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/server/info
     */
    @GetMapping("/info")
    public ResponseEntity<JsonResult<?>> serverInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpServerTestService.serverInfo()));
        } catch (Exception e) {
            log.error("Failed to get MCP server info: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
        }
    }

    //
    // ============ Tools 工具 ============
    //

    /**
     * 列出 bytedesk MCP Server 对外暴露的工具
     * GET http://127.0.0.1:9003/spring/ai/api/v1/mcp/server/tools
     */
    @GetMapping("/tools")
    public ResponseEntity<JsonResult<?>> listExposedTools() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(mcpServerTestService.listExposedTools()));
        } catch (Exception e) {
            log.error("Failed to list exposed MCP tools: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed: " + e.getMessage()));
        }
    }

    /**
     * 直接调用本地已暴露的 MCP 工具回调（进程内直调，无网络往返）
     * POST http://127.0.0.1:9003/spring/ai/api/v1/mcp/server/tools/call
     *
     * 请求体示例：
     * {
     *   "toolName": "bytedeskKnowledgeSearch",
     *   "arguments": { "query": "如何配置客服" }
     * }
     */
    @PostMapping("/tools/call")
    public ResponseEntity<JsonResult<?>> callExposedTool(@RequestBody CallToolBody body) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        try {
            return ResponseEntity.ok(JsonResult.success(
                    mcpServerTestService.callExposedTool(body.toolName(), body.arguments())));
        } catch (Exception e) {
            log.error("Failed to call exposed MCP tool: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Call tool failed: " + e.getMessage()));
        }
    }
}
