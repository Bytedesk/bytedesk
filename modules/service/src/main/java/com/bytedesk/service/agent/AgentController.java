/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-29 20:21:31
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent")
public class AgentController {

    private final AgentService agentService;

    /**
     * query by org
     * 
     * @param agentRequest
     * @return
     */
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(AgentRequest agentRequest) {

        Page<AgentResponse> page = agentService.queryByOrg(agentRequest);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * query by user self
     * 
     * @param agentRequest
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<?> query(AgentRequest agentRequest) {

        AgentResponse agentResponse = agentService.query(agentRequest);
        
        return ResponseEntity.ok(JsonResult.success(agentResponse));
    }

    /**
     * create
     *
     * @param agentRequest agent
     * @return json
     */
    @ActionAnnotation(title = "agent", action = "create", description = "create agent")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AgentRequest agentRequest) {

        AgentResponse agent = agentService.create(agentRequest);
        if (agent == null) {
            return ResponseEntity.ok(JsonResult.error("create agent failed"));
        }

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    /**
     * update
     *
     * @param agentRequest agent
     * @return json
     */
    @ActionAnnotation(title = "agent", action = "update", description = "update agent")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AgentRequest agentRequest) {

        AgentResponse agent = agentService.update(agentRequest);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    /**
     * delete
     *
     * @param agentRequest agent
     * @return json
     */
    @ActionAnnotation(title = "agent", action = "delete", description = "delete agent")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody AgentRequest agentRequest) {

        agentService.deleteByUid(agentRequest.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(agentRequest));
    }

}
