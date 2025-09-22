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
package com.bytedesk.core.open_platform;

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
@RequestMapping("/api/v1/open/platform")
@AllArgsConstructor
@Tag(name = "OpenPlatform Management", description = "OpenPlatform management APIs for organizing and categorizing content with openPlatforms")
@Description("OpenPlatform Management Controller - Content openPlatform management and categorization APIs")
public class OpenPlatformRestController extends BaseRestController<OpenPlatformRequest, OpenPlatformRestService> {

    private final OpenPlatformRestService openPlatformRestService;

    @ActionAnnotation(title = "Open Platform", action = "组织查询", description = "query openPlatform by org")
    @Operation(summary = "Query OpenPlatforms by Organization", description = "Retrieve openPlatforms for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(OpenPlatformRequest request) {
        
        Page<OpenPlatformResponse> openPlatforms = openPlatformRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(openPlatforms));
    }

    @ActionAnnotation(title = "Open Platform", action = "用户查询", description = "query openPlatform by user")
    @Operation(summary = "Query OpenPlatforms by User", description = "Retrieve openPlatforms for the current user")
    @Override
    public ResponseEntity<?> queryByUser(OpenPlatformRequest request) {
        
        Page<OpenPlatformResponse> openPlatforms = openPlatformRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(openPlatforms));
    }

    @ActionAnnotation(title = "Open Platform", action = "查询详情", description = "query openPlatform by uid")
    @Operation(summary = "Query OpenPlatform by UID", description = "Retrieve a specific openPlatform by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(OpenPlatformRequest request) {
        
        OpenPlatformResponse openPlatform = openPlatformRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(openPlatform));
    }

    @ActionAnnotation(title = "Open Platform", action = "新建", description = "create openPlatform")
    @Operation(summary = "Create OpenPlatform", description = "Create a new openPlatform")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(OpenPlatformRequest request) {
        
        OpenPlatformResponse openPlatform = openPlatformRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(openPlatform));
    }

    @ActionAnnotation(title = "Open Platform", action = "更新", description = "update openPlatform")
    @Operation(summary = "Update OpenPlatform", description = "Update an existing openPlatform")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(OpenPlatformRequest request) {
        
        OpenPlatformResponse openPlatform = openPlatformRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(openPlatform));
    }

    @ActionAnnotation(title = "Open Platform", action = "删除", description = "delete openPlatform")
    @Operation(summary = "Delete OpenPlatform", description = "Delete a openPlatform")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(OpenPlatformRequest request) {
        
        openPlatformRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Open Platform", action = "导出", description = "export openPlatform")
    @Operation(summary = "Export OpenPlatforms", description = "Export openPlatforms to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(OpenPlatformRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            openPlatformRestService,
            OpenPlatformExcel.class,
            "Open Platform",
            "openPlatform"
        );
    }

    
    
}