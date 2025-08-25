/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 06:51:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.n8n;

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
@RequestMapping("/api/v1/n8n")
@AllArgsConstructor
@Tag(name = "N8n Management", description = "N8n management APIs for organizing and categorizing content with n8ns")
@Description("N8n Management Controller - Content n8n and categorization APIs")
public class N8nRestController extends BaseRestController<N8nRequest, N8nRestService> {

    private final N8nRestService n8nRestService;

    @ActionAnnotation(title = "N8n", action = "组织查询", description = "query n8n by org")
    @Operation(summary = "Query N8ns by Organization", description = "Retrieve n8ns for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(N8nRequest request) {
        
        Page<N8nResponse> n8ns = n8nRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(n8ns));
    }

    @ActionAnnotation(title = "N8n", action = "用户查询", description = "query n8n by user")
    @Operation(summary = "Query N8ns by User", description = "Retrieve n8ns for the current user")
    @Override
    public ResponseEntity<?> queryByUser(N8nRequest request) {
        
        Page<N8nResponse> n8ns = n8nRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(n8ns));
    }

    @ActionAnnotation(title = "N8n", action = "查询详情", description = "query n8n by uid")
    @Operation(summary = "Query N8n by UID", description = "Retrieve a specific n8n by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(N8nRequest request) {
        
        N8nResponse n8n = n8nRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(n8n));
    }

    @ActionAnnotation(title = "N8n", action = "新建", description = "create n8n")
    @Operation(summary = "Create N8n", description = "Create a new n8n")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(N8nRequest request) {
        
        N8nResponse n8n = n8nRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(n8n));
    }

    @ActionAnnotation(title = "N8n", action = "更新", description = "update n8n")
    @Operation(summary = "Update N8n", description = "Update an existing n8n")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(N8nRequest request) {
        
        N8nResponse n8n = n8nRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(n8n));
    }

    @ActionAnnotation(title = "N8n", action = "删除", description = "delete n8n")
    @Operation(summary = "Delete N8n", description = "Delete a n8n")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(N8nRequest request) {
        
        n8nRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "N8n", action = "导出", description = "export n8n")
    @Operation(summary = "Export N8ns", description = "Export n8ns to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(N8nRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            n8nRestService,
            N8nExcel.class,
            "N8n",
            "n8n"
        );
    }

    
    
}