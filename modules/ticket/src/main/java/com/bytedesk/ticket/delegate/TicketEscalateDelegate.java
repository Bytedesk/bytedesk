/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 13:33:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 09:21:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 升级工单
 * 
 * JavaDelegate 用于实现服务任务(Service Task)的业务逻辑，通常用于:
 * 自动化处理
 * 系统集成
 * 外部服务调用
 * 复杂业务规则执行
 * 
 * 两种服务任务的区别：
 * SLA 超时：自动触发，基于时间
 * 工单升级：人工触发，基于状态
 * 
 * 用途：实现服务任务的具体业务逻辑
 * 绑定位置：服务任务节点
 * 主要场景：自动化处理、系统集成、规则执行
 * 特点：一次性执行，完成特定业务功能  
 */
@Slf4j
@Component("ticketEscalateDelegate")
public class TicketEscalateDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Escalating ticket for process: {}", processInstanceId);
        
        // 获取流程变量
        String status = (String) execution.getVariable("status");
        String solution = (String) execution.getVariable("solution");
        
        // 设置升级相关变量
        execution.setVariable("escalatedTime", new Date());
        execution.setVariable("escalatedReason", "工单需要更高级别处理");
        execution.setVariable("previousStatus", status);
        
        // 更新工单状态为已升级
        execution.setVariable("status", "ESCALATED");
        
        log.info("Ticket escalated - processId: {}, previous status: {}, solution: {}", 
            processInstanceId, status, solution);
    }
} 