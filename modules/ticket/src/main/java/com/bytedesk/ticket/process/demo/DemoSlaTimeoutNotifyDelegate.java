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
 * 演示流程 - SLA超时通知委托
 * 用于IT支持流程中的SLA超时通知
 */
@Slf4j
@Component("demoSlaTimeoutNotifyDelegate")
public class DemoSlaTimeoutNotifyDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();

        log.info("=== Demo SLA Timeout Notify Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Processing SLA timeout notification...");

        // 获取流程变量
        String requesterUid = (String) execution.getVariable("requesterUid");
        String priority = (String) execution.getVariable("priority");
        String category = (String) execution.getVariable("category");
        Object slaTimeObj = execution.getVariable("slaTimeISO");
        String slaTime = slaTimeObj != null ? slaTimeObj.toString() : "PT4H";

        log.info("Requester UID: {}", requesterUid);
        log.info("Priority: {}", priority);
        log.info("Category: {}", category);
        log.info("SLA Time: {}", slaTime);

        // 实际项目中，这里应该：
        // 1. 记录SLA超时事件
        // 2. 发送超时通知给支持人员和用户
        // 3. 计算超时时长
        // 4. 可能触发升级处理

        try {
            // 模拟SLA超时通知
            log.info("SLA timeout detected for category: {}, priority: {}", category, priority);

            // 设置SLA超时结果
            execution.setVariable("slaTimeout", true);
            execution.setVariable("slaTimeoutTime", System.currentTimeMillis());
            execution.setVariable("slaTimeoutStatus", "TIMEOUT");

            // 根据优先级决定是否需要升级
            boolean needEscalation = "HIGH".equals(priority) || "URGENT".equals(priority);
            execution.setVariable("slaNeedEscalation", needEscalation);

            log.info("SLA timeout notification completed, escalation needed: {}", needEscalation);
        } catch (Exception e) {
            log.error("Failed to process SLA timeout notification", e);
            execution.setVariable("slaTimeoutError", e.getMessage());
        }
    }
}
