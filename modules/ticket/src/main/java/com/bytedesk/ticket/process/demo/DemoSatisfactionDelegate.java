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
 * 演示流程 - 满意度调查委托
 * 用于IT支持流程中发送满意度调查
 */
@Slf4j
@Component("demoSatisfactionDelegate")
public class DemoSatisfactionDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();

        log.info("=== Demo Satisfaction Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Sending satisfaction survey...");

        // 获取流程变量
        String requesterUid = (String) execution.getVariable("requesterUid");

        log.info("Requester UID: {}", requesterUid);

        // 实际项目中，这里应该：
        // 1. 生成满意度调查问卷
        // 2. 发送调查链接给用户
        // 3. 记录调查发送状态
        // 4. 设置调查截止时间

        try {
            // 模拟发送满意度调查
            log.info("Sending satisfaction survey to user: {}", requesterUid);

            // 设置调查结果
            execution.setVariable("satisfactionSurveySent", true);
            execution.setVariable("satisfactionSurveySentTime", System.currentTimeMillis());
            execution.setVariable("satisfactionSurveyStatus", "PENDING");

            log.info("Satisfaction survey sent successfully");
        } catch (Exception e) {
            log.error("Failed to send satisfaction survey", e);
            execution.setVariable("satisfactionSurveySent", false);
            execution.setVariable("satisfactionSurveyError", e.getMessage());
        }
    }
}
