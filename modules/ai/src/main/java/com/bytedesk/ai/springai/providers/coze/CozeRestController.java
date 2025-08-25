/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 06:49:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.coze;

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
@RequestMapping("/api/v1/coze")
@AllArgsConstructor
@Tag(name = "Coze Management", description = "Coze management APIs for organizing and categorizing content with cozes")
@Description("Coze Management Controller - Content coze and categorization APIs")
public class CozeRestController extends BaseRestController<CozeRequest, CozeRestService> {

    private final CozeRestService cozeRestService;

    @ActionAnnotation(title = "Coze", action = "组织查询", description = "query coze by org")
    @Operation(summary = "Query Cozes by Organization", description = "Retrieve cozes for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(CozeRequest request) {
        
        Page<CozeResponse> cozes = cozeRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(cozes));
    }

    @ActionAnnotation(title = "Coze", action = "用户查询", description = "query coze by user")
    @Operation(summary = "Query Cozes by User", description = "Retrieve cozes for the current user")
    @Override
    public ResponseEntity<?> queryByUser(CozeRequest request) {
        
        Page<CozeResponse> cozes = cozeRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(cozes));
    }

    @ActionAnnotation(title = "Coze", action = "查询详情", description = "query coze by uid")
    @Operation(summary = "Query Coze by UID", description = "Retrieve a specific coze by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(CozeRequest request) {
        
        CozeResponse coze = cozeRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(coze));
    }

    @ActionAnnotation(title = "Coze", action = "新建", description = "create coze")
    @Operation(summary = "Create Coze", description = "Create a new coze")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(CozeRequest request) {
        
        CozeResponse coze = cozeRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(coze));
    }

    @ActionAnnotation(title = "Coze", action = "更新", description = "update coze")
    @Operation(summary = "Update Coze", description = "Update an existing coze")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(CozeRequest request) {
        
        CozeResponse coze = cozeRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(coze));
    }

    @ActionAnnotation(title = "Coze", action = "删除", description = "delete coze")
    @Operation(summary = "Delete Coze", description = "Delete a coze")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(CozeRequest request) {
        
        cozeRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Coze", action = "导出", description = "export coze")
    @Operation(summary = "Export Cozes", description = "Export cozes to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(CozeRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            cozeRestService,
            CozeExcel.class,
            "Coze",
            "coze"
        );
    }

    
    
}