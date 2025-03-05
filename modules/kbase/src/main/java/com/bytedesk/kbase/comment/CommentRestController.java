/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:38:48
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

@RestController
@RequestMapping("/api/v1/comment")
@AllArgsConstructor
public class CommentRestController extends BaseRestController<CommentRequest> {

    private final CommentRestService commentService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(CommentRequest request) {
        
        Page<CommentResponse> comments = commentService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @Override
    public ResponseEntity<?> queryByUser(CommentRequest request) {
        
        Page<CommentResponse> comments = commentService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @Override
    public ResponseEntity<?> create(CommentRequest request) {
        
        CommentResponse comment = commentService.create(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @Override
    public ResponseEntity<?> update(CommentRequest request) {
        
        CommentResponse comment = commentService.update(request);

        return ResponseEntity.ok(JsonResult.success(comment));
    }

    @Override
    public ResponseEntity<?> delete(CommentRequest request) {
        
        commentService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(CommentRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(CommentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}