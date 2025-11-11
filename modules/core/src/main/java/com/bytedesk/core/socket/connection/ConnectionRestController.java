/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:05:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.connection;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/api/v1/connection")
@AllArgsConstructor
@Tag(name = "Connection Management", description = "Connection management APIs for organizing and categorizing content with connections")
@Description("Connection Management Controller - Content connectionging and categorization APIs")
public class ConnectionRestController extends BaseRestController<ConnectionRequest, ConnectionRestService> {

    private final ConnectionRestService connectionRestService;

    @ActionAnnotation(title = "Connection", action = "组织查询", description = "query connection by org")
    @Operation(summary = "Query Connections by Organization", description = "Retrieve connections for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ConnectionRequest request) {
        
        Page<ConnectionResponse> connections = connectionRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(connections));
    }

    @ActionAnnotation(title = "Connection", action = "用户查询", description = "query connection by user")
    @Operation(summary = "Query Connections by User", description = "Retrieve connections for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ConnectionRequest request) {
        
        Page<ConnectionResponse> connections = connectionRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(connections));
    }

    @ActionAnnotation(title = "Connection", action = "查询详情", description = "query connection by uid")
    @Operation(summary = "Query Connection by UID", description = "Retrieve a specific connection by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ConnectionRequest request) {
        
        ConnectionResponse connection = connectionRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(connection));
    }

    @ActionAnnotation(title = "Connection", action = "新建", description = "create connection")
    @Operation(summary = "Create Connection", description = "Create a new connection")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(ConnectionRequest request) {
        
        ConnectionResponse connection = connectionRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(connection));
    }

    @ActionAnnotation(title = "Connection", action = "更新", description = "update connection")
    @Operation(summary = "Update Connection", description = "Update an existing connection")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(ConnectionRequest request) {
        
        ConnectionResponse connection = connectionRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(connection));
    }

    @ActionAnnotation(title = "Connection", action = "删除", description = "delete connection")
    @Operation(summary = "Delete Connection", description = "Delete a connection")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(ConnectionRequest request) {
        
        connectionRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Connection", action = "导出", description = "export connection")
    @Operation(summary = "Export Connections", description = "Export connections to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(ConnectionRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            connectionRestService,
            ConnectionExcel.class,
            "Connection",
            "connection"
        );
    }

    @ActionAnnotation(title = "在线", action = "查询", description = "presence by user")
    @Operation(summary = "Get Presence", description = "Get user's online presence and active connection count")
    @GetMapping("/presence/{userUid}")
    public ResponseEntity<?> getPresence(@PathVariable("userUid") String userUid) {
        PresenceResponse presence = connectionRestService.getPresence(userUid);
        return ResponseEntity.ok(JsonResult.success(presence));
    }

    
    
}