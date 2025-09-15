/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 13:49:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.complaint;

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
@RequestMapping("/api/v1/complaint")
@AllArgsConstructor
@Tag(name = "Complaint Management", description = "Complaint management APIs for organizing and categorizing content with complaints")
@Description("Complaint Management Controller - Content tag and categorization APIs")
public class ComplaintRestController extends BaseRestController<ComplaintRequest, ComplaintRestService> {

    private final ComplaintRestService complaintRestService;

    @ActionAnnotation(title = "标签", action = "组织查询", description = "query complaint by org")
    @Operation(summary = "Query Complaints by Organization", description = "Retrieve complaints for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ComplaintRequest request) {
        
        Page<ComplaintResponse> complaints = complaintRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(complaints));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query complaint by user")
    @Operation(summary = "Query Complaints by User", description = "Retrieve complaints for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ComplaintRequest request) {
        
        Page<ComplaintResponse> complaints = complaintRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(complaints));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query complaint by uid")
    @Operation(summary = "Query Complaint by UID", description = "Retrieve a specific complaint by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ComplaintRequest request) {
        
        ComplaintResponse complaint = complaintRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(complaint));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create complaint")
    @Operation(summary = "Create Complaint", description = "Create a new complaint")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(ComplaintRequest request) {
        
        ComplaintResponse complaint = complaintRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(complaint));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update complaint")
    @Operation(summary = "Update Complaint", description = "Update an existing complaint")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(ComplaintRequest request) {
        
        ComplaintResponse complaint = complaintRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(complaint));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete complaint")
    @Operation(summary = "Delete Complaint", description = "Delete a complaint")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(ComplaintRequest request) {
        
        complaintRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export complaint")
    @Operation(summary = "Export Complaints", description = "Export complaints to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(ComplaintRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            complaintRestService,
            ComplaintExcel.class,
            "标签",
            "complaint"
        );
    }

    
    
}