/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 14:08:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 23:31:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jackning 270580156@qq.com
 * 
 * 会话流程创建事件监听器
 * 
 * 1. 当会话创建时，创建会话流程实例
 * 2. 为流程实例设置必要的变量
 * 3. 启动流程实例
 * 4. 创建任务
 * 5. 设置流程实例变量
 * 6. 设置 SLA 时间
 * 7. 设置人工客服空闲超时时间
 * 8. 设置机器人超时时间
 * 9. 设置任务变量
 * 
 * 注意：
 * 1. 这里仅处理了基本的会话类型，如一对一客服接待、技能组接待、机器人接待等
 * 
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThreadProcessEventListener {

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final ThreadRestService threadRestService;

    @EventListener
    public void onThreadProcessCreateEvent(ThreadProcessCreateEvent event) {
        log.info("ticket - ThreadProcessCreateEvent: {}", event);
        ThreadEntity thread = event.getThread();
        if (thread == null) {
            log.error("会话线程创建事件, 线程对象为空: {}", event);
            return;
        }
        log.info("开始创建会话流程实例: threadUid={}, orgUid={}", thread.getUid(), thread.getOrgUid());
        
        // 1. 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        // 基本变量
        variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_UID, thread.getUid());
        variables.put(ThreadConsts.THREAD_VARIABLE_ORGUID, thread.getOrgUid());
        variables.put(ThreadConsts.THREAD_VARIABLE_STATUS, thread.getStatus());
        variables.put(ThreadConsts.THREAD_VARIABLE_START_TIME, new Date());
        
        // 设置默认值，避免流程执行时找不到变量
        // 设置 SLA 时间 - 预先设置默认值
        variables.put(ThreadConsts.THREAD_VARIABLE_SLA_TIME, ThreadConsts.DEFAULT_SLA_TIME);  // 默认30分钟
        // 设置人工客服空闲超时时间
        variables.put(ThreadConsts.THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT, ThreadConsts.DEFAULT_HUMAN_IDLE_TIMEOUT);  // 默认15分钟
        // 设置机器人空闲超时时间
        variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT, ThreadConsts.DEFAULT_ROBOT_IDLE_TIMEOUT);   // 默认5分钟
        
        // 添加控制流程流转的变量
        variables.put(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, false);  // 默认不需要转人工
        variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_STATUS, ThreadConsts.THREAD_STATUS_CREATED);   // 初始状态
        
        // 显式初始化机器人相关变量，避免空指针和循环问题
        variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_UNANSWERED_COUNT, 0);
        variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_EXECUTION_COUNT, 0);
        // 初始化访客请求转人工的标志
        variables.put(ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, false);
        
        if (thread.getUserProtobuf() != null) {
            variables.put(ThreadConsts.THREAD_VARIABLE_USER_UID, thread.getUserProtobuf().getUid());
        }
        // 设置客服是否繁忙变量
        variables.put(ThreadConsts.THREAD_VARIABLE_AGENTS_BUSY, thread.isQueuing());
        variables.put(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID, "");
        // 设置为非机器人接待
        variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_ENABLED, thread.isAgentRobot());
        // 设置客服是否离线
        variables.put(ThreadConsts.THREAD_VARIABLE_AGENTS_OFFLINE, thread.isOffline());
        
        // 设置会话类型，用于在delegate中判断是否支持转人工等操作
        if (thread.isAgentType()) {
            variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, ThreadConsts.THREAD_TYPE_AGENT);
        } else if (thread.isWorkgroupType()) {
            variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, ThreadConsts.THREAD_TYPE_WORKGROUP);
        } else if (thread.isRobotType()) {
            variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, ThreadConsts.THREAD_TYPE_ROBOT);
        } else {
            variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, "unknown");
        }
        
        // 根据不同的会话类型设置相应的流程变量
        if (thread.isAgentType()) {
            // 一对一客服接待，直接指定assignee为特定客服
            if (thread.getAgentProtobuf() != null) {
                variables.put(ThreadConsts.THREAD_VARIABLE_ASSIGNEE, thread.getAgentProtobuf().getUid());
                variables.put(ThreadConsts.THREAD_VARIABLE_AGENT_UID, thread.getAgentProtobuf().getUid());
                log.info("一对一客服接待，设置assignee为: {}", thread.getAgentProtobuf().getUid());
            } else {
                log.error("一对一客服接待，但客服信息为空");
            }
        } else if (thread.isWorkgroupType()) {
            // 技能组接待，设置candidateGroups为技能组ID
            if (thread.getWorkgroupProtobuf() != null) {
                variables.put(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID, thread.getWorkgroupProtobuf().getUid());
                log.info("技能组接待，设置candidateGroups为: {}", thread.getWorkgroupProtobuf().getUid());
            } else {
                log.error("技能组接待，但技能组信息为空");
            }
        } else if (thread.isRobotType()) {
            // 机器人接待
            variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_ENABLED, true);  // 确保这里设置为true
            if (thread.getAgentProtobuf() != null) {
                variables.put(ThreadConsts.THREAD_VARIABLE_AGENT_UID, thread.getAgentProtobuf().getUid());
                log.info("机器人接待，设置robotUid为: {}", thread.getAgentProtobuf().getUid());
            }
        } else {
            // 暂时仅处理上述客服会话类型
            log.warn("未知的会话类型，不处理");
            return;
        }
        
        // 2. 启动流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(ThreadConsts.THREAD_PROCESS_KEY)
                .tenantId(thread.getOrgUid())
                .name(thread.getUid()) // 流程实例名称
                .businessKey(thread.getUid())
                .variables(variables)
                .start();
        log.info("会话流程实例创建成功: processInstanceId={}, businessKey={}",
                processInstance.getId(), processInstance.getBusinessKey());

        // 在后续操作前先检查流程实例是否还存在
        boolean isProcessInstanceActive = checkProcessInstanceActive(processInstance.getId());
        
        // 3. 创建任务 - 只有在流程实例仍然活跃时才创建任务
        if (isProcessInstanceActive && thread.isAgentType() && thread.getAgentProtobuf() != null) {
            try {
                Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getId())
                    .taskAssignee(thread.getAgentProtobuf().getUid()) // 指定任务负责人
                    .singleResult();
                if (task != null) {
                    // 完成会话创建任务
                    taskService.complete(task.getId());
                } else {
                    log.warn("会话创建任务未找到，可能是因为流程已自动执行到其他节点: threadUid={}", thread.getUid());
                }
            } catch (Exception e) {
                log.error("创建或完成任务时出错: {}", e.getMessage());
            }
        }

        // 7. 更新会话的流程实例ID - 无论流程是否活跃都需要更新
        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(thread.getUid());
        if (threadOptional.isPresent()) {
            ThreadEntity threadEntity = threadOptional.get();
            String processUid = Utils.formatUid(threadEntity.getOrgUid(), ThreadConsts.THREAD_PROCESS_KEY);
            threadEntity.setProcessEntityUid(processUid);
            threadEntity.setProcessInstanceId(processInstance.getId());
            threadRestService.save(threadEntity);
        }
    }
    
    /**
     * 检查流程实例是否仍然活跃
     * 
     * @param processInstanceId 流程实例ID
     * @return 如果流程实例仍然活跃，则返回true；否则返回false
     */
    private boolean checkProcessInstanceActive(String processInstanceId) {
        try {
            // 尝试查询流程实例
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            return instance != null;
        } catch (Exception e) {
            log.warn("检查流程实例状态时出错: {}", e.getMessage());
            return false;
        }
    }

}
