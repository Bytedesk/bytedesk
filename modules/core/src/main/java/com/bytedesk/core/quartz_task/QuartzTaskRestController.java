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
package com.bytedesk.core.quartz_task;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/api/v1/quartz_task")
@AllArgsConstructor
@Tag(name = "QuartzTask Management", description = "QuartzTask management APIs for organizing and categorizing content with quartz_tasks")
@Description("QuartzTask Management Controller - Content quartz_taskging and categorization APIs")
public class QuartzTaskRestController extends BaseRestController<QuartzTaskRequest, QuartzTaskRestService> {

    private final QuartzTaskRestService quartzTaskRestService;

    @ActionAnnotation(title = "QuartzTask", action = "组织查询", description = "query quartz_task by org")
    @Operation(summary = "Query QuartzTasks by Organization", description = "Retrieve quartz_tasks for the current organization")
    // @PreAuthorize(QuartzTaskPermissions.HAS_QUARTZ_TASK_READ)
    @Override
    public ResponseEntity<?> queryByOrg(QuartzTaskRequest request) {
        
        Page<QuartzTaskResponse> quartz_tasks = quartzTaskRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(quartz_tasks));
    }

    @ActionAnnotation(title = "QuartzTask", action = "用户查询", description = "query quartz_task by user")
    @Operation(summary = "Query QuartzTasks by User", description = "Retrieve quartz_tasks for the current user")
    // @PreAuthorize(QuartzTaskPermissions.HAS_QUARTZ_TASK_READ)
    @Override
    public ResponseEntity<?> queryByUser(QuartzTaskRequest request) {
        
        Page<QuartzTaskResponse> quartz_tasks = quartzTaskRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(quartz_tasks));
    }

    @ActionAnnotation(title = "QuartzTask", action = "查询详情", description = "query quartz_task by uid")
    @Operation(summary = "Query QuartzTask by UID", description = "Retrieve a specific quartz_task by its unique identifier")
    // @PreAuthorize(QuartzTaskPermissions.HAS_QUARTZ_TASK_READ)
    @Override
    public ResponseEntity<?> queryByUid(QuartzTaskRequest request) {
        
        QuartzTaskResponse quartz_task = quartzTaskRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(quartz_task));
    }

    @ActionAnnotation(title = "QuartzTask", action = "新建", description = "create quartz_task")
    @Operation(summary = "Create QuartzTask", description = "Create a new quartz_task")
    @Override
    // @PreAuthorize(QuartzTaskPermissions.HAS_QUARTZ_TASK_CREATE)
    public ResponseEntity<?> create(QuartzTaskRequest request) {
        
        QuartzTaskResponse quartz_task = quartzTaskRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(quartz_task));
    }

    @ActionAnnotation(title = "QuartzTask", action = "更新", description = "update quartz_task")
    @Operation(summary = "Update QuartzTask", description = "Update an existing quartz_task")
    @Override
    // @PreAuthorize(QuartzTaskPermissions.HAS_QUARTZ_TASK_UPDATE)
    public ResponseEntity<?> update(QuartzTaskRequest request) {
        
        QuartzTaskResponse quartz_task = quartzTaskRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(quartz_task));
    }

    @ActionAnnotation(title = "QuartzTask", action = "删除", description = "delete quartz_task")
    @Operation(summary = "Delete QuartzTask", description = "Delete a quartz_task")
    @Override
    // @PreAuthorize(QuartzTaskPermissions.HAS_QUARTZ_TASK_DELETE)
    public ResponseEntity<?> delete(QuartzTaskRequest request) {
        
        quartzTaskRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "QuartzTask", action = "导出", description = "export quartz_task")
    @Operation(summary = "Export QuartzTasks", description = "Export quartz_tasks to Excel format")
    @Override
    // @PreAuthorize(QuartzTaskPermissions.HAS_QUARTZ_TASK_EXPORT)
    @GetMapping("/export")
    public Object export(QuartzTaskRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            quartzTaskRestService,
            QuartzTaskExcel.class,
            "QuartzTask",
            "quartz_task"
        );
    }

    @Operation(summary = "Start Job", description = "Start a Quartz job")
    @PostMapping("/startJob")
    public ResponseEntity<?> startJob(@RequestBody QuartzTaskRequest request) {
        quartzTaskRestService.startJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Pause Job", description = "Pause a running Quartz job")
    @PostMapping("/pauseJob")
    public ResponseEntity<?> pauseJob(@RequestBody QuartzTaskRequest request) {
        quartzTaskRestService.pauseJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Resume Job", description = "Resume a paused Quartz job")
    @PostMapping("/resumeJob")
    public ResponseEntity<?> resumeJob(@RequestBody QuartzTaskRequest request) {
        quartzTaskRestService.resumeJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Delete Job", description = "Remove a job from the Quartz scheduler")
    @PostMapping("/deleteJob")
    public ResponseEntity<?> deleteJob(@RequestBody QuartzTaskRequest request) {
        quartzTaskRestService.deleteJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

}