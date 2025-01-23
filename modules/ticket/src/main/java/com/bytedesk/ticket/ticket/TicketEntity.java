/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 15:32:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.ticket.ticket.listener.TicketEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@EntityListeners({TicketEntityListener.class})
@Entity(name = "bytedesk_ticket")
public class TicketEntity extends BaseEntity {
    
    private String title;           // 工单标题(必填)
    private String description;     // 工单描述

    @Builder.Default
    private String status = TicketStatusEnum.NEW.name();          // 状态(新建/处理中/已解决/已关闭)
    
    @Builder.Default
    private String priority = TicketPriorityEnum.LOW.name();        // 优先级(低/中/高/紧急)

    private String categoryUid;        // 分类

    // 一个工单一个处理人，一个处理人可以处理多个工单
    @ManyToOne
    @JoinColumn(name = "assignee_uid")
    private AgentEntity assignee;        // 处理人

    // 一个工单一个报告人，一个报告人可以报告多个工单
    @ManyToOne
    @JoinColumn(name = "reporter_uid")
    private AgentEntity reporter;        // 报告人

    private String comment;        // 工单备注
} 