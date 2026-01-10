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
 * 演示流程 - 打款处理委托
 * 用于报销审批流程中的自动打款
 */
@Slf4j
@Component("demoPaymentDelegate")
public class DemoPaymentDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();

        log.info("=== Demo Payment Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Processing payment...");

        // 获取流程变量
        String applicantUid = (String) execution.getVariable("applicantUid");
        Object amountObj = execution.getVariable("amount");
        String amount = amountObj != null ? amountObj.toString() : "0";

        log.info("Processing payment for applicant: {}, amount: {}", applicantUid, amount);

        // 实际项目中，这里应该：
        // 1. 调用财务系统API进行打款
        // 2. 记录打款信息
        // 3. 更新报销单状态

        try {
            // 模拟打款处理
            log.info("Payment processing initiated for amount: {}", amount);

            // 设置打款结果变量
            execution.setVariable("paymentStatus", "SUCCESS");
            execution.setVariable("paymentTime", System.currentTimeMillis());

            log.info("Payment processed successfully");
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            execution.setVariable("paymentStatus", "FAILED");
            execution.setVariable("paymentError", e.getMessage());
        }
    }
}
