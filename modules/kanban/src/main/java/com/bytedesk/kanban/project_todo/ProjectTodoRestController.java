/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:02:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.project_todo;

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
@RequestMapping("/api/v1/todo/list")
@AllArgsConstructor
public class ProjectTodoRestController extends BaseRestController<ProjectTodoRequest, ProjectTodoRestService> {

    private final ProjectTodoRestService todoService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(ProjectTodoRequest request) {
        
        Page<ProjectTodoResponse> todos = todoService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(todos));
    }

    @Override
    public ResponseEntity<?> queryByUser(ProjectTodoRequest request) {
        
        Page<ProjectTodoResponse> todos = todoService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(todos));
    }

    @Override
    public ResponseEntity<?> create(ProjectTodoRequest request) {
        
        ProjectTodoResponse todo = todoService.create(request);

        return ResponseEntity.ok(JsonResult.success(todo));
    }

    @Override
    public ResponseEntity<?> update(ProjectTodoRequest request) {
        
        ProjectTodoResponse todo = todoService.update(request);

        return ResponseEntity.ok(JsonResult.success(todo));
    }

    @Override
    public ResponseEntity<?> delete(ProjectTodoRequest request) {
        
        todoService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(ProjectTodoRequest request, HttpServletResponse response) {
        // TODOLIST Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(ProjectTodoRequest request) {
        // TODOLIST Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}