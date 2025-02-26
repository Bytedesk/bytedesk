/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 10:09:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
// import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent")
public class AgentRestController extends BaseRestController<AgentRequest> {

    private final AgentRestService agentService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(AgentRequest request) {

        Page<AgentResponse> page = agentService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(AgentRequest request) {

        AgentResponse agentResponse = agentService.query(request);
        
        return ResponseEntity.ok(JsonResult.success(agentResponse));
    }

    @ActionAnnotation(title = "agent", action = "detail", description = "query agent profile")
    @GetMapping("/query/detail")
    public ResponseEntity<?> queryDetail(AgentRequest request) {

        AgentResponse agent = agentService.queryDetail(request.getUid());

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @ActionAnnotation(title = "agent", action = "syncCurrentThreadCount", description = "sync agent current thread count")
    @PostMapping("/sync/current/thread/count")
    public ResponseEntity<?> syncCurrentThreadCount(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.syncCurrentThreadCount(request);
        if (agent != null) {
            return ResponseEntity.ok(JsonResult.success(agent));
        }
        return ResponseEntity.ok(JsonResult.error("sync current thread count failed"));
    }

    @ActionAnnotation(title = "agent", action = "create", description = "create agent")
    @Override
    public ResponseEntity<?> create(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.create(request);
        if (agent == null) {
            return ResponseEntity.ok(JsonResult.error("create agent failed"));
        }

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @ActionAnnotation(title = "agent", action = "update", description = "update agent")
    @Override
    public ResponseEntity<?> update(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    // updateAvatar
    @ActionAnnotation(title = "agent", action = "updateAvatar", description = "update agent avatar")
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.updateAvatar(request);

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @ActionAnnotation(title = "agent", action = "updateStatus", description = "update agent status")
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.updateStatus(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @ActionAnnotation(title = "agent", action = "updateAutoReply", description = "update agent autoreply")
    @PostMapping("/update/autoreply")
    public ResponseEntity<?> updateAutoReply(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.updateAutoReply(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }
    
    @ActionAnnotation(title = "agent", action = "delete", description = "delete agent")
    @Override
    public ResponseEntity<?> delete(@RequestBody AgentRequest request) {

        agentService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

}
