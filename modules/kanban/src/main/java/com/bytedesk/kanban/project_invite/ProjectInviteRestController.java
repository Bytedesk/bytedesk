/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:01:49
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
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/project_invite")
@AllArgsConstructor
public class ProjectInviteRestController extends BaseRestController<ProjectInviteRequest, ProjectInviteRestService> {

    private final ProjectInviteRestService project_inviteService;

    @Operation(summary = "Query Project Invites by Organization", description = "Retrieve project invites for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_PROJECT_INVITE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query project invite")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(ProjectInviteRequest request) {
        
        Page<ProjectInviteResponse> project_invites = project_inviteService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(project_invites));
    }

    @Operation(summary = "Query Project Invites by User", description = "Retrieve project invites for the current user")
    @ActionAnnotation(title = I18Consts.I18N_PROJECT_INVITE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query project invite")
    @Override
    public ResponseEntity<?> queryByUser(ProjectInviteRequest request) {
        
        Page<ProjectInviteResponse> project_invites = project_inviteService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(project_invites));
    }

    @Operation(summary = "Create Project Invites", description = "Create a new project invite entry")
    @ActionAnnotation(title = I18Consts.I18N_PROJECT_INVITE, action = I18Consts.I18N_ACTION_CREATE, description = "create project invite")
    @Override
    public ResponseEntity<?> create(ProjectInviteRequest request) {
        
        ProjectInviteResponse project_invite = project_inviteService.create(request);

        return ResponseEntity.ok(JsonResult.success(project_invite));
    }

    @Operation(summary = "Update Project Invites", description = "Update an existing project invite entry")
    @ActionAnnotation(title = I18Consts.I18N_PROJECT_INVITE, action = I18Consts.I18N_ACTION_UPDATE, description = "update project invite")
    @Override
    public ResponseEntity<?> update(ProjectInviteRequest request) {
        
        ProjectInviteResponse project_invite = project_inviteService.update(request);

        return ResponseEntity.ok(JsonResult.success(project_invite));
    }

    @Operation(summary = "Delete Project Invites", description = "Delete a project invite entry")
    @ActionAnnotation(title = I18Consts.I18N_PROJECT_INVITE, action = I18Consts.I18N_ACTION_DELETE, description = "delete project invite")
    @Override
    public ResponseEntity<?> delete(ProjectInviteRequest request) {
        
        project_inviteService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Project Invites", description = "Export project invites to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_PROJECT_INVITE, action = I18Consts.I18N_ACTION_EXPORT, description = "export project invite")
    @Override
    public Object export(ProjectInviteRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Project Invites by UID", description = "Retrieve a specific project invite entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_PROJECT_INVITE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query project invite")
    @Override
    public ResponseEntity<?> queryByUid(ProjectInviteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}