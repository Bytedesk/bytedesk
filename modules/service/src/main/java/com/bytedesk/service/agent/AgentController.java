/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-04 13:17:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent")
public class AgentController extends BaseRestController<AgentRequest> {

    private final AgentService agentService;

    /**
     * query by org
     * 
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<?> queryByOrg(AgentRequest request) {

        Page<AgentResponse> page = agentService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * query by user self
     * 
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<?> queryByUser(AgentRequest request) {

        AgentResponse agentResponse = agentService.query(request);
        
        return ResponseEntity.ok(JsonResult.success(agentResponse));
    }

    /**
     * create
     *
     * @param request agent
     * @return json
     */
    @ActionAnnotation(title = "agent", action = "create", description = "create agent")
    @Override
    public ResponseEntity<?> create(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.create(request);
        if (agent == null) {
            return ResponseEntity.ok(JsonResult.error("create agent failed"));
        }

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    /**
     * update
     *
     * @param request agent
     * @return json
     */
    @ActionAnnotation(title = "agent", action = "update", description = "update agent")
    @Override
    public ResponseEntity<?> update(@RequestBody AgentRequest request) {

        AgentResponse agent = agentService.update(request);
        //
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
    
    /**
     * delete
     *
     * @param request agent
     * @return json
     */
    @ActionAnnotation(title = "agent", action = "delete", description = "delete agent")
    @Override
    public ResponseEntity<?> delete(@RequestBody AgentRequest request) {

        agentService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

}
