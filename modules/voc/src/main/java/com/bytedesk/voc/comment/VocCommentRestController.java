/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 13:49:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.comment;

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
@RequestMapping("/api/v1/voc/comment")
@AllArgsConstructor
@Tag(name = "VocComment Management", description = "VocComment management APIs for organizing and categorizing content with comments")
@Description("VocComment Management Controller - Content tag and categorization APIs")
public class VocCommentRestController extends BaseRestController<VocCommentRequest, VocCommentRestService> {

    private final VocCommentRestService commentRestService;

    @ActionAnnotation(title = "标签", action = "组织查询", description = "query comment by org")
    @Operation(summary = "Query VocComments by Organization", description = "Retrieve comments for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(VocCommentRequest request) {
        
        Page<VocCommentResponse> comments = commentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query comment by user")
    @Operation(summary = "Query VocComments by User", description = "Retrieve comments for the current user")
    @Override
    public ResponseEntity<?> queryByUser(VocCommentRequest request) {
        
        Page<VocCommentResponse> comments = commentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query comment by uid")
    @Operation(summary = "Query VocComment by UID", description = "Retrieve a specific comment by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(VocCommentRequest request) {
        
        VocCommentResponse comment = commentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create comment")
    @Operation(summary = "Create VocComment", description = "Create a new comment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(VocCommentRequest request) {
        
        VocCommentResponse comment = commentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update comment")
    @Operation(summary = "Update VocComment", description = "Update an existing comment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(VocCommentRequest request) {
        
        VocCommentResponse comment = commentRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete comment")
    @Operation(summary = "Delete VocComment", description = "Delete a comment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(VocCommentRequest request) {
        
        commentRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export comment")
    @Operation(summary = "Export VocComments", description = "Export comments to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(VocCommentRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            commentRestService,
            VocCommentExcel.class,
            "标签",
            "comment"
        );
    }

    
    
}