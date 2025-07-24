/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 19:55:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.host;

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
@RequestMapping("/api/v1/host")
@AllArgsConstructor
@Tag(name = "Host Management", description = "Host management APIs for organizing and categorizing content with hosts")
@Description("Host Management Controller - Content host and categorization APIs")
public class HostRestController extends BaseRestController<HostRequest> {

    private final HostRestService hostRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "Host", action = "org query", description = "query host by org")
    @Operation(summary = "Query Hosts by Organization", description = "Retrieve hosts for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(HostRequest request) {
        
        Page<HostResponse> hosts = hostRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(hosts));
    }

    @ActionAnnotation(title = "Host", action = "user query", description = "query host by user")
    @Operation(summary = "Query Hosts by User", description = "Retrieve hosts for the current user")
    @Override
    public ResponseEntity<?> queryByUser(HostRequest request) {
        
        Page<HostResponse> hosts = hostRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(hosts));
    }

    @ActionAnnotation(title = "Host", action = "query detail", description = "query host by uid")
    @Operation(summary = "Query Host by UID", description = "Retrieve a specific host by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(HostRequest request) {
        
        HostResponse host = hostRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(host));
    }

    @ActionAnnotation(title = "Host", action = "create", description = "create host")
    @Operation(summary = "Create Host", description = "Create a new host")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(HostRequest request) {
        
        HostResponse host = hostRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(host));
    }

    @ActionAnnotation(title = "Host", action = "update", description = "update host")
    @Operation(summary = "Update Host", description = "Update an existing host")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(HostRequest request) {
        
        HostResponse host = hostRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(host));
    }

    @ActionAnnotation(title = "Host", action = "delete", description = "delete host")
    @Operation(summary = "Delete Host", description = "Delete a host")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(HostRequest request) {
        
        hostRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Host", action = "export", description = "export host")
    @Operation(summary = "Export Hosts", description = "Export hosts to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(HostRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            hostRestService,
            HostExcel.class,
            "Host",
            "host"
        );
    }

    
    
}