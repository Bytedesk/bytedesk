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
package com.bytedesk.ai.tool_call;

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
 * ToolCall Management Controller - API endpoints for managing tool calls
 */
@RestController
@RequestMapping("/api/v1/tool/call")
@AllArgsConstructor
@Tag(name = "ToolCall Management", description = "ToolCall management APIs for organizing and categorizing content with tool_calls")
@Description("ToolCall Management Controller - Content tool_callging and categorization APIs")
public class ToolCallRestController extends BaseRestController<ToolCallRequest, ToolCallRestService> {

    private final ToolCallRestService tool_callRestService;

    @ActionAnnotation(title = I18Consts.I18N_TOOL_CALL, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tool_call by org")
    @Operation(summary = "Query ToolCalls by Organization", description = "Retrieve tool_calls for the current organization")
    @PreAuthorize(ToolCallPermissions.HAS_TOOL_CALL_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ToolCallRequest request) {
        
        Page<ToolCallResponse> tool_calls = tool_callRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tool_calls));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_CALL, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tool_call by user")
    @Operation(summary = "Query ToolCalls by User", description = "Retrieve tool_calls for the current user")
    @PreAuthorize(ToolCallPermissions.HAS_TOOL_CALL_READ)
    @Override
    public ResponseEntity<?> queryByUser(ToolCallRequest request) {
        
        Page<ToolCallResponse> tool_calls = tool_callRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tool_calls));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_CALL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tool_call by uid")
    @Operation(summary = "Query ToolCall by UID", description = "Retrieve a specific tool_call by its unique identifier")
    @PreAuthorize(ToolCallPermissions.HAS_TOOL_CALL_READ)
    @Override
    public ResponseEntity<?> queryByUid(ToolCallRequest request) {
        
        ToolCallResponse tool_call = tool_callRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tool_call));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_CALL, action = I18Consts.I18N_ACTION_CREATE, description = "create tool_call")
    @Operation(summary = "Create ToolCall", description = "Create a new tool_call")
    @Override
    @PreAuthorize(ToolCallPermissions.HAS_TOOL_CALL_CREATE)
    public ResponseEntity<?> create(ToolCallRequest request) {
        
        ToolCallResponse tool_call = tool_callRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tool_call));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_CALL, action = I18Consts.I18N_ACTION_UPDATE, description = "update tool_call")
    @Operation(summary = "Update ToolCall", description = "Update an existing tool_call")
    @Override
    @PreAuthorize(ToolCallPermissions.HAS_TOOL_CALL_UPDATE)
    public ResponseEntity<?> update(ToolCallRequest request) {
        
        ToolCallResponse tool_call = tool_callRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tool_call));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_CALL, action = I18Consts.I18N_ACTION_DELETE, description = "delete tool_call")
    @Operation(summary = "Delete ToolCall", description = "Delete a tool_call")
    @Override
    @PreAuthorize(ToolCallPermissions.HAS_TOOL_CALL_DELETE)
    public ResponseEntity<?> delete(ToolCallRequest request) {
        
        tool_callRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_CALL, action = I18Consts.I18N_ACTION_EXPORT, description = "export tool_call")
    @Operation(summary = "Export ToolCalls", description = "Export tool_calls to Excel format")
    @Override
    @PreAuthorize(ToolCallPermissions.HAS_TOOL_CALL_EXPORT)
    @GetMapping("/export")
    public Object export(ToolCallRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tool_callRestService,
            ToolCallExcel.class,
            "ToolCall",
            "tool_call"
        );
    }

    
    
}