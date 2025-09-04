/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 13:45:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// 使用本地定义的类替代 Spring AI MCP 中缺失的类
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.ai.mcp_server.McpServerService.ServerStatus;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/mcp/server")
@AllArgsConstructor
@Tag(name = "McpServer Management", description = "McpServer management APIs for organizing and categorizing content with mcpServers")
@Description("McpServer Management Controller - Content mcpServer management and categorization APIs")
public class McpServerRestController extends BaseRestController<McpServerRequest, McpServerRestService> {

    private final McpServerRestService mcpServerRestService;
    private final McpServerService mcpServerService;

    @ActionAnnotation(title = "McpServer", action = "组织查询", description = "query mcpServer by org")
    @Operation(summary = "Query McpServers by Organization", description = "Retrieve mcpServers for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(McpServerRequest request) {
        
        Page<McpServerResponse> mcpServers = mcpServerRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(mcpServers));
    }

    @ActionAnnotation(title = "McpServer", action = "用户查询", description = "query mcpServer by user")
    @Operation(summary = "Query McpServers by User", description = "Retrieve mcpServers for the current user")
    @Override
    public ResponseEntity<?> queryByUser(McpServerRequest request) {
        
        Page<McpServerResponse> mcpServers = mcpServerRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(mcpServers));
    }

    @ActionAnnotation(title = "McpServer", action = "查询详情", description = "query mcpServer by uid")
    @Operation(summary = "Query McpServer by UID", description = "Retrieve a specific mcpServer by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(McpServerRequest request) {
        
        McpServerResponse mcpServer = mcpServerRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(mcpServer));
    }

    @ActionAnnotation(title = "McpServer", action = "新建", description = "create mcpServer")
    @Operation(summary = "Create McpServer", description = "Create a new mcpServer")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(McpServerRequest request) {
        
        McpServerResponse mcpServer = mcpServerRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(mcpServer));
    }

    @ActionAnnotation(title = "McpServer", action = "更新", description = "update mcpServer")
    @Operation(summary = "Update McpServer", description = "Update an existing mcpServer")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(McpServerRequest request) {
        
        McpServerResponse mcpServer = mcpServerRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(mcpServer));
    }

    @ActionAnnotation(title = "McpServer", action = "删除", description = "delete mcpServer")
    @Operation(summary = "Delete McpServer", description = "Delete a mcpServer")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(McpServerRequest request) {
        
        mcpServerRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "McpServer", action = "导出", description = "export mcpServer")
    @Operation(summary = "Export McpServers", description = "Export mcpServers to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(McpServerRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            mcpServerRestService,
            McpServerExcel.class,
            "McpServer",
            "mcpServer"
        );
    }

    // ============ MCP 服务器操作接口 ============

    @ActionAnnotation(title = "McpServer", action = "连接服务器", description = "connect to MCP server")
    @Operation(summary = "Connect to MCP Server", description = "Establish connection to a MCP server")
    @PostMapping("/connect/{serverUid}")
    public ResponseEntity<?> connectToServer(@PathVariable String serverUid) {
        CompletableFuture<Boolean> future = mcpServerService.connectToServer(serverUid);
        
        try {
            Boolean connected = future.get();
            if (connected) {
                return ResponseEntity.ok(JsonResult.success("Connected to MCP server successfully"));
            } else {
                return ResponseEntity.ok(JsonResult.error("Failed to connect to MCP server"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Connection failed: " + e.getMessage()));
        }
    }

    // @ActionAnnotation(title = "McpServer", action = "断开连接", description = "disconnect from MCP server")
    // @Operation(summary = "Disconnect from MCP Server", description = "Disconnect from a MCP server")
    // @PostMapping("/disconnect/{serverUid}")
    // public ResponseEntity<?> disconnectFromServer(@PathVariable String serverUid) {
    //     CompletableFuture<Boolean> future = mcpServerService.disconnectFromServer(serverUid);
        
    //     try {
    //         Boolean disconnected = future.get();
    //         if (disconnected) {
    //             return ResponseEntity.ok(JsonResult.success("Disconnected from MCP server successfully"));
    //         } else {
    //             return ResponseEntity.ok(JsonResult.error("Failed to disconnect from MCP server"));
    //         }
    //     } catch (Exception e) {
    //         return ResponseEntity.ok(JsonResult.error("Disconnection failed: " + e.getMessage()));
    //     }
    // }

    @ActionAnnotation(title = "McpServer", action = "调用工具", description = "call MCP tool")
    @Operation(summary = "Call MCP Tool", description = "Execute a tool on the MCP server")
    @PostMapping("/call-tool")
    public ResponseEntity<?> callTool(@RequestBody ToolCallRequest request) {
        CompletableFuture<ToolCallResponse> future = mcpServerService.callTool(request);
        
        try {
            ToolCallResponse response = future.get();
            if (response.isSuccess()) {
                return ResponseEntity.ok(JsonResult.success(response.getData()));
            } else {
                return ResponseEntity.ok(JsonResult.error("Tool call failed: " + response.getErrorMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Tool call failed: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "获取工具列表", description = "get available tools")
    @Operation(summary = "Get Available Tools", description = "Retrieve list of available tools from MCP server")
    @GetMapping("/tools/{serverUid}")
    public ResponseEntity<?> getAvailableTools(@PathVariable String serverUid) {
        CompletableFuture<List<McpTool>> future = mcpServerService.getAvailableTools(serverUid);
        
        try {
            List<McpTool> tools = future.get();
            return ResponseEntity.ok(JsonResult.success(tools));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Failed to get tools: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "获取资源列表", description = "get available resources")
    @Operation(summary = "Get Available Resources", description = "Retrieve list of available resources from MCP server")
    @GetMapping("/resources/{serverUid}")
    public ResponseEntity<?> getAvailableResources(@PathVariable String serverUid) {
        CompletableFuture<List<McpResource>> future = mcpServerService.getAvailableResources(serverUid);
        
        try {
            List<McpResource> resources = future.get();
            return ResponseEntity.ok(JsonResult.success(resources));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Failed to get resources: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "获取提示列表", description = "get available prompts")
    @Operation(summary = "Get Available Prompts", description = "Retrieve list of available prompts from MCP server")
    @GetMapping("/prompts/{serverUid}")
    public ResponseEntity<?> getAvailablePrompts(@PathVariable String serverUid) {
        CompletableFuture<List<McpPrompt>> future = mcpServerService.getAvailablePrompts(serverUid);
        
        try {
            List<McpPrompt> prompts = future.get();
            return ResponseEntity.ok(JsonResult.success(prompts));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Failed to get prompts: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "读取资源", description = "read resource content")
    @Operation(summary = "Read Resource", description = "Read content of a specific resource from MCP server")
    @GetMapping("/resource/{serverUid}")
    public ResponseEntity<?> readResource(
            @PathVariable String serverUid,
            @RequestParam String resourceUri) {
        CompletableFuture<Object> future = mcpServerService.readResource(serverUid, resourceUri);
        
        try {
            Object content = future.get();
            if (content != null) {
                return ResponseEntity.ok(JsonResult.success(content));
            } else {
                return ResponseEntity.ok(JsonResult.error("Resource not found or failed to read"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Failed to read resource: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "获取提示内容", description = "get prompt content")
    @Operation(summary = "Get Prompt", description = "Get content of a specific prompt from MCP server")
    @PostMapping("/prompt/{serverUid}/{promptName}")
    public ResponseEntity<?> getPrompt(
            @PathVariable String serverUid,
            @PathVariable String promptName,
            @RequestBody(required = false) Map<String, Object> arguments) {
        CompletableFuture<Object> future = mcpServerService.getPrompt(serverUid, promptName, arguments);
        
        try {
            Object content = future.get();
            if (content != null) {
                return ResponseEntity.ok(JsonResult.success(content));
            } else {
                return ResponseEntity.ok(JsonResult.error("Prompt not found or failed to get"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Failed to get prompt: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "健康检查", description = "health check")
    @Operation(summary = "Health Check", description = "Check health status of MCP server")
    @GetMapping("/health/{serverUid}")
    public ResponseEntity<?> healthCheck(@PathVariable String serverUid) {
        CompletableFuture<Boolean> future = mcpServerService.healthCheck(serverUid);
        
        try {
            Boolean isHealthy = future.get();
            if (isHealthy) {
                return ResponseEntity.ok(JsonResult.success("Server is healthy"));
            } else {
                return ResponseEntity.ok(JsonResult.error("Server is unhealthy"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Health check failed: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "获取服务器状态", description = "get server status")
    @Operation(summary = "Get Server Status", description = "Get detailed status of MCP server")
    @GetMapping("/status/{serverUid}")
    public ResponseEntity<?> getServerStatus(@PathVariable String serverUid) {
        try {
            ServerStatus status = mcpServerService.getServerStatus(serverUid);
            if (status != null) {
                return ResponseEntity.ok(JsonResult.success(status));
            } else {
                return ResponseEntity.ok(JsonResult.error("Server status not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Failed to get server status: " + e.getMessage()));
        }
    }

    @ActionAnnotation(title = "McpServer", action = "获取活跃服务器", description = "get active servers")
    @Operation(summary = "Get Active Servers", description = "Get list of currently active MCP servers")
    @GetMapping("/active")
    public ResponseEntity<?> getActiveServers() {
        try {
            List<String> activeServers = mcpServerService.getActiveServers();
            return ResponseEntity.ok(JsonResult.success(activeServers));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("Failed to get active servers: " + e.getMessage()));
        }
    }

    
    
}