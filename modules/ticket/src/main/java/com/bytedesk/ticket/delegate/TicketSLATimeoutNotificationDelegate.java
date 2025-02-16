/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-02 23:15:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 09:08:47
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
 * SLA超时通知
 * 
 * 两种服务任务的区别：
 * SLA 超时：自动触发，基于时间
 * 工单升级：人工触发，基于状态
 */
@Slf4j
@Component("ticketSLATimeoutNotificationDelegate")
public class TicketSLATimeoutNotificationDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("SLA timeout for process: {}", processInstanceId);
        
        // 获取流程变量
        String status = (String) execution.getVariable("status");
        Date startTime = (Date) execution.getVariable("startTime");
        String slaTime = (String) execution.getVariable("slaTime");
        
        // 设置 SLA 相关变量
        execution.setVariable("slaTimeoutTime", new Date());
        execution.setVariable("slaTimeoutReason", "超过处理时限");
        
        log.info("SLA timeout notification - processId: {}, status: {}, startTime: {}, slaTime: {}", 
            processInstanceId, status, startTime, slaTime);
    }
}
