/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 12:43:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.maxkb;

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
@RequestMapping("/api/v1/maxkb")
@AllArgsConstructor
@Tag(name = "Maxkb Management", description = "Maxkb management APIs for organizing and categorizing content with maxKbs")
@Description("Maxkb Management Controller - Content maxkb and categorization APIs")
public class MaxkbRestController extends BaseRestController<MaxkbRequest, MaxkbRestService> {

    private final MaxkbRestService maxkbRestService;

    @ActionAnnotation(title = "Maxkb", action = "组织查询", description = "query maxkb by org")
    @Operation(summary = "Query Maxkbs by Organization", description = "Retrieve maxKb for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(MaxkbRequest request) {
        
        Page<MaxkbResponse> maxKbs = maxkbRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(maxKbs));
    }

    @ActionAnnotation(title = "Maxkb", action = "用户查询", description = "query maxkb by user")
    @Operation(summary = "Query Maxkbs by User", description = "Retrieve maxKbs for the current user")
    @Override
    public ResponseEntity<?> queryByUser(MaxkbRequest request) {
        
        Page<MaxkbResponse> maxKbs = maxkbRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(maxKbs));
    }

    @ActionAnnotation(title = "Maxkb", action = "查询详情", description = "query maxkb by uid")
    @Operation(summary = "Query Maxkb by UID", description = "Retrieve a specific maxkb by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(MaxkbRequest request) {
        
        MaxkbResponse maxkb = maxkbRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(maxkb));
    }

    @ActionAnnotation(title = "Maxkb", action = "新建", description = "create maxkb")
    @Operation(summary = "Create Maxkb", description = "Create a new maxkb")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(MaxkbRequest request) {
        
        MaxkbResponse maxkb = maxkbRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(maxkb));
    }

    @ActionAnnotation(title = "Maxkb", action = "更新", description = "update maxkb")
    @Operation(summary = "Update Maxkb", description = "Update an existing maxkb")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(MaxkbRequest request) {
        
        MaxkbResponse maxkb = maxkbRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(maxkb));
    }

    @ActionAnnotation(title = "Maxkb", action = "删除", description = "delete maxkb")
    @Operation(summary = "Delete Maxkb", description = "Delete a maxkb")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(MaxkbRequest request) {
        
        maxkbRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Maxkb", action = "导出", description = "export maxkb")
    @Operation(summary = "Export Maxkbs", description = "Export maxKbs to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(MaxkbRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            maxkbRestService,
            MaxkbExcel.class,
            "Maxkb",
            "maxkb"
        );
    }

    
    
}