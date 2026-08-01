/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 16:07:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 11:04:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/agent/status")
@AllArgsConstructor
@Tag(name = "Agent Status Management", description = "Agent status management APIs")
public class AgentStatusRestController extends BaseRestController<AgentStatusRequest, AgentStatusRestService> {

    private final AgentStatusRestService agentStatusService;

    @Override
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query agent status by org")
    @Operation(summary = "Query Agent Status by Organization")
    @GetMapping("/query/org")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_READ)
    public ResponseEntity<?> queryByOrg(AgentStatusRequest request) {
        
        Page<AgentStatusResponse> page = agentStatusService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query agent status by user")
    @Operation(summary = "Query Agent Status by User")
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_READ)
    public ResponseEntity<?> queryByUser(AgentStatusRequest request) {
        
        Page<AgentStatusResponse> page = agentStatusService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query agent status by uid")
    @Operation(summary = "Query Agent Status by UID")
    @GetMapping("/query/uid")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_READ)
    public ResponseEntity<?> queryByUid(AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS, action = I18Consts.I18N_ACTION_CREATE, description = "create agent status")
    @Operation(summary = "Create Agent Status")
    @PostMapping("/create")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_CREATE)
    public ResponseEntity<?> create(@RequestBody AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS, action = I18Consts.I18N_ACTION_UPDATE, description = "update agent status")
    @Operation(summary = "Update Agent Status")
    @PostMapping("/update")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_UPDATE)
    public ResponseEntity<?> update(@RequestBody AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS, action = I18Consts.I18N_ACTION_DELETE, description = "delete agent status")
    @Operation(summary = "Delete Agent Status")
    @PostMapping("/delete")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_DELETE)
    public ResponseEntity<?> delete(@RequestBody AgentStatusRequest request) {
        
        agentStatusService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS, action = I18Consts.I18N_ACTION_EXPORT, description = "export agent status")
    @GetMapping("/export")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_EXPORT)
    public Object export(AgentStatusRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            agentStatusService,
            AgentStatusExcel.class,
            "客服状态",
            "agent_status"
        );
    }

    
    
}
