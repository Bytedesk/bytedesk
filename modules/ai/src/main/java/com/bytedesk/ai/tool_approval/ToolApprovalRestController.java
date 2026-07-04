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
package com.bytedesk.ai.tool_approval;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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

/**
 * 工具审批管理控制器 - 用于管理工具审批的API接口
 * ToolApproval Management Controller - API endpoints for managing tool approvals
 */
@RestController
@RequestMapping("/api/v1/tool/approval")
@AllArgsConstructor
@Tag(name = "ToolApproval Management", description = "ToolApproval management APIs for organizing and categorizing content with tool_approvals")
@Description("ToolApproval Management Controller - Content tool_approvalging and categorization APIs")
public class ToolApprovalRestController extends BaseRestController<ToolApprovalRequest, ToolApprovalRestService> {

    private final ToolApprovalRestService tool_approvalRestService;

    @ActionAnnotation(title = I18Consts.I18N_TOOL_APPROVAL, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tool_approval by org")
    @Operation(summary = "Query ToolApprovals by Organization", description = "Retrieve tool_approvals for the current organization")
    @PreAuthorize(ToolApprovalPermissions.HAS_TOOL_APPROVAL_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ToolApprovalRequest request) {
        
        Page<ToolApprovalResponse> tool_approvals = tool_approvalRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tool_approvals));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_APPROVAL, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tool_approval by user")
    @Operation(summary = "Query ToolApprovals by User", description = "Retrieve tool_approvals for the current user")
    @PreAuthorize(ToolApprovalPermissions.HAS_TOOL_APPROVAL_READ)
    @Override
    public ResponseEntity<?> queryByUser(ToolApprovalRequest request) {
        
        Page<ToolApprovalResponse> tool_approvals = tool_approvalRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tool_approvals));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_APPROVAL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tool_approval by uid")
    @Operation(summary = "Query ToolApproval by UID", description = "Retrieve a specific tool_approval by its unique identifier")
    @PreAuthorize(ToolApprovalPermissions.HAS_TOOL_APPROVAL_READ)
    @Override
    public ResponseEntity<?> queryByUid(ToolApprovalRequest request) {
        
        ToolApprovalResponse tool_approval = tool_approvalRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tool_approval));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_APPROVAL, action = I18Consts.I18N_ACTION_CREATE, description = "create tool_approval")
    @Operation(summary = "Create ToolApproval", description = "Create a new tool_approval")
    @Override
    @PreAuthorize(ToolApprovalPermissions.HAS_TOOL_APPROVAL_CREATE)
    public ResponseEntity<?> create(ToolApprovalRequest request) {
        
        ToolApprovalResponse tool_approval = tool_approvalRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tool_approval));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_APPROVAL, action = I18Consts.I18N_ACTION_UPDATE, description = "update tool_approval")
    @Operation(summary = "Update ToolApproval", description = "Update an existing tool_approval")
    @Override
    @PreAuthorize(ToolApprovalPermissions.HAS_TOOL_APPROVAL_UPDATE)
    public ResponseEntity<?> update(ToolApprovalRequest request) {
        
        ToolApprovalResponse tool_approval = tool_approvalRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tool_approval));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_APPROVAL, action = I18Consts.I18N_ACTION_DELETE, description = "delete tool_approval")
    @Operation(summary = "Delete ToolApproval", description = "Delete a tool_approval")
    @Override
    @PreAuthorize(ToolApprovalPermissions.HAS_TOOL_APPROVAL_DELETE)
    public ResponseEntity<?> delete(ToolApprovalRequest request) {
        
        tool_approvalRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_APPROVAL, action = I18Consts.I18N_ACTION_EXPORT, description = "export tool_approval")
    @Operation(summary = "Export ToolApprovals", description = "Export tool_approvals to Excel format")
    @Override
    @PreAuthorize(ToolApprovalPermissions.HAS_TOOL_APPROVAL_EXPORT)
    @GetMapping("/export")
    public Object export(ToolApprovalRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tool_approvalRestService,
            ToolApprovalExcel.class,
            "ToolApproval",
            "tool_approval"
        );
    }

    
    
}