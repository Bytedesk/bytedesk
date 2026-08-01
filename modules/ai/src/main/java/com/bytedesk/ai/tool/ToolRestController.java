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
package com.bytedesk.ai.tool;

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

@RestController
@RequestMapping("/api/v1/tool")
@AllArgsConstructor
@Tag(name = "Tool Management", description = "Tool management APIs for organizing and categorizing content with tools")
@Description("Tool Management Controller - Content toolging and categorization APIs")
public class ToolRestController extends BaseRestController<ToolRequest, ToolRestService> {

    private final ToolRestService toolRestService;

    @ActionAnnotation(title = I18Consts.I18N_TOOL, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tool by org")
    @Operation(summary = "Query Tools by Organization", description = "Retrieve tools for the current organization")
    @PreAuthorize(ToolPermissions.HAS_TOOL_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ToolRequest request) {
        
        Page<ToolResponse> tools = toolRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tools));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tool by user")
    @Operation(summary = "Query Tools by User", description = "Retrieve tools for the current user")
    @PreAuthorize(ToolPermissions.HAS_TOOL_READ)
    @Override
    public ResponseEntity<?> queryByUser(ToolRequest request) {
        
        Page<ToolResponse> tools = toolRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tools));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tool by uid")
    @Operation(summary = "Query Tool by UID", description = "Retrieve a specific tool by its unique identifier")
    @PreAuthorize(ToolPermissions.HAS_TOOL_READ)
    @Override
    public ResponseEntity<?> queryByUid(ToolRequest request) {
        
        ToolResponse tool = toolRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tool));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL, action = I18Consts.I18N_ACTION_CREATE, description = "create tool")
    @Operation(summary = "Create Tool", description = "Create a new tool")
    @Override
    @PreAuthorize(ToolPermissions.HAS_TOOL_CREATE)
    public ResponseEntity<?> create(ToolRequest request) {
        
        ToolResponse tool = toolRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tool));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL, action = I18Consts.I18N_ACTION_UPDATE, description = "update tool")
    @Operation(summary = "Update Tool", description = "Update an existing tool")
    @Override
    @PreAuthorize(ToolPermissions.HAS_TOOL_UPDATE)
    public ResponseEntity<?> update(ToolRequest request) {
        
        ToolResponse tool = toolRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tool));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL, action = I18Consts.I18N_ACTION_DELETE, description = "delete tool")
    @Operation(summary = "Delete Tool", description = "Delete a tool")
    @Override
    @PreAuthorize(ToolPermissions.HAS_TOOL_DELETE)
    public ResponseEntity<?> delete(ToolRequest request) {
        
        toolRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL, action = I18Consts.I18N_ACTION_EXPORT, description = "export tool")
    @Operation(summary = "Export Tools", description = "Export tools to Excel format")
    @Override
    @PreAuthorize(ToolPermissions.HAS_TOOL_EXPORT)
    @GetMapping("/export")
    public Object export(ToolRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            toolRestService,
            ToolExcel.class,
            "Tool",
            "tool"
        );
    }

    
    
}