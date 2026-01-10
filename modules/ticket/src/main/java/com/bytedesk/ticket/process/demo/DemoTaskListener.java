/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-07 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-07 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.process.demo;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 演示流程任务监听器
 * 用于演示流程的任务事件处理
 */
@Slf4j
@Component("demoTaskListener")
public class DemoTaskListener implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String taskId = delegateTask.getId();
        String taskName = delegateTask.getName();
        String assignee = delegateTask.getAssignee();
        String processInstanceId = delegateTask.getProcessInstanceId();

        log.info("=== Demo Task Listener ===");
        log.info("Event: {}", eventName);
        log.info("Task ID: {}", taskId);
        log.info("Task Name: {}", taskName);
        log.info("Assignee: {}", assignee);
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Variables: {}", delegateTask.getVariables());

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
        log.info("Demo task created: name={}, assignee={}",
                delegateTask.getName(),
                delegateTask.getAssignee());
        // 任务创建时的业务逻辑
        // 例如：发送任务创建通知、初始化任务变量等
    }

    private void handleTaskAssignment(DelegateTask delegateTask) {
        log.info("Demo task assigned: name={}, assignee={}",
                delegateTask.getName(),
                delegateTask.getAssignee());
        // 任务分配时的业务逻辑
        // 例如：发送任务分配通知给处理人
    }

    private void handleTaskComplete(DelegateTask delegateTask) {
        log.info("Demo task completed: name={}, assignee={}",
                delegateTask.getName(),
                delegateTask.getAssignee());
        // 任务完成时的业务逻辑
        // 例如：记录任务完成时间、更新流程状态等
    }

    private void handleTaskDelete(DelegateTask delegateTask) {
        log.info("Demo task deleted: name={}", delegateTask.getName());
        // 任务删除时的业务逻辑
    }
}
