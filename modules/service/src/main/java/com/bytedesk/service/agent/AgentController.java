/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-08 09:16:00
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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent")
public class AgentController {

    private final AgentService agentService;

    /**
     * query
     * 
     * @param agentRequest
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<?> query(AgentRequest agentRequest) {

        return ResponseEntity.ok(JsonResult.success(agentService.query(agentRequest)));
    }

    /**
     * create
     *
     * @param agentRequest agent
     * @return json
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AgentRequest agentRequest) {

        Agent agent = agentService.create(agentRequest);
        if (agent == null) {
            return ResponseEntity.ok(JsonResult.error("department not exist"));
        }

        return ResponseEntity.ok(JsonResult.success(agentService.convertToAgentResponse(agent)));
    }

    /**
     * update
     *
     * @param agentRequest agent
     * @return json
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AgentRequest agentRequest) {

        //
        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * delete
     *
     * @param agentRequest agent
     * @return json
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody AgentRequest agentRequest) {

        //
        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * filter
     *
     * @return json
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filter(AgentRequest filterParam) {
        
        //
        return ResponseEntity.ok(JsonResult.success());
    }


}
