/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 14:15:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.comment;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "评论管理", description = "评论管理相关接口")
@RestController
@RequestMapping("/api/v1/kbase/comment")
@AllArgsConstructor
public class KbaseCommentRestController extends BaseRestController<KbaseCommentRequest, KbaseCommentRestService> {

    private final KbaseCommentRestService commentService;

    @Operation(summary = "查询组织下的评论", description = "根据组织ID查询评论列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseCommentResponse.class)))
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(KbaseCommentRequest request) {
        
        Page<KbaseCommentResponse> comments = commentService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @Operation(summary = "查询用户下的评论", description = "根据用户ID查询评论列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseCommentResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(KbaseCommentRequest request) {
        
        Page<KbaseCommentResponse> comments = commentService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @Operation(summary = "创建评论", description = "创建新的评论")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseCommentResponse.class)))
    @Override
    public ResponseEntity<?> create(KbaseCommentRequest request) {
        
        KbaseCommentResponse comment = commentService.create(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @Operation(summary = "更新评论", description = "更新评论信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = KbaseCommentResponse.class)))
    @Override
    public ResponseEntity<?> update(KbaseCommentRequest request) {
        
        KbaseCommentResponse comment = commentService.update(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @Operation(summary = "删除评论", description = "删除指定的评论")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(KbaseCommentRequest request) {
        
        commentService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出评论", description = "导出评论数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(KbaseCommentRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(KbaseCommentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}