/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:24:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/server")
@AllArgsConstructor
@Tag(name = "Server Management", description = "Server management APIs for organizing and categorizing content with servers")
@Description("Server Management Controller - Content server and categorization APIs")
public class ServerRestController extends BaseRestController<ServerRequest, ServerRestService> {

    private final ServerRestService serverRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query server by org")
    @Operation(summary = "Query Servers by Organization", description = "Retrieve servers for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ServerRequest request) {
        
        Page<ServerResponse> servers = serverRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(servers));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query server by user")
    @Operation(summary = "Query Servers by User", description = "Retrieve servers for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ServerRequest request) {
        
        Page<ServerResponse> servers = serverRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(servers));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query server by uid")
    @Operation(summary = "Query Server by UID", description = "Retrieve a specific server by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ServerRequest request) {
        
        ServerResponse server = serverRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(server));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create server")
    @Operation(summary = "Create Server", description = "Create a new server")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(ServerRequest request) {
        
        ServerResponse server = serverRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(server));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update server")
    @Operation(summary = "Update Server", description = "Update an existing server")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(ServerRequest request) {
        
        ServerResponse server = serverRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(server));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete server")
    @Operation(summary = "Delete Server", description = "Delete a server")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(ServerRequest request) {
        
        serverRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export server")
    @Operation(summary = "Export Servers", description = "Export servers to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(ServerRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            serverRestService,
            ServerExcel.class,
            "标签",
            "server"
        );
    }

    
    
}