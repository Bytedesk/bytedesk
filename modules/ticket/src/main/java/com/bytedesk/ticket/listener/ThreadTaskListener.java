/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 08:32:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 08:32:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.listener;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 客服会话流程任务监听器
 * 
 * 监听客服会话任务的生命周期事件：
 * - 任务创建
 * - 任务分配
 * - 任务完成
 * - 任务删除
 */
@Slf4j
@Component("threadTaskListener")
public class ThreadTaskListener implements TaskListener {
    
    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String taskId = delegateTask.getId();
        String taskName = delegateTask.getName();
        String processInstanceId = delegateTask.getProcessInstanceId();
        
        log.info("Thread task event: {}, taskId: {}, taskName: {}, processInstanceId: {}", 
            eventName, taskId, taskName, processInstanceId);

        switch (eventName) {
            case EVENTNAME_CREATE:
                handleTaskCreate(delegateTask);
                break;
            case EVENTNAME_ASSIGNMENT:
                handleTaskAssignment(delegateTask);
                break;
            case EVENTNAME_COMPLETE:
                handleTaskComplete(delegateTask);
                break;
            case EVENTNAME_DELETE:
                handleTaskDelete(delegateTask);
                break;
            default:
                log.warn("Unhandled task event: {}", eventName);
                break;
        }
    }

    private void handleTaskCreate(DelegateTask delegateTask) {
        // 任务创建时的业务逻辑
        log.info("Thread task created: {}", delegateTask.getId());
        
        // 记录任务创建时间
        delegateTask.setVariable("taskCreateTime", System.currentTimeMillis());
    }

    private void handleTaskAssignment(DelegateTask delegateTask) {
        // 任务分配时的业务逻辑
        String assignee = delegateTask.getAssignee();
        log.info("Thread task assigned to: {}", assignee);
        
        // 记录任务分配时间和分配给的用户
        delegateTask.setVariable("taskAssignTime", System.currentTimeMillis());
        delegateTask.setVariable("taskAssignee", assignee);
    }

    private void handleTaskComplete(DelegateTask delegateTask) {
        // 任务完成时的业务逻辑
        log.info("Thread task completed: {}", delegateTask.getId());
        
        // 记录任务完成时间
        delegateTask.setVariable("taskCompleteTime", System.currentTimeMillis());
        
        // 计算任务处理时长
        Long createTime = (Long) delegateTask.getVariable("taskCreateTime");
        if (createTime != null) {
            Long duration = System.currentTimeMillis() - createTime;
            delegateTask.setVariable("taskDuration", duration);
            log.info("Task duration: {} ms", duration);
        }
    }

    private void handleTaskDelete(DelegateTask delegateTask) {
        // 任务删除时的业务逻辑
        log.info("Thread task deleted: {}", delegateTask.getId());
    }
} 