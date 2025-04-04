/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 14:08:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 14:03:03
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
import com.bytedesk.core.thread.event.ThreadCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThreadProcessEventListener {

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final ThreadRestService threadRestService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        log.info("ticket - onThreadCreateEvent: {}", event);
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
        if (thread.getUserProtobuf() != null) {
            variables.put(ThreadConsts.THREAD_VARIABLE_USER_UID, thread.getUserProtobuf().getUid());
        }
        
        // 根据不同的会话类型设置相应的流程变量
        if (thread.isAgentType()) {
            // 一对一客服接待，直接指定assignee为特定客服
            if (thread.getAgentProtobuf() != null) {
                variables.put(ThreadConsts.THREAD_VARIABLE_ASSIGNEE, thread.getAgentProtobuf().getUid());
                log.info("一对一客服接待，设置assignee为: {}", thread.getAgentProtobuf().getUid());
            } else {
                log.error("一对一客服接待，但客服信息为空");
            }
            // 设置为非机器人接待
            variables.put("robotEnabled", false);
        } else if (thread.isWorkgroupType()) {
            // 技能组接待，设置candidateGroups为技能组ID
            if (thread.getWorkgroupProtobuf() != null) {
                variables.put(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID, thread.getWorkgroupProtobuf().getUid());
                log.info("技能组接待，设置candidateGroups为: {}", thread.getWorkgroupProtobuf().getUid());
            } else {
                log.error("技能组接待，但技能组信息为空");
            }
            // 设置为非机器人接待
            variables.put("robotEnabled", false);
        } else if (thread.isRobotType()) {
            // 机器人接待
            variables.put("robotEnabled", true);
            if (thread.getAgentProtobuf() != null) {
                variables.put(ThreadConsts.THREAD_VARIABLE_AGENT_UID, thread.getAgentProtobuf().getUid());
                log.info("机器人接待，设置robotUid为: {}", thread.getAgentProtobuf().getUid());
            }
            // 设置机器人超时时间（默认5分钟）
            variables.put("robotIdleTimeout", "PT5M");
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

        // 3. 创建任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        if (task != null) {
            // 完成会话创建任务
            taskService.complete(task.getId());
        } else {
            log.error("会话创建任务创建失败: task={}", task);
        }

        // 4. 设置流程实例变量
        // 可以在流程执行的任何时候调用, 每次调用都会产生一次变量更新历史记录
        // 适合设置运行时的动态变量或需要更新的变量
        // 每次调用都会有一次数据库操作
        runtimeService.setVariable(processInstance.getId(), ThreadConsts.THREAD_VARIABLE_START_TIME, new Date());

        // 5. 设置 SLA 时间
        String slaTime = "PT30M"; // 默认30分钟，后期支持自定义
        runtimeService.setVariable(processInstance.getId(), ThreadConsts.THREAD_VARIABLE_SLA_TIME, slaTime);
        
        // 6. 设置人工客服空闲超时时间（默认15分钟）
        runtimeService.setVariable(processInstance.getId(), "humanIdleTimeout", "PT15M");
        
        // 7. 更新会话的流程实例ID
        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(thread.getUid());
        if (threadOptional.isPresent()) {
            ThreadEntity threadEntity = threadOptional.get();
            threadEntity.setProcessInstanceId(processInstance.getId());
            threadRestService.save(threadEntity);
        }
    }

}
