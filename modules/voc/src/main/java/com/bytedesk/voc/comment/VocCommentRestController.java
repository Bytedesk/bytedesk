/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:03:46
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
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

    @ActionAnnotation(title = I18Consts.I18N_COMMENT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query comment by org")
    @Operation(summary = "Query VocComments by Organization", description = "Retrieve comments for the current organization")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(VocCommentRequest request) {
        
        Page<VocCommentResponse> comments = commentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @ActionAnnotation(title = I18Consts.I18N_COMMENT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query comment by user")
    @Operation(summary = "Query VocComments by User", description = "Retrieve comments for the current user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(VocCommentRequest request) {
        
        Page<VocCommentResponse> comments = commentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @ActionAnnotation(title = I18Consts.I18N_COMMENT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query comment by uid")
    @Operation(summary = "Query VocComment by UID", description = "Retrieve a specific comment by its unique identifier")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(VocCommentRequest request) {
        
        VocCommentResponse comment = commentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @ActionAnnotation(title = I18Consts.I18N_COMMENT, action = I18Consts.I18N_ACTION_CREATE, description = "create comment")
    @Operation(summary = "Create VocComment", description = "Create a new comment")
    @PostMapping("/create")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(@RequestBody VocCommentRequest request) {
        
        VocCommentResponse comment = commentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @ActionAnnotation(title = I18Consts.I18N_COMMENT, action = I18Consts.I18N_ACTION_UPDATE, description = "update comment")
    @Operation(summary = "Update VocComment", description = "Update an existing comment")
    @PostMapping("/update")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(@RequestBody VocCommentRequest request) {
        
        VocCommentResponse comment = commentRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @ActionAnnotation(title = I18Consts.I18N_COMMENT, action = I18Consts.I18N_ACTION_DELETE, description = "delete comment")
    @Operation(summary = "Delete VocComment", description = "Delete a comment")
    @PostMapping("/delete")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(@RequestBody VocCommentRequest request) {
        
        commentRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_COMMENT, action = I18Consts.I18N_ACTION_EXPORT, description = "export comment")
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
            "comment",
            "comment"
        );
    }

    
    
}