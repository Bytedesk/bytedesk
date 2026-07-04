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
package com.bytedesk.ai.tool_audit;

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
 * ToolAudit Management Controller - API endpoints for managing tool audits
 */
@RestController
@RequestMapping("/api/v1/tool/audit")
@AllArgsConstructor
@Tag(name = "ToolAudit Management", description = "ToolAudit management APIs for organizing and categorizing content with tool_audits")
@Description("ToolAudit Management Controller - Content tool_auditging and categorization APIs")
public class ToolAuditRestController extends BaseRestController<ToolAuditRequest, ToolAuditRestService> {

    private final ToolAuditRestService tool_auditRestService;

    @ActionAnnotation(title = I18Consts.I18N_TOOL_AUDIT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tool_audit by org")
    @Operation(summary = "Query ToolAudits by Organization", description = "Retrieve tool_audits for the current organization")
    @PreAuthorize(ToolAuditPermissions.HAS_TOOL_AUDIT_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ToolAuditRequest request) {
        
        Page<ToolAuditResponse> tool_audits = tool_auditRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tool_audits));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_AUDIT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tool_audit by user")
    @Operation(summary = "Query ToolAudits by User", description = "Retrieve tool_audits for the current user")
    @PreAuthorize(ToolAuditPermissions.HAS_TOOL_AUDIT_READ)
    @Override
    public ResponseEntity<?> queryByUser(ToolAuditRequest request) {
        
        Page<ToolAuditResponse> tool_audits = tool_auditRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tool_audits));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_AUDIT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tool_audit by uid")
    @Operation(summary = "Query ToolAudit by UID", description = "Retrieve a specific tool_audit by its unique identifier")
    @PreAuthorize(ToolAuditPermissions.HAS_TOOL_AUDIT_READ)
    @Override
    public ResponseEntity<?> queryByUid(ToolAuditRequest request) {
        
        ToolAuditResponse tool_audit = tool_auditRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tool_audit));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_AUDIT, action = I18Consts.I18N_ACTION_CREATE, description = "create tool_audit")
    @Operation(summary = "Create ToolAudit", description = "Create a new tool_audit")
    @Override
    @PreAuthorize(ToolAuditPermissions.HAS_TOOL_AUDIT_CREATE)
    public ResponseEntity<?> create(ToolAuditRequest request) {
        
        ToolAuditResponse tool_audit = tool_auditRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tool_audit));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_AUDIT, action = I18Consts.I18N_ACTION_UPDATE, description = "update tool_audit")
    @Operation(summary = "Update ToolAudit", description = "Update an existing tool_audit")
    @Override
    @PreAuthorize(ToolAuditPermissions.HAS_TOOL_AUDIT_UPDATE)
    public ResponseEntity<?> update(ToolAuditRequest request) {
        
        ToolAuditResponse tool_audit = tool_auditRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tool_audit));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_AUDIT, action = I18Consts.I18N_ACTION_DELETE, description = "delete tool_audit")
    @Operation(summary = "Delete ToolAudit", description = "Delete a tool_audit")
    @Override
    @PreAuthorize(ToolAuditPermissions.HAS_TOOL_AUDIT_DELETE)
    public ResponseEntity<?> delete(ToolAuditRequest request) {
        
        tool_auditRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_AUDIT, action = I18Consts.I18N_ACTION_EXPORT, description = "export tool_audit")
    @Operation(summary = "Export ToolAudits", description = "Export tool_audits to Excel format")
    @Override
    @PreAuthorize(ToolAuditPermissions.HAS_TOOL_AUDIT_EXPORT)
    @GetMapping("/export")
    public Object export(ToolAuditRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tool_auditRestService,
            ToolAuditExcel.class,
            "ToolAudit",
            "tool_audit"
        );
    }

    
    
}