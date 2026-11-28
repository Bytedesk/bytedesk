/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:36:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.push.service.PushSendService;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
@Tag(name = "Push Notification Management", description = "Push notification management APIs for sending and managing push notifications")
@Description("Push Notification Controller - Push notification and messaging APIs")
public class PushRestController extends BaseRestController<PushRequest, PushRestService> {

    private final PushRestService pushRestService;
    
    private final PushSendService pushSendService;

    @ActionAnnotation(title = I18Consts.I18N_PUSH, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query push")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Push Notifications by Organization", description = "Retrieve push notifications for the current organization (Admin only)")
    @Override
    public ResponseEntity<?> queryByOrg(PushRequest request) {

        Page<PushResponse> page = pushRestService.queryByOrg(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query push")
    @Operation(summary = "Query Push Notifications by User", description = "Retrieve push notifications for the current user")
    @Override
    public ResponseEntity<?> queryByUser(PushRequest request) {
        
        Page<PushResponse> page = pushRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH, action = I18Consts.I18N_ACTION_CREATE, description = "create push")
    @Operation(summary = "Create Push Notification", description = "Create and send a new push notification")
    @Override
    public ResponseEntity<?> create(PushRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(pushRestService.create(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH, action = I18Consts.I18N_ACTION_UPDATE, description = "update push")
    @Operation(summary = "Update Push Notification", description = "Update an existing push notification")
    @Override
    public ResponseEntity<?> update(PushRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(pushRestService.update(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH, action = I18Consts.I18N_ACTION_DELETE, description = "delete push")
    @Operation(summary = "Delete Push Notification", description = "Delete a push notification")
    @Override
    public ResponseEntity<?> delete(PushRequest request) {

        pushRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Resend Push Notification", description = "Resend a failed push notification with the same verification code")
    @RequestMapping("/resend")
    public ResponseEntity<?> resend(PushRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(pushSendService.resend(request)));
    }

    @Operation(summary = "Export Push Records", description = "Export push records to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_PUSH, action = I18Consts.I18N_ACTION_EXPORT, description = "export push")
    @Override
    public Object export(PushRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Push Records by UID", description = "Retrieve a specific push record entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_PUSH, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query push")
    @Override
    public ResponseEntity<?> queryByUid(PushRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
