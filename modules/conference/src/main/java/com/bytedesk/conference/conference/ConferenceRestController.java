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
package com.bytedesk.conference.conference;

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
@RequestMapping("/api/v1/conference")
@AllArgsConstructor
@Tag(name = "Conference Management", description = "Conference management APIs for organizing and categorizing content with conferences")
@Description("Conference Management Controller - Content conferenceging and categorization APIs")
public class ConferenceRestController extends BaseRestController<ConferenceRequest, ConferenceRestService> {

    private final ConferenceRestService conferenceRestService;

    @ActionAnnotation(title = "Conference", action = "组织查询", description = "query conference by org")
    @Operation(summary = "Query Conferences by Organization", description = "Retrieve conferences for the current organization")
    @PreAuthorize(ConferencePermissions.HAS_CONFERENCE_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ConferenceRequest request) {
        
        Page<ConferenceResponse> conferences = conferenceRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(conferences));
    }

    @ActionAnnotation(title = "Conference", action = "用户查询", description = "query conference by user")
    @Operation(summary = "Query Conferences by User", description = "Retrieve conferences for the current user")
    @PreAuthorize(ConferencePermissions.HAS_CONFERENCE_READ)
    @Override
    public ResponseEntity<?> queryByUser(ConferenceRequest request) {
        
        Page<ConferenceResponse> conferences = conferenceRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(conferences));
    }

    @ActionAnnotation(title = "Conference", action = "查询详情", description = "query conference by uid")
    @Operation(summary = "Query Conference by UID", description = "Retrieve a specific conference by its unique identifier")
    @PreAuthorize(ConferencePermissions.HAS_CONFERENCE_READ)
    @Override
    public ResponseEntity<?> queryByUid(ConferenceRequest request) {
        
        ConferenceResponse conference = conferenceRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(conference));
    }

    @ActionAnnotation(title = "Conference", action = "新建", description = "create conference")
    @Operation(summary = "Create Conference", description = "Create a new conference")
    @Override
    @PreAuthorize(ConferencePermissions.HAS_CONFERENCE_CREATE)
    public ResponseEntity<?> create(ConferenceRequest request) {
        
        ConferenceResponse conference = conferenceRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(conference));
    }

    @ActionAnnotation(title = "Conference", action = "更新", description = "update conference")
    @Operation(summary = "Update Conference", description = "Update an existing conference")
    @Override
    @PreAuthorize(ConferencePermissions.HAS_CONFERENCE_UPDATE)
    public ResponseEntity<?> update(ConferenceRequest request) {
        
        ConferenceResponse conference = conferenceRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(conference));
    }

    @ActionAnnotation(title = "Conference", action = "删除", description = "delete conference")
    @Operation(summary = "Delete Conference", description = "Delete a conference")
    @Override
    @PreAuthorize(ConferencePermissions.HAS_CONFERENCE_DELETE)
    public ResponseEntity<?> delete(ConferenceRequest request) {
        
        conferenceRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Conference", action = "导出", description = "export conference")
    @Operation(summary = "Export Conferences", description = "Export conferences to Excel format")
    @Override
    @PreAuthorize(ConferencePermissions.HAS_CONFERENCE_EXPORT)
    @GetMapping("/export")
    public Object export(ConferenceRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            conferenceRestService,
            ConferenceExcel.class,
            "Conference",
            "conference"
        );
    }

    
    
}