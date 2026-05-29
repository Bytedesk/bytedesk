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
package com.bytedesk.core.email_push;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/email_push")
@AllArgsConstructor
@Tag(name = "EmailPush Management", description = "EmailPush management APIs for organizing and categorizing content with email_pushs")
@Description("EmailPush Management Controller - Content email_pushging and categorization APIs")
public class EmailPushRestController extends BaseRestController<EmailPushRequest, EmailPushRestService> {

    private final EmailPushRestService emailPushRestService;

    @ActionAnnotation(title = I18Consts.I18N_EMAIL_PUSH, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query email_push by org")
    @Operation(summary = "Query EmailPushs by Organization", description = "Retrieve email_pushs for the current organization")
    @PreAuthorize(EmailPushPermissions.HAS_EMAIL_PUSH_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(EmailPushRequest request) {
        
        Page<EmailPushResponse> email_pushs = emailPushRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(email_pushs));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL_PUSH, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query email_push by user")
    @Operation(summary = "Query EmailPushs by User", description = "Retrieve email_pushs for the current user")
    @PreAuthorize(EmailPushPermissions.HAS_EMAIL_PUSH_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(EmailPushRequest request) {
        
        Page<EmailPushResponse> email_pushs = emailPushRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(email_pushs));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL_PUSH, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query email_push by uid")
    @Operation(summary = "Query EmailPush by UID", description = "Retrieve a specific email_push by its unique identifier")
    @PreAuthorize(EmailPushPermissions.HAS_EMAIL_PUSH_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(EmailPushRequest request) {
        
        EmailPushResponse email_push = emailPushRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(email_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL_PUSH, action = I18Consts.I18N_ACTION_CREATE, description = "create email_push")
    @Operation(summary = "Create EmailPush", description = "Create a new email_push")
    @Override
    @PreAuthorize(EmailPushPermissions.HAS_EMAIL_PUSH_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EmailPushRequest request) {
        
        EmailPushResponse email_push = emailPushRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(email_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL_PUSH, action = I18Consts.I18N_ACTION_UPDATE, description = "update email_push")
    @Operation(summary = "Update EmailPush", description = "Update an existing email_push")
    @Override
    @PreAuthorize(EmailPushPermissions.HAS_EMAIL_PUSH_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody EmailPushRequest request) {
        
        EmailPushResponse email_push = emailPushRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(email_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL_PUSH, action = I18Consts.I18N_ACTION_DELETE, description = "delete email_push")
    @Operation(summary = "Delete EmailPush", description = "Delete a email_push")
    @Override
    @PreAuthorize(EmailPushPermissions.HAS_EMAIL_PUSH_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody EmailPushRequest request) {
        
        emailPushRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_EMAIL_PUSH, action = I18Consts.I18N_ACTION_EXPORT, description = "export email_push")
    @Operation(summary = "Export EmailPushs", description = "Export email_pushs to Excel format")
    @Override
    @PreAuthorize(EmailPushPermissions.HAS_EMAIL_PUSH_EXPORT)
    @GetMapping("/export")
    public Object export(EmailPushRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            emailPushRestService,
            EmailPushExcel.class,
            "EmailPush",
            "email_push"
        );
    }

    
    
}