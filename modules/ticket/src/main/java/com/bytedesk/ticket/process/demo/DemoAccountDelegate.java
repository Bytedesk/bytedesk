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
 * 演示流程 - 账号处理委托
 * 用于IT支持流程中的账号权限自动处理
 */
@Slf4j
@Component("demoAccountDelegate")
public class DemoAccountDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();

        log.info("=== Demo Account Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Processing account request...");

        // 获取流程变量
        String requesterUid = (String) execution.getVariable("requesterUid");
        String category = (String) execution.getVariable("category");

        log.info("Requester UID: {}", requesterUid);
        log.info("Category: {}", category);

        // 实际项目中，这里应该：
        // 1. 根据请求类型自动处理账号权限
        // 2. 重置密码、解锁账号、分配权限等
        // 3. 记录操作日志

        try {
            // 模拟账号处理
            log.info("Processing account request for: {}", category);

            // 设置处理结果
            execution.setVariable("accountProcessed", true);
            execution.setVariable("accountProcessTime", System.currentTimeMillis());

            log.info("Account request processed successfully");
        } catch (Exception e) {
            log.error("Account request processing failed", e);
            execution.setVariable("accountProcessed", false);
            execution.setVariable("accountProcessError", e.getMessage());
        }
    }
}
