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
package com.bytedesk.ticket.ticket.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.ticket.TicketSLAService;
import com.bytedesk.ticket.ticket_sla.TicketSlaTypeEnum;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class TicketSLATimeoutNotificationDelegate implements JavaDelegate {

    private final TicketSLAService ticketSLAService;

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("SLA timeout for process: {}", processInstanceId);
        
        // 获取流程变量
        String status = (String) execution.getVariable("status");
        Date startTime = (Date) execution.getVariable("startTime");
        String slaTime = (String) execution.getVariable("slaTime");
        String slaTypeValue = (String) execution.getVariable(TicketConsts.TICKET_VARIABLE_SLA_TYPE);
        String activityId = execution.getCurrentActivityId();
        TicketSlaTypeEnum slaType = resolveSlaType(slaTypeValue, activityId);
        
        // 设置 SLA 相关变量
        execution.setVariable("slaTimeoutTime", new Date());
        execution.setVariable("slaTimeoutReason", "超过处理时限");
        boolean breached = ticketSLAService.markBreachedByProcessInstance(processInstanceId, slaType, "超过处理时限");
        execution.setVariable("slaBreached", breached);
        execution.setVariable("slaBreachedType", breached ? slaType.name() : null);
        
        log.info("SLA timeout notification - processId: {}, status: {}, startTime: {}, slaTime: {}", 
            processInstanceId, status, startTime, slaTime);
    }

    private TicketSlaTypeEnum resolveSlaType(String slaTypeValue, String activityId) {
        if (StringUtils.hasText(slaTypeValue)) {
            return TicketSlaTypeEnum.valueOf(slaTypeValue);
        }
        String normalizedActivityId = StringUtils.hasText(activityId) ? activityId.toLowerCase() : "";
        if (normalizedActivityId.contains("claim")) {
            return TicketSlaTypeEnum.CLAIM;
        }
        if (normalizedActivityId.contains("first_response") || normalizedActivityId.contains("firstresponse")) {
            return TicketSlaTypeEnum.FIRST_RESPONSE;
        }
        if (normalizedActivityId.contains("verify")) {
            return TicketSlaTypeEnum.CUSTOMER_VERIFY;
        }
        return TicketSlaTypeEnum.RESOLUTION;
    }
}
