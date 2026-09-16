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
package com.bytedesk.core.sms_template;

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
@RequestMapping("/api/v1/sms_template")
@AllArgsConstructor
@Tag(name = "SmsTemplate Management", description = "SmsTemplate management APIs for organizing and categorizing content with sms_templates")
@Description("SmsTemplate Management Controller - Content sms_templateging and categorization APIs")
public class SmsTemplateRestController extends BaseRestController<SmsTemplateRequest, SmsTemplateRestService> {

    private final SmsTemplateRestService sms_templateRestService;

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query sms_template by org")
    @Operation(summary = "Query SmsTemplates by Organization", description = "Retrieve sms_templates for the current organization")
    @Override
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_READ)
    public ResponseEntity<?> queryByOrg(SmsTemplateRequest request) {
        
        Page<SmsTemplateResponse> sms_templates = sms_templateRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(sms_templates));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query sms_template by user")
    @Operation(summary = "Query SmsTemplates by User", description = "Retrieve sms_templates for the current user")
    @Override
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_READ)
    public ResponseEntity<?> queryByUser(SmsTemplateRequest request) {
        
        Page<SmsTemplateResponse> sms_templates = sms_templateRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(sms_templates));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query sms_template by uid")
    @Operation(summary = "Query SmsTemplate by UID", description = "Retrieve a specific sms_template by its unique identifier")
    @Override
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_READ)
    public ResponseEntity<?> queryByUid(SmsTemplateRequest request) {
        
        SmsTemplateResponse sms_template = sms_templateRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(sms_template));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = I18Consts.I18N_ACTION_CREATE, description = "create sms_template")
    @Operation(summary = "Create SmsTemplate", description = "Create a new sms_template")
    @Override
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_CREATE)
    public ResponseEntity<?> create(SmsTemplateRequest request) {
        
        SmsTemplateResponse sms_template = sms_templateRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(sms_template));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = I18Consts.I18N_ACTION_UPDATE, description = "update sms_template")
    @Operation(summary = "Update SmsTemplate", description = "Update an existing sms_template")
    @Override
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_UPDATE)
    public ResponseEntity<?> update(SmsTemplateRequest request) {
        
        SmsTemplateResponse sms_template = sms_templateRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(sms_template));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = I18Consts.I18N_ACTION_DELETE, description = "delete sms_template")
    @Operation(summary = "Delete SmsTemplate", description = "Delete a sms_template")
    @Override
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_DELETE)
    public ResponseEntity<?> delete(SmsTemplateRequest request) {
        
        sms_templateRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = I18Consts.I18N_ACTION_EXPORT, description = "export sms_template")
    @Operation(summary = "Export SmsTemplates", description = "Export sms_templates to Excel format")
    @Override
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_EXPORT)
    @GetMapping("/export")
    public Object export(SmsTemplateRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            sms_templateRestService,
            SmsTemplateExcel.class,
            "SmsTemplate",
            "sms_template"
        );
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_TEMPLATE, action = "testSms", description = "send test sms by template")
    @Operation(summary = "Send test SMS by template", description = "Send a test SMS using selected provider, template and variables")
    @PostMapping("/test-send")
    @PreAuthorize(SmsTemplatePermissions.HAS_SMS_TEMPLATE_READ)
    public ResponseEntity<?> testSend(@RequestBody SmsTemplateTestRequest request) {
        try {
            return ResponseEntity.ok(sms_templateRestService.sendTestSms(request));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error("测试短信发送失败: " + e.getMessage()));
        }
    }

    
    
}