/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 14:08:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 11:01:00
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
import com.bytedesk.ticket.consts.TicketConsts;

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
            log.error("工单线程创建事件, 线程对象为空: {}", event);
            return;
        }
        log.info("开始创建工单流程实例: threadUid={}, orgUid={}", thread.getUid(), thread.getOrgUid());
        // 1. 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        // 基本变量
        variables.put(TicketConsts.TICKET_VARIABLE_THREAD_UID, thread.getUid());
        // variables.put(TicketConsts.TICKET_VARIABLE_DEPARTMENT_UID, thread.getDepartmentUid());
        // variables.put(TicketConsts.TICKET_VARIABLE_REPORTER_UID, thread.getReporter().getUid());
        variables.put(TicketConsts.TICKET_VARIABLE_ORGUID, thread.getOrgUid());
        //
        // variables.put(TicketConsts.TICKET_VARIABLE_DESCRIPTION, thread.getDescription());
        // variables.put(TicketConsts.TICKET_VARIABLE_START_USER_ID, thread.getReporter().getUid());
        // variables.put(TicketConsts.TICKET_VARIABLE_STATUS, thread.getStatus());
        // variables.put(TicketConsts.TICKET_VARIABLE_PRIORITY, thread.getPriority());
        // variables.put(TicketConsts.TICKET_VARIABLE_CATEGORY_UID, thread.getCategoryUid());
        
        // 2. 启动流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(TicketConsts.THREAD_PROCESS_KEY)
                .tenantId(thread.getOrgUid())
                .name(thread.getUid()) // 流程实例名称
                .businessKey(thread.getUid())
                .variables(variables)
                .start();
        log.info("流程实例创建成功: processInstanceId={}, businessKey={}",
                processInstance.getId(), processInstance.getBusinessKey());

        // 3. 创建任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                // .taskAssignee(thread.getReporter().getUid())
                .singleResult();
        if (task != null) {
            // 完成工单创建任务
            taskService.complete(task.getId());
        } else {
            log.error("工单创建任务创建失败: task={}", task);
        }

        // 4. 设置流程实例变量
        // 可以在流程执行的任何时候调用, 每次调用都会产生一次变量更新历史记录
        // 适合设置运行时的动态变量或需要更新的变量
        // 每次调用都会有一次数据库操作
        runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_START_TIME, new Date());

        // 5. 设置 SLA 时间
        // String slaTime = switch (thread.getPriority()) {
        //     case "CRITICAL" -> "PT30M";
        //     case "URGENT" -> "PT1H";
        //     case "HIGH" -> "PT2H";
        //     case "MEDIUM" -> "PT4H";
        //     case "LOW" -> "PT8H";
        //     default -> "P1D";
        // };
        // runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_SLA_TIME, slaTime);
        // 第一步的assignee设置为reporter
        // runtimeService.setVariable(processInstance.getId(), TicketConsts.TICKET_VARIABLE_ASSIGNEE,
        //         thread.getReporter());
        
        // 6. 更新工单的流程实例ID
        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(thread.getUid());
        if (threadOptional.isPresent()) {
            ThreadEntity threadEntity = threadOptional.get();
            threadEntity.setProcessInstanceId(processInstance.getId());
            threadRestService.save(threadEntity);
        }
    }

}
