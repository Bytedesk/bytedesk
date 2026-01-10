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
 * 演示流程任务超时监听器
 * 用于处理任务超时事件（如请假审批超时）
 */
@Slf4j
@Component("demoTaskTimeoutListener")
public class DemoTaskTimeoutListener implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String taskId = delegateTask.getId();
        String taskName = delegateTask.getName();
        String assignee = delegateTask.getAssignee();
        String processInstanceId = delegateTask.getProcessInstanceId();

        log.info("=== Demo Task Timeout Listener ===");
        log.info("Event: {}", eventName);
        log.info("Task ID: {}", taskId);
        log.info("Task Name: {}", taskName);
        log.info("Assignee: {}", assignee);
        log.info("Process Instance ID: {}", processInstanceId);

        // 任务创建时设置超时提醒
        if (EVENTNAME_CREATE.equals(eventName)) {
            log.info("Setting up timeout monitoring for task: {}", taskName);
            // 可以在这里设置超时监控逻辑
            // 例如：记录超时时间、设置提醒等
        }
    }
}
