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
package com.bytedesk.core.schedule;

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
@RequestMapping("/api/v1/schedule")
@AllArgsConstructor
@Tag(name = "Schedule Management", description = "Schedule management APIs for organizing and categorizing content with schedules")
@Description("Schedule Management Controller - Content scheduleging and categorization APIs")
public class ScheduleRestController extends BaseRestController<ScheduleRequest, ScheduleRestService> {

    private final ScheduleRestService scheduleRestService;

    @ActionAnnotation(title = "Schedule", action = "组织查询", description = "query schedule by org")
    @Operation(summary = "Query Schedules by Organization", description = "Retrieve schedules for the current organization")
    @PreAuthorize(SchedulePermissions.HAS_SCHEDULE_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ScheduleRequest request) {
        
        Page<ScheduleResponse> schedules = scheduleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(schedules));
    }

    @ActionAnnotation(title = "Schedule", action = "用户查询", description = "query schedule by user")
    @Operation(summary = "Query Schedules by User", description = "Retrieve schedules for the current user")
    @PreAuthorize(SchedulePermissions.HAS_SCHEDULE_READ)
    @Override
    public ResponseEntity<?> queryByUser(ScheduleRequest request) {
        
        Page<ScheduleResponse> schedules = scheduleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(schedules));
    }

    @ActionAnnotation(title = "Schedule", action = "查询详情", description = "query schedule by uid")
    @Operation(summary = "Query Schedule by UID", description = "Retrieve a specific schedule by its unique identifier")
    @PreAuthorize(SchedulePermissions.HAS_SCHEDULE_READ)
    @Override
    public ResponseEntity<?> queryByUid(ScheduleRequest request) {
        
        ScheduleResponse schedule = scheduleRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(schedule));
    }

    @ActionAnnotation(title = "Schedule", action = "新建", description = "create schedule")
    @Operation(summary = "Create Schedule", description = "Create a new schedule")
    @Override
    @PreAuthorize(SchedulePermissions.HAS_SCHEDULE_CREATE)
    public ResponseEntity<?> create(ScheduleRequest request) {
        
        ScheduleResponse schedule = scheduleRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(schedule));
    }

    @ActionAnnotation(title = "Schedule", action = "更新", description = "update schedule")
    @Operation(summary = "Update Schedule", description = "Update an existing schedule")
    @Override
    @PreAuthorize(SchedulePermissions.HAS_SCHEDULE_UPDATE)
    public ResponseEntity<?> update(ScheduleRequest request) {
        
        ScheduleResponse schedule = scheduleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(schedule));
    }

    @ActionAnnotation(title = "Schedule", action = "删除", description = "delete schedule")
    @Operation(summary = "Delete Schedule", description = "Delete a schedule")
    @Override
    @PreAuthorize(SchedulePermissions.HAS_SCHEDULE_DELETE)
    public ResponseEntity<?> delete(ScheduleRequest request) {
        
        scheduleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Schedule", action = "导出", description = "export schedule")
    @Operation(summary = "Export Schedules", description = "Export schedules to Excel format")
    @Override
    @PreAuthorize(SchedulePermissions.HAS_SCHEDULE_EXPORT)
    @GetMapping("/export")
    public Object export(ScheduleRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            scheduleRestService,
            ScheduleExcel.class,
            "Schedule",
            "schedule"
        );
    }

    
    
}