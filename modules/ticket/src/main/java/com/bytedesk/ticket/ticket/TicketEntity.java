/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-20 17:03:25
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
import com.bytedesk.ticket.ticket.listener.TicketEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners({TicketEntityListener.class})
@Entity(name = "bytedesk_ticket")
public class TicketEntity extends BaseEntity {
    
    private String title;           // 工单标题(必填)
    private String description;     // 工单描述
    private String status;          // 状态(新建/处理中/已解决/已关闭)
    private String priority;        // 优先级(低/中/高/紧急)
    private String category;        // 分类
    private String assignee;        // 处理人
    private String reporter;        // 报告人
    private String comment;        // 工单备注

} 