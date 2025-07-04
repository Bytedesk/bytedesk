/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:37:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.project_invite;

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
@RequestMapping("/api/v1/project_invite")
@AllArgsConstructor
public class ProjectInviteRestController extends BaseRestController<ProjectInviteRequest> {

    private final ProjectInviteRestService project_inviteService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(ProjectInviteRequest request) {
        
        Page<ProjectInviteResponse> project_invites = project_inviteService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(project_invites));
    }

    @Override
    public ResponseEntity<?> queryByUser(ProjectInviteRequest request) {
        
        Page<ProjectInviteResponse> project_invites = project_inviteService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(project_invites));
    }

    @Override
    public ResponseEntity<?> create(ProjectInviteRequest request) {
        
        ProjectInviteResponse project_invite = project_inviteService.create(request);

        return ResponseEntity.ok(JsonResult.success(project_invite));
    }

    @Override
    public ResponseEntity<?> update(ProjectInviteRequest request) {
        
        ProjectInviteResponse project_invite = project_inviteService.update(request);

        return ResponseEntity.ok(JsonResult.success(project_invite));
    }

    @Override
    public ResponseEntity<?> delete(ProjectInviteRequest request) {
        
        project_inviteService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(ProjectInviteRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(ProjectInviteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}