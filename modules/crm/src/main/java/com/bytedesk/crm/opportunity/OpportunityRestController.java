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
package com.bytedesk.crm.opportunity;

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
@RequestMapping("/api/v1/opportunity")
@AllArgsConstructor
@Tag(name = "Opportunity Management", description = "Opportunity management APIs for organizing and categorizing content with opportunitys")
@Description("Opportunity Management Controller - Content opportunityging and categorization APIs")
public class OpportunityRestController extends BaseRestController<OpportunityRequest, OpportunityRestService> {

    private final OpportunityRestService opportunityRestService;

    @ActionAnnotation(title = "Opportunity", action = "组织查询", description = "query opportunity by org")
    @Operation(summary = "Query Opportunitys by Organization", description = "Retrieve opportunitys for the current organization")
    @PreAuthorize(OpportunityPermissions.HAS_OPPORTUNITY_READ)
    @Override
    public ResponseEntity<?> queryByOrg(OpportunityRequest request) {
        
        Page<OpportunityResponse> opportunitys = opportunityRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(opportunitys));
    }

    @ActionAnnotation(title = "Opportunity", action = "用户查询", description = "query opportunity by user")
    @Operation(summary = "Query Opportunitys by User", description = "Retrieve opportunitys for the current user")
    @PreAuthorize(OpportunityPermissions.HAS_OPPORTUNITY_READ)
    @Override
    public ResponseEntity<?> queryByUser(OpportunityRequest request) {
        
        Page<OpportunityResponse> opportunitys = opportunityRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(opportunitys));
    }

    @ActionAnnotation(title = "Opportunity", action = "查询详情", description = "query opportunity by uid")
    @Operation(summary = "Query Opportunity by UID", description = "Retrieve a specific opportunity by its unique identifier")
    @PreAuthorize(OpportunityPermissions.HAS_OPPORTUNITY_READ)
    @Override
    public ResponseEntity<?> queryByUid(OpportunityRequest request) {
        
        OpportunityResponse opportunity = opportunityRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(opportunity));
    }

    @ActionAnnotation(title = "Opportunity", action = "新建", description = "create opportunity")
    @Operation(summary = "Create Opportunity", description = "Create a new opportunity")
    @Override
    @PreAuthorize(OpportunityPermissions.HAS_OPPORTUNITY_CREATE)
    public ResponseEntity<?> create(OpportunityRequest request) {
        
        OpportunityResponse opportunity = opportunityRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(opportunity));
    }

    @ActionAnnotation(title = "Opportunity", action = "更新", description = "update opportunity")
    @Operation(summary = "Update Opportunity", description = "Update an existing opportunity")
    @Override
    @PreAuthorize(OpportunityPermissions.HAS_OPPORTUNITY_UPDATE)
    public ResponseEntity<?> update(OpportunityRequest request) {
        
        OpportunityResponse opportunity = opportunityRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(opportunity));
    }

    @ActionAnnotation(title = "Opportunity", action = "删除", description = "delete opportunity")
    @Operation(summary = "Delete Opportunity", description = "Delete a opportunity")
    @Override
    @PreAuthorize(OpportunityPermissions.HAS_OPPORTUNITY_DELETE)
    public ResponseEntity<?> delete(OpportunityRequest request) {
        
        opportunityRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Opportunity", action = "导出", description = "export opportunity")
    @Operation(summary = "Export Opportunitys", description = "Export opportunitys to Excel format")
    @Override
    @PreAuthorize(OpportunityPermissions.HAS_OPPORTUNITY_EXPORT)
    @GetMapping("/export")
    public Object export(OpportunityRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            opportunityRestService,
            OpportunityExcel.class,
            "Opportunity",
            "opportunity"
        );
    }

    
    
}