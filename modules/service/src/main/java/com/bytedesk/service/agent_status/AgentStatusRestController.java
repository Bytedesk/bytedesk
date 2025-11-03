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
    public ResponseEntity<?> queryByOrg(AgentStatusRequest request) {
        
        Page<AgentStatusResponse> page = agentStatusService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    @Operation(summary = "根据用户查询客服状态")
    public ResponseEntity<?> queryByUser(AgentStatusRequest request) {
        
        Page<AgentStatusResponse> page = agentStatusService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    @Operation(summary = "根据UID查询客服状态")
    public ResponseEntity<?> queryByUid(AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @Operation(summary = "创建客服状态")
    public ResponseEntity<?> create(AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @Operation(summary = "更新客服状态")
    public ResponseEntity<?> update(AgentStatusRequest request) {
        
        AgentStatusResponse response = agentStatusService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    @Operation(summary = "删除客服状态")
    public ResponseEntity<?> delete(AgentStatusRequest request) {
        
        agentStatusService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(AgentStatusRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
    
}
