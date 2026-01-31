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
package com.bytedesk.crm.lead_follow;

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
@RequestMapping("/api/v1/lead_follow")
@AllArgsConstructor
@Tag(name = "LeadFollow Management", description = "LeadFollow management APIs for organizing and categorizing content with lead_follows")
@Description("LeadFollow Management Controller - Content lead_followging and categorization APIs")
public class LeadFollowRestController extends BaseRestController<LeadFollowRequest, LeadFollowRestService> {

    private final LeadFollowRestService lead_followRestService;

    @ActionAnnotation(title = "LeadFollow", action = "组织查询", description = "query lead_follow by org")
    @Operation(summary = "Query LeadFollows by Organization", description = "Retrieve lead_follows for the current organization")
    @PreAuthorize(LeadFollowPermissions.HAS_LEAD_FOLLOW_READ)
    @Override
    public ResponseEntity<?> queryByOrg(LeadFollowRequest request) {
        
        Page<LeadFollowResponse> lead_follows = lead_followRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(lead_follows));
    }

    @ActionAnnotation(title = "LeadFollow", action = "用户查询", description = "query lead_follow by user")
    @Operation(summary = "Query LeadFollows by User", description = "Retrieve lead_follows for the current user")
    @PreAuthorize(LeadFollowPermissions.HAS_LEAD_FOLLOW_READ)
    @Override
    public ResponseEntity<?> queryByUser(LeadFollowRequest request) {
        
        Page<LeadFollowResponse> lead_follows = lead_followRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(lead_follows));
    }

    @ActionAnnotation(title = "LeadFollow", action = "查询详情", description = "query lead_follow by uid")
    @Operation(summary = "Query LeadFollow by UID", description = "Retrieve a specific lead_follow by its unique identifier")
    @PreAuthorize(LeadFollowPermissions.HAS_LEAD_FOLLOW_READ)
    @Override
    public ResponseEntity<?> queryByUid(LeadFollowRequest request) {
        
        LeadFollowResponse lead_follow = lead_followRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(lead_follow));
    }

    @ActionAnnotation(title = "LeadFollow", action = "新建", description = "create lead_follow")
    @Operation(summary = "Create LeadFollow", description = "Create a new lead_follow")
    @Override
    @PreAuthorize(LeadFollowPermissions.HAS_LEAD_FOLLOW_CREATE)
    public ResponseEntity<?> create(LeadFollowRequest request) {
        
        LeadFollowResponse lead_follow = lead_followRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(lead_follow));
    }

    @ActionAnnotation(title = "LeadFollow", action = "更新", description = "update lead_follow")
    @Operation(summary = "Update LeadFollow", description = "Update an existing lead_follow")
    @Override
    @PreAuthorize(LeadFollowPermissions.HAS_LEAD_FOLLOW_UPDATE)
    public ResponseEntity<?> update(LeadFollowRequest request) {
        
        LeadFollowResponse lead_follow = lead_followRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(lead_follow));
    }

    @ActionAnnotation(title = "LeadFollow", action = "删除", description = "delete lead_follow")
    @Operation(summary = "Delete LeadFollow", description = "Delete a lead_follow")
    @Override
    @PreAuthorize(LeadFollowPermissions.HAS_LEAD_FOLLOW_DELETE)
    public ResponseEntity<?> delete(LeadFollowRequest request) {
        
        lead_followRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "LeadFollow", action = "导出", description = "export lead_follow")
    @Operation(summary = "Export LeadFollows", description = "Export lead_follows to Excel format")
    @Override
    @PreAuthorize(LeadFollowPermissions.HAS_LEAD_FOLLOW_EXPORT)
    @GetMapping("/export")
    public Object export(LeadFollowRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            lead_followRestService,
            LeadFollowExcel.class,
            "LeadFollow",
            "lead_follow"
        );
    }

    
    
}