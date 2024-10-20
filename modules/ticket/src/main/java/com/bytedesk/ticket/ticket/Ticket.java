/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:13:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-16 19:04:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工单
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_ticket")
public class Ticket extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String content;

    @Builder.Default
    @Column(name = "ticket_state")
    private String state = TicketStateEnum.INIT.name();
    
    // private String priority;

    @JsonIgnore
    @ManyToOne
    private ThreadEntity thread;

}
