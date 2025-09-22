/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 13:06:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 17:51:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.service.TicketNotificationService;

import org.flowable.dmn.api.DmnDecisionService;
import com.bytedesk.core.utils.BdDateUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class TicketSLAService {
    
    @Autowired
    private TicketNotificationService notificationService;

    @Autowired
    private DmnDecisionService dmnDecisionService;

    /**
     * 根据工单类型和优先级确定 SLA 规则
     */
    public Map<String, Object> determineSLA(String category, String priority) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("category", category);
        variables.put("priority", priority);
        
        return dmnDecisionService.createExecuteDecisionBuilder()
                .decisionKey("assignTicket")
                .variables(variables)
                .executeWithSingleResult();
    }
    
    /**
     * 检查工单是否违反 SLA
     */
    public Boolean isSLABreached(TicketEntity ticket) {
        Map<String, Object> sla = determineSLA(ticket.getCategoryUid(), ticket.getPriority());
        
        ZonedDateTime now = BdDateUtils.now();
        ZonedDateTime createdAt = ticket.getCreatedAt();
        
        // 检查响应时间
        // if (ticket.getFirstResponseTime() == null) {
        //     long responseTimeLimit = ((Number) sla.get("responseTime")).longValue();
        //     if (ChronoUnit.MINUTES.between(createdAt, now) > responseTimeLimit) {
        //         notifyResponseTimeBreached(ticket);
        //         return true;
        //     }
        // }
        
        // 检查解决时间
        if (!"已关闭".equals(ticket.getStatus())) {
            long resolutionTimeLimit = ((Number) sla.get("resolutionTime")).longValue();
            if (ChronoUnit.HOURS.between(createdAt, now) > resolutionTimeLimit) {
                notifyResolutionTimeBreached(ticket);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 更新工单的首次响应时间
     */
    public void recordFirstResponse(TicketEntity ticket) {
        // if (ticket.getFirstResponseTime() == null) {
        //     ticket.setFirstResponseTime(BdDateUtils.now());
        //     ticketRepository.save(ticket);
        // }
    }
    
    /**
     * 响应时间超时通知
     */
    // private void notifyResponseTimeBreached(TicketEntity ticket) {
    //     String message = String.format(
    //         "工单 #%d 超出响应时限！创建时间：%s",
    //         ticket.getId(),
    //         ticket.getCreatedAt()
    //     );
    //     notificationService.notifyManager(ticket.getAssignee(), message);
    // }
    
    /**
     * 解决时间超时通知
     */
    private void notifyResolutionTimeBreached(TicketEntity ticket) {
        String message = String.format(
            "工单 #%d 超出解决时限！创建时间：%s",
            ticket.getId(),
            ticket.getCreatedAt()
        );
        notificationService.notifyManager(ticket.getAssignee().getUid(), message);
    }
} 