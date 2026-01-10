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
 * 演示流程 - 关闭工单委托
 * 用于IT支持流程中关闭工单并发送满意度调查
 */
@Slf4j
@Component("demoCloseTicketDelegate")
public class DemoCloseTicketDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();

        log.info("=== Demo Close Ticket Delegate ===");
        log.info("Process Instance ID: {}", processInstanceId);
        log.info("Closing ticket...");

        // 获取流程变量
        String requesterUid = (String) execution.getVariable("requesterUid");
        String category = (String) execution.getVariable("category");

        log.info("Requester UID: {}", requesterUid);
        log.info("Category: {}", category);

        // 实际项目中，这里应该：
        // 1. 更新工单状态为已关闭
        // 2. 记录关闭时间和关闭人
        // 3. 触发满意度调查

        try {
            // 模拟关闭工单
            log.info("Closing IT support ticket for category: {}", category);

            // 设置关闭结果
            execution.setVariable("ticketClosed", true);
            execution.setVariable("ticketCloseTime", System.currentTimeMillis());
            execution.setVariable("ticketStatus", "CLOSED");

            log.info("IT support ticket closed successfully");
        } catch (Exception e) {
            log.error("Failed to close IT support ticket", e);
            execution.setVariable("ticketClosed", false);
            execution.setVariable("ticketCloseError", e.getMessage());
        }
    }
}
