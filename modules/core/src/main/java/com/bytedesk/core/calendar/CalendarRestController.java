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
package com.bytedesk.core.calendar;

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
@RequestMapping("/api/v1/calendar")
@AllArgsConstructor
@Tag(name = "Calendar Management", description = "Calendar management APIs for organizing and categorizing content with calendars")
@Description("Calendar Management Controller - Content calendarging and categorization APIs")
public class CalendarRestController extends BaseRestController<CalendarRequest, CalendarRestService> {

    private final CalendarRestService calendarRestService;

    @ActionAnnotation(title = "Calendar", action = "组织查询", description = "query calendar by org")
    @Operation(summary = "Query Calendars by Organization", description = "Retrieve calendars for the current organization")
    @PreAuthorize(CalendarPermissions.HAS_CALENDAR_READ)
    @Override
    public ResponseEntity<?> queryByOrg(CalendarRequest request) {
        
        Page<CalendarResponse> calendars = calendarRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(calendars));
    }

    @ActionAnnotation(title = "Calendar", action = "用户查询", description = "query calendar by user")
    @Operation(summary = "Query Calendars by User", description = "Retrieve calendars for the current user")
    @PreAuthorize(CalendarPermissions.HAS_CALENDAR_READ)
    @Override
    public ResponseEntity<?> queryByUser(CalendarRequest request) {
        
        Page<CalendarResponse> calendars = calendarRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(calendars));
    }

    @ActionAnnotation(title = "Calendar", action = "查询详情", description = "query calendar by uid")
    @Operation(summary = "Query Calendar by UID", description = "Retrieve a specific calendar by its unique identifier")
    @PreAuthorize(CalendarPermissions.HAS_CALENDAR_READ)
    @Override
    public ResponseEntity<?> queryByUid(CalendarRequest request) {
        
        CalendarResponse calendar = calendarRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(calendar));
    }

    @ActionAnnotation(title = "Calendar", action = "新建", description = "create calendar")
    @Operation(summary = "Create Calendar", description = "Create a new calendar")
    @Override
    @PreAuthorize(CalendarPermissions.HAS_CALENDAR_CREATE)
    public ResponseEntity<?> create(CalendarRequest request) {
        
        CalendarResponse calendar = calendarRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(calendar));
    }

    @ActionAnnotation(title = "Calendar", action = "更新", description = "update calendar")
    @Operation(summary = "Update Calendar", description = "Update an existing calendar")
    @Override
    @PreAuthorize(CalendarPermissions.HAS_CALENDAR_UPDATE)
    public ResponseEntity<?> update(CalendarRequest request) {
        
        CalendarResponse calendar = calendarRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(calendar));
    }

    @ActionAnnotation(title = "Calendar", action = "删除", description = "delete calendar")
    @Operation(summary = "Delete Calendar", description = "Delete a calendar")
    @Override
    @PreAuthorize(CalendarPermissions.HAS_CALENDAR_DELETE)
    public ResponseEntity<?> delete(CalendarRequest request) {
        
        calendarRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Calendar", action = "导出", description = "export calendar")
    @Operation(summary = "Export Calendars", description = "Export calendars to Excel format")
    @Override
    @PreAuthorize(CalendarPermissions.HAS_CALENDAR_EXPORT)
    @GetMapping("/export")
    public Object export(CalendarRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            calendarRestService,
            CalendarExcel.class,
            "Calendar",
            "calendar"
        );
    }

    
    
}