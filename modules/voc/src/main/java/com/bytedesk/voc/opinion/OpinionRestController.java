/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:03:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.opinion;

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
@RequestMapping("/api/v1/opinion")
@AllArgsConstructor
@Tag(name = "Opinion Management", description = "Opinion management APIs for organizing and categorizing content with opinions")
@Description("Opinion Management Controller - Content tag and categorization APIs")
public class OpinionRestController extends BaseRestController<OpinionRequest, OpinionRestService> {

    private final OpinionRestService opinionRestService;

    @ActionAnnotation(title = "opinion", action = "组织查询", description = "query opinion by org")
    @Operation(summary = "Query Opinions by Organization", description = "Retrieve opinions for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(OpinionRequest request) {
        
        Page<OpinionResponse> opinions = opinionRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(opinions));
    }

    @ActionAnnotation(title = "opinion", action = "用户查询", description = "query opinion by user")
    @Operation(summary = "Query Opinions by User", description = "Retrieve opinions for the current user")
    @Override
    public ResponseEntity<?> queryByUser(OpinionRequest request) {
        
        Page<OpinionResponse> opinions = opinionRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(opinions));
    }

    @ActionAnnotation(title = "opinion", action = "查询详情", description = "query opinion by uid")
    @Operation(summary = "Query Opinion by UID", description = "Retrieve a specific opinion by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(OpinionRequest request) {
        
        OpinionResponse opinion = opinionRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(opinion));
    }

    @ActionAnnotation(title = "opinion", action = "新建", description = "create opinion")
    @Operation(summary = "Create Opinion", description = "Create a new opinion")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(OpinionRequest request) {
        
        OpinionResponse opinion = opinionRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(opinion));
    }

    @ActionAnnotation(title = "opinion", action = "更新", description = "update opinion")
    @Operation(summary = "Update Opinion", description = "Update an existing opinion")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(OpinionRequest request) {
        
        OpinionResponse opinion = opinionRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(opinion));
    }

    @ActionAnnotation(title = "opinion", action = "删除", description = "delete opinion")
    @Operation(summary = "Delete Opinion", description = "Delete a opinion")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(OpinionRequest request) {
        
        opinionRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "opinion", action = "导出", description = "export opinion")
    @Operation(summary = "Export Opinions", description = "Export opinions to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(OpinionRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            opinionRestService,
            OpinionExcel.class,
            "opinion",
            "opinion"
        );
    }

    
    
}