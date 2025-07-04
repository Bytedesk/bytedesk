/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-07 13:37:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group_invite;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/group/invite")
@AllArgsConstructor
public class GroupInviteRestController extends BaseRestController<GroupInviteRequest> {

    private final GroupInviteRestService groupInviteService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(GroupInviteRequest request) {
        
        Page<GroupInviteResponse> groupInvites = groupInviteService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(groupInvites));
    }

    @Override
    public ResponseEntity<?> queryByUser(GroupInviteRequest request) {
        
        Page<GroupInviteResponse> groupInvites = groupInviteService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(groupInvites));
    }

    @Override
    public ResponseEntity<?> create(GroupInviteRequest request) {
        
        GroupInviteResponse groupInvite = groupInviteService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(groupInvite));
    }

    @Override
    public ResponseEntity<?> update(GroupInviteRequest request) {
        
        GroupInviteResponse groupInvite = groupInviteService.update(request);

        return ResponseEntity.ok(JsonResult.success(groupInvite));
    }

    @Override
    public ResponseEntity<?> delete(GroupInviteRequest request) {
        
        groupInviteService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(GroupInviteRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(GroupInviteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}