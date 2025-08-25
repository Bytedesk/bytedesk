/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 06:52:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

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
@RequestMapping("/api/v1/dify")
@AllArgsConstructor
@Tag(name = "Dify Management", description = "Dify management APIs for organizing and categorizing content with difys")
@Description("Dify Management Controller - Content dify and categorization APIs")
public class DifyRestController extends BaseRestController<DifyRequest, DifyRestService> {

    private final DifyRestService difyRestService;

    @ActionAnnotation(title = "Dify", action = "组织查询", description = "query dify by org")
    @Operation(summary = "Query Difys by Organization", description = "Retrieve difys for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(DifyRequest request) {
        
        Page<DifyResponse> difys = difyRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(difys));
    }

    @ActionAnnotation(title = "Dify", action = "用户查询", description = "query dify by user")
    @Operation(summary = "Query Difys by User", description = "Retrieve difys for the current user")
    @Override
    public ResponseEntity<?> queryByUser(DifyRequest request) {
        
        Page<DifyResponse> difys = difyRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(difys));
    }

    @ActionAnnotation(title = "Dify", action = "查询详情", description = "query dify by uid")
    @Operation(summary = "Query Dify by UID", description = "Retrieve a specific dify by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(DifyRequest request) {
        
        DifyResponse dify = difyRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(dify));
    }

    @ActionAnnotation(title = "Dify", action = "新建", description = "create dify")
    @Operation(summary = "Create Dify", description = "Create a new dify")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(DifyRequest request) {
        
        DifyResponse dify = difyRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(dify));
    }

    @ActionAnnotation(title = "Dify", action = "更新", description = "update dify")
    @Operation(summary = "Update Dify", description = "Update an existing dify")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(DifyRequest request) {
        
        DifyResponse dify = difyRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(dify));
    }

    @ActionAnnotation(title = "Dify", action = "删除", description = "delete dify")
    @Operation(summary = "Delete Dify", description = "Delete a dify")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(DifyRequest request) {
        
        difyRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Dify", action = "导出", description = "export dify")
    @Operation(summary = "Export Difys", description = "Export difys to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(DifyRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            difyRestService,
            DifyExcel.class,
            "Dify",
            "dify"
        );
    }

    
    
}