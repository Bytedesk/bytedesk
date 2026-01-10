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
 * 演示流程 - 升级通知委托
 * 用于IT支持流程中的高优先级工单升级通知
 */
@Slf4j
@Component("demoEscalationNotifyDelegate")
public class DemoEscalationNotifyDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();

        log.info("=== Demo Escalation Notify Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Processing escalation notification...");

        // 获取流程变量
        String requesterUid = (String) execution.getVariable("requesterUid");
        String priority = (String) execution.getVariable("priority");
        String category = (String) execution.getVariable("category");

        log.info("Requester UID: {}", requesterUid);
        log.info("Priority: {}", priority);
        log.info("Category: {}", category);

        // 实际项目中，这里应该：
        // 1. 发送升级通知给IT经理
        // 2. 记录升级事件
        // 3. 可能需要通知其他相关人员
        // 4. 设置升级级别和处理时限

        try {
            // 模拟升级通知
            log.info("Sending escalation notification for priority: {}", priority);

            // 设置升级结果
            execution.setVariable("escalationNotified", true);
            execution.setVariable("escalationNotifiedTime", System.currentTimeMillis());
            execution.setVariable("escalationLevel", "MANAGER");
            execution.setVariable("escalationStatus", "ESCALATED");

            log.info("Escalation notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send escalation notification", e);
            execution.setVariable("escalationNotified", false);
            execution.setVariable("escalationError", e.getMessage());
        }
    }
}
