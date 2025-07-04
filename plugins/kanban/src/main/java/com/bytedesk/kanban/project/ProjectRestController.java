/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 22:15:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.project;

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
@RequestMapping("/api/v1/project")
@AllArgsConstructor
public class ProjectRestController extends BaseRestController<ProjectRequest> {

    private final ProjectRestService projectService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(ProjectRequest request) {
        
        Page<ProjectResponse> projects = projectService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(projects));
    }

    @Override
    public ResponseEntity<?> queryByUser(ProjectRequest request) {
        
        Page<ProjectResponse> projects = projectService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(projects));
    }

    @Override
    public ResponseEntity<?> queryByUid(ProjectRequest request) {
        
        ProjectResponse project = projectService.queryByUid(request);
        if (project == null) {
            return ResponseEntity.ok(JsonResult.error("项目不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(project));
    }

    @Override
    public ResponseEntity<?> create(ProjectRequest request) {
        
        ProjectResponse project = projectService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(project));
    }

    @Override
    public ResponseEntity<?> update(ProjectRequest request) {
        
        ProjectResponse project = projectService.update(request);

        return ResponseEntity.ok(JsonResult.success(project));
    }

    @Override
    public ResponseEntity<?> delete(ProjectRequest request) {
        
        projectService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(ProjectRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
    
}