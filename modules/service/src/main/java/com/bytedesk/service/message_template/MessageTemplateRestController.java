/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-18 16:22:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_template;

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
import org.springframework.context.annotation.Description;

@Tag(name = "Template Management", description = "Template management APIs")
@RestController
@RequestMapping("/api/v1/message/template")
@AllArgsConstructor
@Description("MessageTemplate Management Controller - Message template creation and management APIs")
public class MessageTemplateRestController extends BaseRestController<MessageTemplateRequest, MessageTemplateRestService> {

    private final MessageTemplateRestService templateRestService;

    @PreAuthorize(MessageTemplatePermissions.HAS_MESSAGE_TEMPLATE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_TEMPLATE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg template")
    @Operation(summary = "Query Templates by Organization", description = "Retrieve template list for the organization")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(MessageTemplateRequest request) {
        
        Page<MessageTemplateResponse> templates = templateRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(templates));
    }

    @PreAuthorize(MessageTemplatePermissions.HAS_MESSAGE_TEMPLATE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_TEMPLATE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser template")
    @Operation(summary = "Query Templates by User", description = "Retrieve template list for the user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(MessageTemplateRequest request) {
        
        Page<MessageTemplateResponse> templates = templateRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(templates));
    }

    @PreAuthorize(MessageTemplatePermissions.HAS_MESSAGE_TEMPLATE_READ)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_TEMPLATE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid template")
    @Operation(summary = "Query Template by UID", description = "Retrieve template details by UID")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(MessageTemplateRequest request) {
        
        MessageTemplateResponse template = templateRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @PreAuthorize(MessageTemplatePermissions.HAS_MESSAGE_TEMPLATE_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_TEMPLATE, action = I18Consts.I18N_ACTION_CREATE, description = "create template")
    @Operation(summary = "Create Template", description = "Create a new template")
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody MessageTemplateRequest request) {
        
        MessageTemplateResponse template = templateRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @PreAuthorize(MessageTemplatePermissions.HAS_MESSAGE_TEMPLATE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_TEMPLATE, action = I18Consts.I18N_ACTION_UPDATE, description = "update template")
    @Operation(summary = "Update Template", description = "Update the existing template")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody MessageTemplateRequest request) {
        
        MessageTemplateResponse template = templateRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @PreAuthorize(MessageTemplatePermissions.HAS_MESSAGE_TEMPLATE_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_TEMPLATE, action = I18Consts.I18N_ACTION_DELETE, description = "delete template")
    @Operation(summary = "Delete Template", description = "Delete the specified template")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody MessageTemplateRequest request) {
        
        templateRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/export")
    @PreAuthorize(MessageTemplatePermissions.HAS_MESSAGE_TEMPLATE_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_MESSAGE_TEMPLATE, action = I18Consts.I18N_ACTION_EXPORT, description = "export template")
    @Operation(summary = "Export Templates", description = "Export template data")
    @Override
    public Object export(MessageTemplateRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            templateRestService,
            MessageTemplateExcel.class,
            "模板",
            "template"
        );
    }

    
    
}