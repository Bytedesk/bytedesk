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

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/agent/status")
@AllArgsConstructor
@Tag(name = "客服状态管理", description = "客服状态管理相关接口")
public class AgentStatusRestController extends BaseRestController<AgentStatusRequest, AgentStatusRestService> {

    private final AgentStatusRestService agentStatusService;

    @Override
    @Operation(summary = "根据组织查询客服状态")
    @GetMapping("/query/org")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_READ)
    public ResponseEntity<?> queryByOrg(AgentStatusRequest request) {
        
        Page<AgentStatusResponse> page = agentStatusService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    @Operation(summary = "根据用户查询客服状态")
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_READ)
    public ResponseEntity<?> queryByUser(AgentStatusRequest request) {
        
        Page<AgentStatusResponse> page = agentStatusService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    @Operation(summary = "根据UID查询客服状态")
    @GetMapping("/query/uid")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_READ)
    public ResponseEntity<?> queryByUid(AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @Operation(summary = "创建客服状态")
    @PostMapping("/create")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_CREATE)
    public ResponseEntity<?> create(@RequestBody AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @Operation(summary = "更新客服状态")
    @PostMapping("/update")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_UPDATE)
    public ResponseEntity<?> update(@RequestBody AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @Operation(summary = "删除客服状态")
    @PostMapping("/delete")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_DELETE)
    public ResponseEntity<?> delete(@RequestBody AgentStatusRequest request) {
        
        agentStatusService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    @GetMapping("/export")
    @PreAuthorize(AgentStatusPermissions.HAS_AGENT_STATUS_EXPORT)
    public Object export(AgentStatusRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
    
}
