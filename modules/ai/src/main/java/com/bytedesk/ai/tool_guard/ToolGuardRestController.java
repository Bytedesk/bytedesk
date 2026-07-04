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
package com.bytedesk.ai.tool_guard;

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
 * ToolGuard Management Controller - API endpoints for managing tool guards
 */
@RestController
@RequestMapping("/api/v1/tool/guard")
@AllArgsConstructor
@Tag(name = "ToolGuard Management", description = "ToolGuard management APIs for organizing and categorizing content with tool_guards")
@Description("ToolGuard Management Controller - Content tool_guardging and categorization APIs")
public class ToolGuardRestController extends BaseRestController<ToolGuardRequest, ToolGuardRestService> {

    private final ToolGuardRestService tool_guardRestService;

    @ActionAnnotation(title = I18Consts.I18N_TOOL_GUARD, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tool_guard by org")
    @Operation(summary = "Query ToolGuards by Organization", description = "Retrieve tool_guards for the current organization")
    @PreAuthorize(ToolGuardPermissions.HAS_TOOL_GUARD_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ToolGuardRequest request) {
        
        Page<ToolGuardResponse> tool_guards = tool_guardRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tool_guards));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_GUARD, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tool_guard by user")
    @Operation(summary = "Query ToolGuards by User", description = "Retrieve tool_guards for the current user")
    @PreAuthorize(ToolGuardPermissions.HAS_TOOL_GUARD_READ)
    @Override
    public ResponseEntity<?> queryByUser(ToolGuardRequest request) {
        
        Page<ToolGuardResponse> tool_guards = tool_guardRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tool_guards));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_GUARD, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tool_guard by uid")
    @Operation(summary = "Query ToolGuard by UID", description = "Retrieve a specific tool_guard by its unique identifier")
    @PreAuthorize(ToolGuardPermissions.HAS_TOOL_GUARD_READ)
    @Override
    public ResponseEntity<?> queryByUid(ToolGuardRequest request) {
        
        ToolGuardResponse tool_guard = tool_guardRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tool_guard));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_GUARD, action = I18Consts.I18N_ACTION_CREATE, description = "create tool_guard")
    @Operation(summary = "Create ToolGuard", description = "Create a new tool_guard")
    @Override
    @PreAuthorize(ToolGuardPermissions.HAS_TOOL_GUARD_CREATE)
    public ResponseEntity<?> create(ToolGuardRequest request) {
        
        ToolGuardResponse tool_guard = tool_guardRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tool_guard));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_GUARD, action = I18Consts.I18N_ACTION_UPDATE, description = "update tool_guard")
    @Operation(summary = "Update ToolGuard", description = "Update an existing tool_guard")
    @Override
    @PreAuthorize(ToolGuardPermissions.HAS_TOOL_GUARD_UPDATE)
    public ResponseEntity<?> update(ToolGuardRequest request) {
        
        ToolGuardResponse tool_guard = tool_guardRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tool_guard));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_GUARD, action = I18Consts.I18N_ACTION_DELETE, description = "delete tool_guard")
    @Operation(summary = "Delete ToolGuard", description = "Delete a tool_guard")
    @Override
    @PreAuthorize(ToolGuardPermissions.HAS_TOOL_GUARD_DELETE)
    public ResponseEntity<?> delete(ToolGuardRequest request) {
        
        tool_guardRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_GUARD, action = I18Consts.I18N_ACTION_EXPORT, description = "export tool_guard")
    @Operation(summary = "Export ToolGuards", description = "Export tool_guards to Excel format")
    @Override
    @PreAuthorize(ToolGuardPermissions.HAS_TOOL_GUARD_EXPORT)
    @GetMapping("/export")
    public Object export(ToolGuardRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tool_guardRestService,
            ToolGuardExcel.class,
            "ToolGuard",
            "tool_guard"
        );
    }

    
    
}