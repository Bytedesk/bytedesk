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
package com.bytedesk.kbase.trigger;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/trigger")
@AllArgsConstructor
@Tag(name = "Trigger Management", description = "Trigger management APIs for organizing and categorizing content with triggers")
@Description("Trigger Management Controller - Content triggerging and categorization APIs")
public class TriggerRestController extends BaseRestController<TriggerRequest, TriggerRestService> {

    private final TriggerRestService triggerRestService;

    @ActionAnnotation(title = "Trigger", action = "组织查询", description = "query trigger by org")
    @Operation(summary = "Query Triggers by Organization", description = "Retrieve triggers for the current organization")
    // @PreAuthorize(TriggerPermissions.HAS_TRIGGER_READ)
    @Override
    public ResponseEntity<?> queryByOrg(TriggerRequest request) {
        
        Page<TriggerResponse> triggers = triggerRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(triggers));
    }

    @ActionAnnotation(title = "Trigger", action = "用户查询", description = "query trigger by user")
    @Operation(summary = "Query Triggers by User", description = "Retrieve triggers for the current user")
    // @PreAuthorize(TriggerPermissions.HAS_TRIGGER_READ)
    @Override
    public ResponseEntity<?> queryByUser(TriggerRequest request) {
        
        Page<TriggerResponse> triggers = triggerRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(triggers));
    }

    @ActionAnnotation(title = "Trigger", action = "查询详情", description = "query trigger by uid")
    @Operation(summary = "Query Trigger by UID", description = "Retrieve a specific trigger by its unique identifier")
    // @PreAuthorize(TriggerPermissions.HAS_TRIGGER_READ)
    @Override
    public ResponseEntity<?> queryByUid(TriggerRequest request) {
        
        TriggerResponse trigger = triggerRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(trigger));
    }

    @ActionAnnotation(title = "Trigger", action = "新建", description = "create trigger")
    @Operation(summary = "Create Trigger", description = "Create a new trigger")
    @Override
    // @PreAuthorize(TriggerPermissions.HAS_TRIGGER_CREATE)
    public ResponseEntity<?> create(TriggerRequest request) {
        
        TriggerResponse trigger = triggerRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(trigger));
    }

    @ActionAnnotation(title = "Trigger", action = "更新", description = "update trigger")
    @Operation(summary = "Update Trigger", description = "Update an existing trigger")
    @Override
    // @PreAuthorize(TriggerPermissions.HAS_TRIGGER_UPDATE)
    public ResponseEntity<?> update(TriggerRequest request) {
        
        TriggerResponse trigger = triggerRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(trigger));
    }

    @ActionAnnotation(title = "Trigger", action = "删除", description = "delete trigger")
    @Operation(summary = "Delete Trigger", description = "Delete a trigger")
    @Override
    // @PreAuthorize(TriggerPermissions.HAS_TRIGGER_DELETE)
    public ResponseEntity<?> delete(TriggerRequest request) {
        
        triggerRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Trigger", action = "导出", description = "export trigger")
    @Operation(summary = "Export Triggers", description = "Export triggers to Excel format")
    @Override
    // @PreAuthorize(TriggerPermissions.HAS_TRIGGER_EXPORT)
    @GetMapping("/export")
    public Object export(TriggerRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            triggerRestService,
            TriggerExcel.class,
            "Trigger",
            "trigger"
        );
    }

    
    
}