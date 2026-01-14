/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task_comment;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/task/comment")
@AllArgsConstructor
@Tag(name = "TaskComment Management", description = "TaskComment management APIs for organizing and categorizing content with taskComments")
@Description("TaskComment Management Controller - Content task_commentging and categorization APIs")
public class TaskCommentRestController extends BaseRestController<TaskCommentRequest, TaskCommentRestService> {

    private final TaskCommentRestService taskCommentRestService;

    @ActionAnnotation(title = "TaskComment", action = "组织查询", description = "query task_comment by org")
    @Operation(summary = "Query TaskComments by Organization", description = "Retrieve taskComments for the current organization")
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_READ)
    @Override
    public ResponseEntity<?> queryByOrg(TaskCommentRequest request) {
        
        Page<TaskCommentResponse> taskComments = taskCommentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(taskComments));
    }

    @ActionAnnotation(title = "TaskComment", action = "用户查询", description = "query task_comment by user")
    @Operation(summary = "Query TaskComments by User", description = "Retrieve taskComments for the current user")
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_READ)
    @Override
    public ResponseEntity<?> queryByUser(TaskCommentRequest request) {
        
        Page<TaskCommentResponse> taskComments = taskCommentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(taskComments));
    }

    @ActionAnnotation(title = "TaskComment", action = "查询详情", description = "query task_comment by uid")
    @Operation(summary = "Query TaskComment by UID", description = "Retrieve a specific task_comment by its unique identifier")
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_READ)
    @Override
    public ResponseEntity<?> queryByUid(TaskCommentRequest request) {
        
        TaskCommentResponse task_comment = taskCommentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(task_comment));
    }

    @ActionAnnotation(title = "TaskComment", action = "新建", description = "create task_comment")
    @Operation(summary = "Create TaskComment", description = "Create a new task_comment")
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_CREATE)
    @Override
    public ResponseEntity<?> create(TaskCommentRequest request) {
        
        TaskCommentResponse task_comment = taskCommentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(task_comment));
    }

    @ActionAnnotation(title = "TaskComment", action = "更新", description = "update task_comment")
    @Operation(summary = "Update TaskComment", description = "Update an existing task_comment")
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_UPDATE)
    @Override
    public ResponseEntity<?> update(TaskCommentRequest request) {
        
        TaskCommentResponse task_comment = taskCommentRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(task_comment));
    }

    @ActionAnnotation(title = "TaskComment", action = "删除", description = "delete task_comment")
    @Operation(summary = "Delete TaskComment", description = "Delete a task_comment")
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_DELETE)
    @Override
    public ResponseEntity<?> delete(TaskCommentRequest request) {
        
        taskCommentRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "TaskComment", action = "导出", description = "export task_comment")
    @Operation(summary = "Export TaskComments", description = "Export taskComments to Excel format")
    @Override
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_EXPORT)
    @GetMapping("/export")
    public Object export(TaskCommentRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            taskCommentRestService,
            TaskCommentExcel.class,
            "TaskComment",
            "task_comment"
        );
    }

    @ActionAnnotation(title = "TaskComment", action = "按任务查询", description = "query task comments by task")
    @Operation(summary = "Query TaskComments by Task", description = "Retrieve comments for a given task")
    @PreAuthorize(TaskCommentPermissions.HAS_TASK_COMMENT_READ)
    @GetMapping("/query/task")
    public ResponseEntity<?> queryByTask(TaskCommentRequest request) {
        Page<TaskCommentResponse> page = taskCommentRestService.queryByTask(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    
    
}