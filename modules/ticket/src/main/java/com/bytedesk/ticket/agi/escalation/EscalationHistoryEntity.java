/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:12:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 11:06:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.agi.escalation;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_escalation_history")
@EqualsAndHashCode(callSuper = true)
public class EscalationHistoryEntity extends BaseEntity {

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;
    
    @Column(name = "from_user_id")
    private Long fromUserId;
    
    @Column(name = "to_user_id")
    private Long toUserId;
    
    private String reason;
    
    @Column(name = "notified_users")
    private String notifiedUsers;  // 已通知用户列表
} 