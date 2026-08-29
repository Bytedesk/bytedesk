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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 通知管理接口
 * 
 * @author Jackning
 * @since 2024-09-01
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification Management", description = "Notification management APIs, including query, create, update, delete, send, and mark read operations")
public class NotificationRestController extends BaseRestController<NotificationRequest, NotificationRestService> {

    private final NotificationRestService notificationRestService;

    private final NotificationService notificationService;

    /**
     * 根据组织查询通知
     * 
     * @param request 查询请求
     * @return 分页通知列表
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg notification")
    @Operation(summary = "Query Notifications by Organization", description = "Retrieve notifications for the current organization")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(NotificationRequest request) {

        Page<NotificationResponse> page = notificationRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 根据用户查询通知
     * 
     * @param request 查询请求
     * @return 分页通知列表
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser notification")
    @Operation(summary = "Query Notifications by User", description = "Retrieve notifications for the current user")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(NotificationRequest request) {

        Page<NotificationResponse> page = notificationRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 根据UID查询通知详情（组织内隔离，超级管理员可查全部）
     * 
     * @param request 查询请求
     * @return 通知详情
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid notification")
    @Operation(summary = "Query Notification by UID", description = "Query the notification by unique identifier")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(NotificationRequest request) {

        NotificationResponse notification = notificationRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    /**
     * 创建通知
     * 
     * @param request 创建通知请求
     * @return 创建的通知
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_CREATE, description = "create notification")
    @Operation(summary = "Create Notification", description = "Create a new notification")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_CREATE)
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody NotificationRequest request) {

        NotificationResponse notification = notificationRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    /**
     * 更新通知
     * 
     * @param request 更新通知请求
     * @return 更新后的通知
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_UPDATE, description = "update notification")
    @Operation(summary = "Update Notification", description = "Update an existing notification")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_UPDATE)
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody NotificationRequest request) {

        NotificationResponse notification = notificationRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(notification));
    }

    /**
     * 删除通知
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_DELETE, description = "delete notification")
    @Operation(summary = "Delete Notification", description = "Delete a notification")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_DELETE)
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody NotificationRequest request) {

        notificationRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * 查询当前用户未读通知数量
     * 
     * @return 未读数量
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_QUERY_UNREAD_COUNT, description = "countUnread notification")
    @Operation(summary = "Query Unread Count", description = "Retrieve the unread notification count for the current user")
    @GetMapping("/query/unread/count")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    public ResponseEntity<?> countUnread() {
        return ResponseEntity.ok(JsonResult.success(notificationRestService.countUnread()));
    }

    /**
     * 发送通知
     * 
     * @param request 发送通知请求
     * @return 发送结果
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_SEND, description = "send notification")
    @Operation(summary = "Send Notification", description = "Dispatch notification to organization, department or user")
    @PostMapping("/send")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_CREATE)
    public ResponseEntity<?> send(@RequestBody NotificationRequest request) {
        NotificationDispatchResponse response = notificationService.dispatchNotification(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 标记通知为已读
     * 
     * @param request 标记请求
     * @return 标记后的通知
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_MARK_AS_READ, description = "markRead notification")
    @Operation(summary = "Mark Notification Read", description = "Mark the specified notification as read")
    @PostMapping("/read")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    public ResponseEntity<?> markRead(@RequestBody NotificationRequest request) {
        NotificationResponse response = notificationRestService.markRead(request.getUid());
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 全部标记已读
     * 
     * @return 处理的未读数量
     */
    @ActionAnnotation(title = I18Consts.I18N_NOTIFICATION, action = I18Consts.I18N_ACTION_MARK_ALL_AS_READ, description = "markAllRead notification")
    @Operation(summary = "Mark All Notifications Read", description = "Mark all unread notifications of the current user as read")
    @PostMapping("/read/all")
    @PreAuthorize(NotificationPermissions.HAS_NOTIFICATION_READ)
    public ResponseEntity<?> markAllRead() {
        return ResponseEntity.ok(JsonResult.success(notificationRestService.markAllRead()));
    }

    /**
     * 导出通知列表（暂未实现）
     */
    @Override
    public Object export(NotificationRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }
    
}
