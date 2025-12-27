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
package com.bytedesk.service.agent_next;

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
@RequestMapping("/api/v1/agent_next")
@AllArgsConstructor
@Tag(name = "AgentNext Management", description = "AgentNext management APIs for organizing and categorizing content with agent_nexts")
@Description("AgentNext Management Controller - Content agent_nextging and categorization APIs")
public class AgentNextRestController extends BaseRestController<AgentNextRequest, AgentNextRestService> {

    private final AgentNextRestService agentNextRestService;

    @ActionAnnotation(title = "AgentNext", action = "组织查询", description = "query agent_next by org")
    @Operation(summary = "Query AgentNexts by Organization", description = "Retrieve agent_nexts for the current organization")
    @PreAuthorize(AgentNextPermissions.HAS_TAG_READ)
    @Override
    public ResponseEntity<?> queryByOrg(AgentNextRequest request) {
        
        Page<AgentNextResponse> agent_nexts = agentNextRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(agent_nexts));
    }

    @ActionAnnotation(title = "AgentNext", action = "用户查询", description = "query agent_next by user")
    @Operation(summary = "Query AgentNexts by User", description = "Retrieve agent_nexts for the current user")
    @PreAuthorize(AgentNextPermissions.HAS_TAG_READ)
    @Override
    public ResponseEntity<?> queryByUser(AgentNextRequest request) {
        
        Page<AgentNextResponse> agent_nexts = agentNextRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(agent_nexts));
    }

    @ActionAnnotation(title = "AgentNext", action = "查询详情", description = "query agent_next by uid")
    @Operation(summary = "Query AgentNext by UID", description = "Retrieve a specific agent_next by its unique identifier")
    @PreAuthorize(AgentNextPermissions.HAS_TAG_READ)
    @Override
    public ResponseEntity<?> queryByUid(AgentNextRequest request) {
        
        AgentNextResponse agent_next = agentNextRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(agent_next));
    }

    @ActionAnnotation(title = "AgentNext", action = "新建", description = "create agent_next")
    @Operation(summary = "Create AgentNext", description = "Create a new agent_next")
    @Override
    @PreAuthorize(AgentNextPermissions.HAS_TAG_CREATE)
    public ResponseEntity<?> create(AgentNextRequest request) {
        
        AgentNextResponse agent_next = agentNextRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(agent_next));
    }

    @ActionAnnotation(title = "AgentNext", action = "更新", description = "update agent_next")
    @Operation(summary = "Update AgentNext", description = "Update an existing agent_next")
    @Override
    @PreAuthorize(AgentNextPermissions.HAS_TAG_UPDATE)
    public ResponseEntity<?> update(AgentNextRequest request) {
        
        AgentNextResponse agent_next = agentNextRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(agent_next));
    }

    @ActionAnnotation(title = "AgentNext", action = "删除", description = "delete agent_next")
    @Operation(summary = "Delete AgentNext", description = "Delete a agent_next")
    @Override
    @PreAuthorize(AgentNextPermissions.HAS_TAG_DELETE)
    public ResponseEntity<?> delete(AgentNextRequest request) {
        
        agentNextRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "AgentNext", action = "导出", description = "export agent_next")
    @Operation(summary = "Export AgentNexts", description = "Export agent_nexts to Excel format")
    @Override
    @PreAuthorize(AgentNextPermissions.HAS_TAG_EXPORT)
    @GetMapping("/export")
    public Object export(AgentNextRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            agentNextRestService,
            AgentNextExcel.class,
            "AgentNext",
            "agent_next"
        );
    }

    
    
}