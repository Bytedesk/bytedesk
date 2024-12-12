/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 14:59:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:34:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.notification;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bytedesk_ticket_notification")
@EqualsAndHashCode(callSuper = true)
public class TicketNotificationEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String content;
    
    @Column(name = "target_id")
    private Long targetId;
    
    @Column(name = "target_type")
    private String targetType;
    
    @Column(name = "is_read")
    private Boolean read = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
} 