/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 17:12:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase_invite;

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
@RequestMapping("/api/v1/kbase/invite")
@AllArgsConstructor
public class KbaseInviteRestController extends BaseRestController<KbaseInviteRequest> {

    private final KbaseInviteRestService tagService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(KbaseInviteRequest request) {
        
        Page<KbaseInviteResponse> tags = tagService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @Override
    public ResponseEntity<?> queryByUser(KbaseInviteRequest request) {
        
        Page<KbaseInviteResponse> tags = tagService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @Override
    public ResponseEntity<?> create(KbaseInviteRequest request) {
        
        KbaseInviteResponse tag = tagService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @Override
    public ResponseEntity<?> update(KbaseInviteRequest request) {
        
        KbaseInviteResponse tag = tagService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @Override
    public ResponseEntity<?> delete(KbaseInviteRequest request) {
        
        tagService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(KbaseInviteRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(KbaseInviteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}