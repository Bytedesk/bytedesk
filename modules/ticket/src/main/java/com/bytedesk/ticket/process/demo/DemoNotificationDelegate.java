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
 * 演示流程 - 通知委托
 * 用于发送各种通知（打款通知、审批通知等）
 */
@Slf4j
@Component("demoNotificationDelegate")
public class DemoNotificationDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String taskName = execution.getCurrentActivityName();

        log.info("=== Demo Notification Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Task Name: {}", taskName);
        log.info("Sending notification...");

        // 获取流程变量
        String applicantUid = (String) execution.getVariable("applicantUid");

        // 实际项目中，这里应该：
        // 1. 根据通知类型发送不同通知
        // 2. 支持多种通知渠道（邮件、短信、站内信等）
        // 3. 记录通知发送状态

        log.info("Notification sent to: {}", applicantUid);
        log.info("Notification type: {}", taskName);
    }
}
