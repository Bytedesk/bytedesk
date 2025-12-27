/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:05:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/task")
@AllArgsConstructor
@Tag(name = "Task Management", description = "Task management APIs for organizing and categorizing content with tasks")
@Description("Task Management Controller - Content taskging and categorization APIs")
public class TaskRestController extends BaseRestController<TaskRequest, TaskRestService> {

    private final TaskRestService taskRestService;

    @ActionAnnotation(title = "Task", action = "组织查询", description = "query task by org")
    @Operation(summary = "Query Tasks by Organization", description = "Retrieve tasks for the current organization")
    // @PreAuthorize(TaskPermissions.HAS_TASK_READ)
    @Override
    public ResponseEntity<?> queryByOrg(TaskRequest request) {
        
        Page<TaskResponse> tasks = taskRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tasks));
    }

    @ActionAnnotation(title = "Task", action = "用户查询", description = "query task by user")
    @Operation(summary = "Query Tasks by User", description = "Retrieve tasks for the current user")
    // @PreAuthorize(TaskPermissions.HAS_TASK_READ)
    @Override
    public ResponseEntity<?> queryByUser(TaskRequest request) {
        
        Page<TaskResponse> tasks = taskRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tasks));
    }

    @ActionAnnotation(title = "Task", action = "查询详情", description = "query task by uid")
    @Operation(summary = "Query Task by UID", description = "Retrieve a specific task by its unique identifier")
    // @PreAuthorize(TaskPermissions.HAS_TASK_READ)
    @Override
    public ResponseEntity<?> queryByUid(TaskRequest request) {
        
        TaskResponse task = taskRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(task));
    }

    @ActionAnnotation(title = "Task", action = "新建", description = "create task")
    @Operation(summary = "Create Task", description = "Create a new task")
    @Override
    // @PreAuthorize(TaskPermissions.HAS_TASK_CREATE)
    public ResponseEntity<?> create(TaskRequest request) {
        
        TaskResponse task = taskRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(task));
    }

    @ActionAnnotation(title = "Task", action = "更新", description = "update task")
    @Operation(summary = "Update Task", description = "Update an existing task")
    @Override
    // @PreAuthorize(TaskPermissions.HAS_TASK_UPDATE)
    public ResponseEntity<?> update(TaskRequest request) {
        
        TaskResponse task = taskRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(task));
    }

    @ActionAnnotation(title = "Task", action = "删除", description = "delete task")
    @Operation(summary = "Delete Task", description = "Delete a task")
    @Override
    // @PreAuthorize(TaskPermissions.HAS_TASK_DELETE)
    public ResponseEntity<?> delete(TaskRequest request) {
        
        taskRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Task", action = "导出", description = "export task")
    @Operation(summary = "Export Tasks", description = "Export tasks to Excel format")
    @Override
    // @PreAuthorize(TaskPermissions.HAS_TASK_EXPORT)
    @GetMapping("/export")
    public Object export(TaskRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            taskRestService,
            TaskExcel.class,
            "Task",
            "task"
        );
    }

    
    
}