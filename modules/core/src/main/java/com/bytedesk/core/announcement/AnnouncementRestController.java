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
package com.bytedesk.core.announcement;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/announcement")
@AllArgsConstructor
@Tag(name = "Announcement Management", description = "Announcement management APIs for organizing and categorizing content with announcements")
@Description("Announcement Management Controller - Content announcements and categorization APIs")
public class AnnouncementRestController extends BaseRestController<AnnouncementRequest, AnnouncementRestService> {

    private final AnnouncementRestService announcementRestService;

    @ActionAnnotation(title = "标签", action = "组织查询", description = "query announcement by org")
    @Operation(summary = "Query Announcements by Organization", description = "Retrieve announcements for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(AnnouncementRequest request) {
        
        Page<AnnouncementResponse> announcements = announcementRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(announcements));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query announcement by user")
    @Operation(summary = "Query Announcements by User", description = "Retrieve announcements for the current user")
    @Override
    public ResponseEntity<?> queryByUser(AnnouncementRequest request) {
        
        Page<AnnouncementResponse> announcements = announcementRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(announcements));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query announcement by uid")
    @Operation(summary = "Query Announcement by UID", description = "Retrieve a specific announcement by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(AnnouncementRequest request) {
        
        AnnouncementResponse announcement = announcementRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(announcement));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create announcement")
    @Operation(summary = "Create Announcement", description = "Create a new announcement")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(AnnouncementRequest request) {
        
        AnnouncementResponse announcement = announcementRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(announcement));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update announcement")
    @Operation(summary = "Update Announcement", description = "Update an existing announcement")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(AnnouncementRequest request) {
        
        AnnouncementResponse announcement = announcementRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(announcement));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete announcement")
    @Operation(summary = "Delete Announcement", description = "Delete a announcement")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(AnnouncementRequest request) {
        
        announcementRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export announcement")
    @Operation(summary = "Export Announcements", description = "Export announcements to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(AnnouncementRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            announcementRestService,
            AnnouncementExcel.class,
            "标签",
            "announcement"
        );
    }

    
    
}