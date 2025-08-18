/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-18 15:39:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_template;

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
@RequestMapping("/api/v1/agentTemplate")
@AllArgsConstructor
@Tag(name = "AgentTemplate Management", description = "AgentTemplate management APIs for organizing and categorizing content with agentTemplates")
@Description("AgentTemplate Management Controller - Content agent template and categorization APIs")
public class AgentTemplateRestController extends BaseRestController<AgentTemplateRequest> {

    private final AgentTemplateRestService agentTemplateRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query agentTemplate by org")
    @Operation(summary = "Query AgentTemplates by Organization", description = "Retrieve agentTemplates for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(AgentTemplateRequest request) {
        
        Page<AgentTemplateResponse> agentTemplates = agentTemplateRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(agentTemplates));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query agentTemplate by user")
    @Operation(summary = "Query AgentTemplates by User", description = "Retrieve agentTemplates for the current user")
    @Override
    public ResponseEntity<?> queryByUser(AgentTemplateRequest request) {
        
        Page<AgentTemplateResponse> agentTemplates = agentTemplateRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(agentTemplates));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query agentTemplate by uid")
    @Operation(summary = "Query AgentTemplate by UID", description = "Retrieve a specific agentTemplate by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(AgentTemplateRequest request) {
        
        AgentTemplateResponse agentTemplate = agentTemplateRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(agentTemplate));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create agentTemplate")
    @Operation(summary = "Create AgentTemplate", description = "Create a new agentTemplate")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(AgentTemplateRequest request) {
        
        AgentTemplateResponse agentTemplate = agentTemplateRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(agentTemplate));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update agentTemplate")
    @Operation(summary = "Update AgentTemplate", description = "Update an existing agentTemplate")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(AgentTemplateRequest request) {
        
        AgentTemplateResponse agentTemplate = agentTemplateRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(agentTemplate));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete agentTemplate")
    @Operation(summary = "Delete AgentTemplate", description = "Delete a agentTemplate")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(AgentTemplateRequest request) {
        
        agentTemplateRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export agentTemplate")
    @Operation(summary = "Export AgentTemplates", description = "Export agentTemplates to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(AgentTemplateRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            agentTemplateRestService,
            AgentTemplateExcel.class,
            "标签",
            "agentTemplate"
        );
    }

    
    
}