/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-29 12:24:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-29 12:29:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;

import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketAssignmentService {

    private final TaskService taskService;
    private final AgentRestService agentService;
    private final WorkgroupRestService workgroupService;

    /**
     * 为任务添加候选用户
     */
    public void addCandidateUser(String taskId, String userId) {
        Optional<AgentEntity> agentOptional = agentService.findByUid(userId);
        if (agentOptional.isPresent()) {
            taskService.addCandidateUser(taskId, agentOptional.get().getUid());
            log.info("Added candidate user {} to task {}", agentOptional.get().getUid(), taskId);
        }
    }

    /**
     * 为任务添加候选工作组
     */
    public void addCandidateGroup(String taskId, String workgroupId) {
        Optional<WorkgroupEntity> workgroupOptional = workgroupService.findByUid(workgroupId);
        if (workgroupOptional.isPresent()) {
            taskService.addCandidateGroup(taskId, workgroupOptional.get().getUid());
            log.info("Added candidate group {} to task {}", workgroupOptional.get().getId(), taskId);
        }
    }

    /**
     * 获取用户所在的所有工作组ID
     */
    public List<String> getUserWorkgroups(String userId) {
        Optional<AgentEntity> agentOptional = agentService.findByUid(userId);
        if (agentOptional.isPresent()) {
            // return workgroupService.findByAgentUid(agentOptional.get().getUid());
        }
        return Collections.emptyList();
    }

    /**
     * 获取工作组中的所有用户ID
     */
    public List<String> getWorkgroupUsers(String workgroupId) {
        Optional<WorkgroupEntity> workgroupOptional = workgroupService.findByUid(workgroupId);
        if (workgroupOptional.isPresent()) {
            // return agentService.getAgentUidsByWorkgroup(workgroupOptional.get());
        }
        return Collections.emptyList();
    }

    /**
     * 检查用户是否是主管
     */
    public boolean isSupervisor(String userId) {
        Optional<AgentEntity> agentOptional = agentService.findByUid(userId);
        if (agentOptional.isPresent()) {
            // return agentOptional.get().isSupervisor();
        }
        return false;
    }
} 