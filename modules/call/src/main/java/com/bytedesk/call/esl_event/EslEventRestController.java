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
package com.bytedesk.call.esl_event;

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
@RequestMapping("/api/v1/esl_event")
@AllArgsConstructor
@Tag(name = "EslEvent Management", description = "EslEvent management APIs for organizing and categorizing content with esl_events")
@Description("EslEvent Management Controller - Content esl_eventging and categorization APIs")
public class EslEventRestController extends BaseRestController<EslEventRequest, EslEventRestService> {

    private final EslEventRestService audioFileRestService;

    @ActionAnnotation(title = "EslEvent", action = "组织查询", description = "query esl_event by org")
    @Operation(summary = "Query EslEvents by Organization", description = "Retrieve esl_events for the current organization")
    @PreAuthorize(EslEventPermissions.HAS_ESL_EVENTS_READ)
    @Override
    public ResponseEntity<?> queryByOrg(EslEventRequest request) {
        
        Page<EslEventResponse> esl_events = audioFileRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(esl_events));
    }

    @ActionAnnotation(title = "EslEvent", action = "用户查询", description = "query esl_event by user")
    @Operation(summary = "Query EslEvents by User", description = "Retrieve esl_events for the current user")
    @PreAuthorize(EslEventPermissions.HAS_ESL_EVENTS_READ)
    @Override
    public ResponseEntity<?> queryByUser(EslEventRequest request) {
        
        Page<EslEventResponse> esl_events = audioFileRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(esl_events));
    }

    @ActionAnnotation(title = "EslEvent", action = "查询详情", description = "query esl_event by uid")
    @Operation(summary = "Query EslEvent by UID", description = "Retrieve a specific esl_event by its unique identifier")
    @PreAuthorize(EslEventPermissions.HAS_ESL_EVENTS_READ)
    @Override
    public ResponseEntity<?> queryByUid(EslEventRequest request) {
        
        EslEventResponse esl_event = audioFileRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(esl_event));
    }

    @ActionAnnotation(title = "EslEvent", action = "新建", description = "create esl_event")
    @Operation(summary = "Create EslEvent", description = "Create a new esl_event")
    @Override
    @PreAuthorize(EslEventPermissions.HAS_ESL_EVENTS_CREATE)
    public ResponseEntity<?> create(EslEventRequest request) {
        
        EslEventResponse esl_event = audioFileRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(esl_event));
    }

    @ActionAnnotation(title = "EslEvent", action = "更新", description = "update esl_event")
    @Operation(summary = "Update EslEvent", description = "Update an existing esl_event")
    @Override
    @PreAuthorize(EslEventPermissions.HAS_ESL_EVENTS_UPDATE)
    public ResponseEntity<?> update(EslEventRequest request) {
        
        EslEventResponse esl_event = audioFileRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(esl_event));
    }

    @ActionAnnotation(title = "EslEvent", action = "删除", description = "delete esl_event")
    @Operation(summary = "Delete EslEvent", description = "Delete a esl_event")
    @Override
    @PreAuthorize(EslEventPermissions.HAS_ESL_EVENTS_DELETE)
    public ResponseEntity<?> delete(EslEventRequest request) {
        
        audioFileRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "EslEvent", action = "导出", description = "export esl_event")
    @Operation(summary = "Export EslEvents", description = "Export esl_events to Excel format")
    @Override
    @PreAuthorize(EslEventPermissions.HAS_ESL_EVENTS_EXPORT)
    @GetMapping("/export")
    public Object export(EslEventRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            audioFileRestService,
            EslEventExcel.class,
            "EslEvent",
            "esl_event"
        );
    }

    
    
}