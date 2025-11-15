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
package com.bytedesk.core.task_list;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/task_list")
@AllArgsConstructor
@Tag(name = "TaskList Management", description = "TaskList management APIs for organizing and categorizing content with task_lists")
@Description("TaskList Management Controller - Content task_listging and categorization APIs")
public class TaskListRestController extends BaseRestController<TaskListRequest, TaskListRestService> {

    private final TaskListRestService taskListRestService;

    @ActionAnnotation(title = "TaskList", action = "组织查询", description = "query task_list by org")
    @Operation(summary = "Query TaskLists by Organization", description = "Retrieve task_lists for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(TaskListRequest request) {
        
        Page<TaskListResponse> task_lists = taskListRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(task_lists));
    }

    @ActionAnnotation(title = "TaskList", action = "用户查询", description = "query task_list by user")
    @Operation(summary = "Query TaskLists by User", description = "Retrieve task_lists for the current user")
    @Override
    public ResponseEntity<?> queryByUser(TaskListRequest request) {
        
        Page<TaskListResponse> task_lists = taskListRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(task_lists));
    }

    @ActionAnnotation(title = "TaskList", action = "查询详情", description = "query task_list by uid")
    @Operation(summary = "Query TaskList by UID", description = "Retrieve a specific task_list by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(TaskListRequest request) {
        
        TaskListResponse task_list = taskListRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(task_list));
    }

    @ActionAnnotation(title = "TaskList", action = "新建", description = "create task_list")
    @Operation(summary = "Create TaskList", description = "Create a new task_list")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(TaskListRequest request) {
        
        TaskListResponse task_list = taskListRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(task_list));
    }

    @ActionAnnotation(title = "TaskList", action = "更新", description = "update task_list")
    @Operation(summary = "Update TaskList", description = "Update an existing task_list")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(TaskListRequest request) {
        
        TaskListResponse task_list = taskListRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(task_list));
    }

    @ActionAnnotation(title = "TaskList", action = "删除", description = "delete task_list")
    @Operation(summary = "Delete TaskList", description = "Delete a task_list")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(TaskListRequest request) {
        
        taskListRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "TaskList", action = "导出", description = "export task_list")
    @Operation(summary = "Export TaskLists", description = "Export task_lists to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(TaskListRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            taskListRestService,
            TaskListExcel.class,
            "TaskList",
            "task_list"
        );
    }

    
    
}