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
package com.bytedesk.crm.lead;

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
@RequestMapping("/api/v1/lead")
@AllArgsConstructor
@Tag(name = "Lead Management", description = "Lead management APIs for organizing and categorizing content with leads")
@Description("Lead Management Controller - Content leadging and categorization APIs")
public class LeadRestController extends BaseRestController<LeadRequest, LeadRestService> {

    private final LeadRestService leadRestService;

    @ActionAnnotation(title = "Lead", action = "组织查询", description = "query lead by org")
    @Operation(summary = "Query Leads by Organization", description = "Retrieve leads for the current organization")
    @PreAuthorize(LeadPermissions.HAS_LEAD_READ)
    @Override
    public ResponseEntity<?> queryByOrg(LeadRequest request) {
        
        Page<LeadResponse> leads = leadRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(leads));
    }

    @ActionAnnotation(title = "Lead", action = "用户查询", description = "query lead by user")
    @Operation(summary = "Query Leads by User", description = "Retrieve leads for the current user")
    @PreAuthorize(LeadPermissions.HAS_LEAD_READ)
    @Override
    public ResponseEntity<?> queryByUser(LeadRequest request) {
        
        Page<LeadResponse> leads = leadRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(leads));
    }

    @ActionAnnotation(title = "Lead", action = "查询详情", description = "query lead by uid")
    @Operation(summary = "Query Lead by UID", description = "Retrieve a specific lead by its unique identifier")
    @PreAuthorize(LeadPermissions.HAS_LEAD_READ)
    @Override
    public ResponseEntity<?> queryByUid(LeadRequest request) {
        
        LeadResponse lead = leadRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(lead));
    }

    @ActionAnnotation(title = "Lead", action = "新建", description = "create lead")
    @Operation(summary = "Create Lead", description = "Create a new lead")
    @Override
    @PreAuthorize(LeadPermissions.HAS_LEAD_CREATE)
    public ResponseEntity<?> create(LeadRequest request) {
        
        LeadResponse lead = leadRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(lead));
    }

    @ActionAnnotation(title = "Lead", action = "更新", description = "update lead")
    @Operation(summary = "Update Lead", description = "Update an existing lead")
    @Override
    @PreAuthorize(LeadPermissions.HAS_LEAD_UPDATE)
    public ResponseEntity<?> update(LeadRequest request) {
        
        LeadResponse lead = leadRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(lead));
    }

    @ActionAnnotation(title = "Lead", action = "删除", description = "delete lead")
    @Operation(summary = "Delete Lead", description = "Delete a lead")
    @Override
    @PreAuthorize(LeadPermissions.HAS_LEAD_DELETE)
    public ResponseEntity<?> delete(LeadRequest request) {
        
        leadRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Lead", action = "导出", description = "export lead")
    @Operation(summary = "Export Leads", description = "Export leads to Excel format")
    @Override
    @PreAuthorize(LeadPermissions.HAS_LEAD_EXPORT)
    @GetMapping("/export")
    public Object export(LeadRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            leadRestService,
            LeadExcel.class,
            "Lead",
            "lead"
        );
    }

    
    
}