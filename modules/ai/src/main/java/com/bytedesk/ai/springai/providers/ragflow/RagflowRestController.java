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
package com.bytedesk.ai.springai.providers.ragflow;

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
@RequestMapping("/api/v1/ragflow")
@AllArgsConstructor
@Tag(name = "Ragflow Management", description = "Ragflow management APIs for organizing and categorizing content with ragflows")
@Description("Ragflow Management Controller - Content ragflow and categorization APIs")
public class RagflowRestController extends BaseRestController<RagflowRequest, RagflowRestService> {

    private final RagflowRestService ragflowRestService;

    @ActionAnnotation(title = "Ragflow", action = "组织查询", description = "query ragflow by org")
    @Operation(summary = "Query Ragflows by Organization", description = "Retrieve ragflows for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(RagflowRequest request) {
        
        Page<RagflowResponse> ragflows = ragflowRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ragflows));
    }

    @ActionAnnotation(title = "Ragflow", action = "用户查询", description = "query ragflow by user")
    @Operation(summary = "Query Ragflows by User", description = "Retrieve ragflows for the current user")
    @Override
    public ResponseEntity<?> queryByUser(RagflowRequest request) {
        
        Page<RagflowResponse> ragflows = ragflowRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ragflows));
    }

    @ActionAnnotation(title = "Ragflow", action = "查询详情", description = "query ragflow by uid")
    @Operation(summary = "Query Ragflow by UID", description = "Retrieve a specific ragflow by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(RagflowRequest request) {
        
        RagflowResponse ragflow = ragflowRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(ragflow));
    }

    @ActionAnnotation(title = "Ragflow", action = "新建", description = "create ragflow")
    @Operation(summary = "Create Ragflow", description = "Create a new ragflow")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(RagflowRequest request) {
        
        RagflowResponse ragflow = ragflowRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(ragflow));
    }

    @ActionAnnotation(title = "Ragflow", action = "更新", description = "update ragflow")
    @Operation(summary = "Update Ragflow", description = "Update an existing ragflow")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(RagflowRequest request) {
        
        RagflowResponse ragflow = ragflowRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(ragflow));
    }

    @ActionAnnotation(title = "Ragflow", action = "删除", description = "delete ragflow")
    @Operation(summary = "Delete Ragflow", description = "Delete a ragflow")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(RagflowRequest request) {
        
        ragflowRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Ragflow", action = "导出", description = "export ragflow")
    @Operation(summary = "Export Ragflows", description = "Export ragflows to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(RagflowRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ragflowRestService,
            RagflowExcel.class,
            "Ragflow",
            "ragflow"
        );
    }

    
    
}