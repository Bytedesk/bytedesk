/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-26 13:36:46
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

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

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

    

    
    
}