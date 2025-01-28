/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 12:50:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import org.flowable.task.service.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// 使用 TaskListener 处理任务相关事件
@Slf4j
@Component
public class TicketTaskListener implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String taskId = delegateTask.getId();
        String processInstanceId = delegateTask.getProcessInstanceId();

        log.info("Task event: {}, taskId: {}, processInstanceId: {}", 
            eventName, taskId, processInstanceId);

        switch (eventName) {
            case EVENTNAME_CREATE:
                // 任务创建时触发
                handleTaskCreated(delegateTask);
                break;
            case EVENTNAME_ASSIGNMENT:
                // 任务分配时触发
                handleTaskAssigned(delegateTask);
                break;
            case EVENTNAME_COMPLETE:
                // 任务完成时触发
                handleTaskCompleted(delegateTask);
                break;
            case EVENTNAME_DELETE:
                // 任务删除时触发
                handleTaskDeleted(delegateTask);
                break;
            default:
                log.warn("Unhandled task event: {}", eventName);
                break;
        }
    }

    private void handleTaskCreated(DelegateTask delegateTask) {
        // 处理任务创建事件
        log.info("Task created: {}", delegateTask.getId());
        // TODO: 可以在这里添加任务创建后的处理逻辑
        // 例如：发送通知、更新统计数据等
    }

    private void handleTaskAssigned(DelegateTask delegateTask) {
        // 处理任务分配事件
        String assignee = delegateTask.getAssignee();
        log.info("Task assigned to: {}", assignee);
        // TODO: 可以在这里添加任务分配后的处理逻辑
        // 例如：发送通知给被分配人
    }

    private void handleTaskCompleted(DelegateTask delegateTask) {
        // 处理任务完成事件
        log.info("Task completed: {}", delegateTask.getId());
        // TODO: 可以在这里添加任务完成后的处理逻辑
        // 例如：更新工单状态、发送完成通知等
    }

    private void handleTaskDeleted(DelegateTask delegateTask) {
        // 处理任务删除事件
        log.info("Task deleted: {}", delegateTask.getId());
        // TODO: 可以在这里添加任务删除后的处理逻辑
        // 例如：记录删除原因、更新相关统计等
    }
}
