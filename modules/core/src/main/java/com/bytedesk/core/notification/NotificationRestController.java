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
package com.bytedesk.core.notification;

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
@RequestMapping("/api/v1/notification")
@AllArgsConstructor
@Tag(name = "Notification Management", description = "Notification management APIs for organizing and categorizing content with notifications")
@Description("Notification Management Controller - Content notifications and categorization APIs")
public class NotificationRestController extends BaseRestController<NotificationRequest, NotificationRestService> {

    private final NotificationRestService notificationRestService;

    @ActionAnnotation(title = "标签", action = "组织查询", description = "query notification by org")
    @Operation(summary = "Query Notifications by Organization", description = "Retrieve notifications for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(NotificationRequest request) {
        
        Page<NotificationResponse> notifications = notificationRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(notifications));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query notification by user")
    @Operation(summary = "Query Notifications by User", description = "Retrieve notifications for the current user")
    @Override
    public ResponseEntity<?> queryByUser(NotificationRequest request) {
        
        Page<NotificationResponse> notifications = notificationRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(notifications));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query notification by uid")
    @Operation(summary = "Query Notification by UID", description = "Retrieve a specific notification by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(NotificationRequest request) {
        
        NotificationResponse notification = notificationRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create notification")
    @Operation(summary = "Create Notification", description = "Create a new notification")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(NotificationRequest request) {
        
        NotificationResponse notification = notificationRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update notification")
    @Operation(summary = "Update Notification", description = "Update an existing notification")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(NotificationRequest request) {
        
        NotificationResponse notification = notificationRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete notification")
    @Operation(summary = "Delete Notification", description = "Delete a notification")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(NotificationRequest request) {
        
        notificationRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export notification")
    @Operation(summary = "Export Notifications", description = "Export notifications to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(NotificationRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            notificationRestService,
            NotificationExcel.class,
            "标签",
            "notification"
        );
    }

    
    
}