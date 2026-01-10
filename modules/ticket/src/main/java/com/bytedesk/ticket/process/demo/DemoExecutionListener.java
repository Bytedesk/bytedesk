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

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 演示流程执行监听器
 * 用于演示流程的执行事件处理
 */
@Slf4j
@Component("demoExecutionListener")
public class DemoExecutionListener implements ExecutionListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();
        String processDefinitionId = execution.getProcessDefinitionId();
        String currentActivityId = execution.getCurrentActivityId();
        String currentActivityName = execution.getCurrentActivityName();

        log.info("=== Demo Execution Listener ===");
        log.info("Event: {}", eventName);
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Process Definition ID: {}", processDefinitionId);
        log.info("Current Activity ID: {}", currentActivityId);
        log.info("Current Activity Name: {}", currentActivityName);
        log.info("Variables: {}", execution.getVariables());

        switch (eventName) {
            case EVENTNAME_START:
                handleProcessStart(execution);
                break;
            case EVENTNAME_END:
                handleProcessEnd(execution);
                break;
            case EVENTNAME_TAKE:
                handleProcessTake(execution);
                break;
            default:
                log.warn("Unhandled execution event: {}", eventName);
                break;
        }
    }

    private void handleProcessStart(DelegateExecution execution) {
        log.info("Demo process started: {}", execution.getProcessInstanceId());
        // 可以在这里添加流程开始时的业务逻辑
        // 例如：初始化流程变量、发送通知等
    }

    private void handleProcessEnd(DelegateExecution execution) {
        log.info("Demo process ended: {}", execution.getProcessInstanceId());
        // 可以在这里添加流程结束时的业务逻辑
        // 例如：记录流程完成时间、发送完成通知等
    }

    private void handleProcessTake(DelegateExecution execution) {
        log.info("Demo process transition: activityId={}, activityName={}",
                execution.getCurrentActivityId(),
                execution.getCurrentActivityName());
        // 可以在这里添加流程流转时的业务逻辑
    }
}
