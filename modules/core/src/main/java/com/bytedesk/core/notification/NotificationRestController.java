/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:36:34
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Notification management APIs for admin inbox notifications")
@Description("Notification Management Controller - Admin inbox notification management APIs")
public class NotificationRestController extends BaseRestController<NotificationRequest, NotificationRestService> {

    private final NotificationRestService notificationRestService;

    private final NotificationService notificationService;

    @Operation(summary = "Query Notifications by Organization", description = "Retrieve notifications for the current organization")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    @Override
    public ResponseEntity<?> queryByOrg(NotificationRequest request) {

        Page<NotificationResponse> page = notificationRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Notifications by User", description = "Retrieve notifications for the current user")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    @Override
    public ResponseEntity<?> queryByUser(NotificationRequest request) {

        Page<NotificationResponse> page = notificationRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Create Notification", description = "Create a new notification")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_CREATE)
    @Override
    public ResponseEntity<?> create(NotificationRequest request) {

        NotificationResponse notification = notificationRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    @Operation(summary = "Update Notification", description = "Update an existing notification")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_UPDATE)
    @Override
    public ResponseEntity<?> update(NotificationRequest request) {

        NotificationResponse notification = notificationRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    @Operation(summary = "Delete Notification", description = "Delete a notification")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_DELETE)
    @Override
    public ResponseEntity<?> delete(NotificationRequest request) {

        notificationRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/query/unread/count")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    public ResponseEntity<?> countUnread() {
        return ResponseEntity.ok(JsonResult.success(notificationRestService.countUnread()));
    }

    @PostMapping("/send")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_CREATE)
    public ResponseEntity<?> send(@RequestBody NotificationRequest request) {
        NotificationDispatchResponse response = notificationService.dispatchNotification(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/read")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    public ResponseEntity<?> markRead(@RequestBody NotificationRequest request) {
        NotificationResponse response = notificationRestService.markRead(request.getUid());
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/read/all")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    public ResponseEntity<?> markAllRead() {
        return ResponseEntity.ok(JsonResult.success(notificationRestService.markAllRead()));
    }

    @Override
    public Object export(NotificationRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    public ResponseEntity<?> queryByUid(NotificationRequest request) {
        NotificationResponse notification = notificationRestService.queryCurrentUserNotification(request.getUid());
        return ResponseEntity.ok(JsonResult.success(notification));
    }
    
}
