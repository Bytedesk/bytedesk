/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 16:05:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.todo_list;

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
public class TodoListRestController extends BaseRestController<TodoListRequest> {

    private final TodoListRestService todoService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(TodoListRequest request) {
        
        Page<TodoListResponse> todos = todoService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(todos));
    }

    @Override
    public ResponseEntity<?> queryByUser(TodoListRequest request) {
        
        Page<TodoListResponse> todos = todoService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(todos));
    }

    @Override
    public ResponseEntity<?> create(TodoListRequest request) {
        
        TodoListResponse todo = todoService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(todo));
    }

    @Override
    public ResponseEntity<?> update(TodoListRequest request) {
        
        TodoListResponse todo = todoService.update(request);

        return ResponseEntity.ok(JsonResult.success(todo));
    }

    @Override
    public ResponseEntity<?> delete(TodoListRequest request) {
        
        todoService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(TodoListRequest request, HttpServletResponse response) {
        // TODOLIST Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(TodoListRequest request) {
        // TODOLIST Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}