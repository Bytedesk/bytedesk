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
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 演示流程 - 超时通知委托
 * 用于请假审批流程中的超时提醒
 */
@Slf4j
@Component("demoTimeoutNotifyDelegate")
public class DemoTimeoutNotifyDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String taskName = execution.getCurrentActivityName();

        log.info("=== Demo Timeout Notify Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Task Name: {}", taskName);
        log.info("Sending timeout notification...");

        // 获取流程变量
        String applicantUid = (String) execution.getVariable("applicantUid");
        log.info("Applicant UID: {}", applicantUid);

        // 实际项目中，这里应该：
        // 1. 发送超时通知给审批人
        // 2. 记录超时事件
        // 3. 可能需要升级处理

        log.info("Timeout notification sent for task: {}", taskName);
    }
}
