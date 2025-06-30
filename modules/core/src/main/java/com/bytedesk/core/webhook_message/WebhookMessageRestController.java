/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-06 20:05:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.webhook_message;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/webhook/message")
@AllArgsConstructor
public class WebhookMessageRestController extends BaseRestController<WebhookMessageRequest> {

    private final WebhookMessageRestService webhookRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "WebhookMessage", action = "组织查询", description = "query webhook by org")
    @Override
    public ResponseEntity<?> queryByOrg(WebhookMessageRequest request) {
        
        Page<WebhookMessageResponse> webhooks = webhookRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(webhooks));
    }

    @ActionAnnotation(title = "WebhookMessage", action = "用户查询", description = "query webhook by user")
    @Override
    public ResponseEntity<?> queryByUser(WebhookMessageRequest request) {
        
        Page<WebhookMessageResponse> webhooks = webhookRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(webhooks));
    }

    @ActionAnnotation(title = "WebhookMessage", action = "查询详情", description = "query webhook by uid")
    @Override
    public ResponseEntity<?> queryByUid(WebhookMessageRequest request) {
        
        WebhookMessageResponse webhook = webhookRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(webhook));
    }

    @ActionAnnotation(title = "WebhookMessage", action = "新建", description = "create webhook")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(WebhookMessageRequest request) {
        
        WebhookMessageResponse webhook = webhookRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(webhook));
    }

    @ActionAnnotation(title = "WebhookMessage", action = "更新", description = "update webhook")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(WebhookMessageRequest request) {
        
        WebhookMessageResponse webhook = webhookRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(webhook));
    }

    @ActionAnnotation(title = "WebhookMessage", action = "删除", description = "delete webhook")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(WebhookMessageRequest request) {
        
        webhookRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "WebhookMessage", action = "导出", description = "export webhook")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(WebhookMessageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            webhookRestService,
            WebhookMessageExcel.class,
            "WebhookMessage",
            "webhook"
        );
    }

    
    
}