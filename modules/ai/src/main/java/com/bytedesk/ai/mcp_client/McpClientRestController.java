/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_client;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/mcp/client")
@AllArgsConstructor
@Tag(name = "McpClient Management", description = "McpClient management APIs for organizing and categorizing content with mcp_clients")
@Description("McpClient Management Controller - Content mcp_clientging and categorization APIs")
public class McpClientRestController extends BaseRestController<McpClientRequest, McpClientRestService> {

    private final McpClientRestService mcpClientRestService;

    @ActionAnnotation(title = "McpClient", action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query mcp_client by org")
    @Operation(summary = "Query McpClients by Organization", description = "Retrieve mcp_clients for the current organization")
    // @PreAuthorize(McpClientPermissions.HAS_MCPCLIENT_READ)
    @Override
    public ResponseEntity<?> queryByOrg(McpClientRequest request) {
        
        Page<McpClientResponse> mcp_clients = mcpClientRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(mcp_clients));
    }

    @ActionAnnotation(title = "McpClient", action = I18Consts.I18N_ACTION_QUERY_USER, description = "query mcp_client by user")
    @Operation(summary = "Query McpClients by User", description = "Retrieve mcp_clients for the current user")
    // @PreAuthorize(McpClientPermissions.HAS_MCPCLIENT_READ)
    @Override
    public ResponseEntity<?> queryByUser(McpClientRequest request) {
        
        Page<McpClientResponse> mcp_clients = mcpClientRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(mcp_clients));
    }

    @ActionAnnotation(title = "McpClient", action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query mcp_client by uid")
    @Operation(summary = "Query McpClient by UID", description = "Retrieve a specific mcp_client by its unique identifier")
    // @PreAuthorize(McpClientPermissions.HAS_MCPCLIENT_READ)
    @Override
    public ResponseEntity<?> queryByUid(McpClientRequest request) {
        
        McpClientResponse mcp_client = mcpClientRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(mcp_client));
    }

    @ActionAnnotation(title = "McpClient", action = I18Consts.I18N_ACTION_CREATE, description = "create mcp_client")
    @Operation(summary = "Create McpClient", description = "Create a new mcp_client")
    @Override
    // @PreAuthorize(McpClientPermissions.HAS_MCPCLIENT_CREATE)
    public ResponseEntity<?> create(McpClientRequest request) {
        
        McpClientResponse mcp_client = mcpClientRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(mcp_client));
    }

    @ActionAnnotation(title = "McpClient", action = I18Consts.I18N_ACTION_UPDATE, description = "update mcp_client")
    @Operation(summary = "Update McpClient", description = "Update an existing mcp_client")
    @Override
    // @PreAuthorize(McpClientPermissions.HAS_MCPCLIENT_UPDATE)
    public ResponseEntity<?> update(McpClientRequest request) {
        
        McpClientResponse mcp_client = mcpClientRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(mcp_client));
    }

    @ActionAnnotation(title = "McpClient", action = I18Consts.I18N_ACTION_DELETE, description = "delete mcp_client")
    @Operation(summary = "Delete McpClient", description = "Delete a mcp_client")
    @Override
    // @PreAuthorize(McpClientPermissions.HAS_MCPCLIENT_DELETE)
    public ResponseEntity<?> delete(McpClientRequest request) {
        
        mcpClientRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "McpClient", action = I18Consts.I18N_ACTION_EXPORT, description = "export mcp_client")
    @Operation(summary = "Export McpClients", description = "Export mcp_clients to Excel format")
    @Override
    // @PreAuthorize(McpClientPermissions.HAS_MCPCLIENT_EXPORT)
    @GetMapping("/export")
    public Object export(McpClientRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            mcpClientRestService,
            McpClientExcel.class,
            "McpClient",
            "mcp_client"
        );
    }

    
    
}