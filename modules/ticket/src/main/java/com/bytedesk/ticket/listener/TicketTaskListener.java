/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-16 08:41:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 09:20:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * TaskListener (任务监听器):
 * 监听任务相关事件
 * 事件类型：create、assignment、complete、delete
 * 只能绑定到用户任务上
 * 
 * 关注任务的生命周期
 * 可以访问和修改任务相关信息
 * 适合处理任务级别的业务逻辑
 * 
 * 用途：监听用户任务的生命周期事件
 * 绑定位置：只能绑定到用户任务
 * 主要场景：任务级别的监控和处理
 * 特点：可以访问和修改任务相关信息
 */
@Slf4j
@Component("ticketTaskListener")
public class TicketTaskListener implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        String taskId = delegateTask.getId();
        String taskName = delegateTask.getName();
        String assignee = delegateTask.getAssignee();

        log.info("Task event: {}, taskId: {}, taskName: {}, assignee: {}",
                eventName, taskId, taskName, assignee);

        switch (eventName) {
            case EVENTNAME_CREATE:
                // 任务创建时触发
                handleTaskCreate(delegateTask);
                break;
            case EVENTNAME_ASSIGNMENT:
                // 任务分配时触发
                handleTaskAssignment(delegateTask);
                break;
            case EVENTNAME_COMPLETE:
                // 任务完成时触发
                handleTaskComplete(delegateTask);
                break;
            case EVENTNAME_DELETE:
                // 任务删除时触发
                handleTaskDelete(delegateTask);
                break;
            default:
                log.warn("Unhandled task event: {}", eventName);
                break;
        }
    }

    private void handleTaskCreate(DelegateTask task) {
        log.info("Task created: {}, name: {}", task.getId(), task.getName());
    }

    private void handleTaskAssignment(DelegateTask task) {
        log.info("Task assigned: {}, assignee: {}", task.getId(), task.getAssignee());
    }

    private void handleTaskComplete(DelegateTask task) {
        log.info("Task completed: {}, assignee: {}", task.getId(), task.getAssignee());
    }

    private void handleTaskDelete(DelegateTask task) {
        log.info("Task deleted: {}", task.getId());
    }
}