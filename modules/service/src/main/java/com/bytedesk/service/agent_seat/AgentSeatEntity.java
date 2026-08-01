/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_seat;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.service.agent_seat.enums.AgentSeatSourceEnum;
import com.bytedesk.service.agent_seat.enums.AgentSeatStatusEnum;
import com.bytedesk.service.agent_seat.enums.AgentSeatTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * AgentSeat entity for content categorization and organization
 * Provides agent_seat functionality for various system entities
 * 
 * Database Table: bytedesk_service_agent_seat
 * Purpose: Stores agent_seat definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({AgentSeatEntityListener.class})
@Table(name = "bytedesk_service_agent_seat")
public class AgentSeatEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "seat_no", length = 128)
    private String seatNo;

    @Builder.Default
    @Column(name = "seat_source", length = 32)
    private String source = AgentSeatSourceEnum.EXTRA.name();

    @Builder.Default
    @Column(name = "seat_status", length = 32)
    private String status = AgentSeatStatusEnum.AVAILABLE.name();

    @Builder.Default
    @Column(name = "is_base_seat")
    private Boolean baseSeat = false;

    /**
     * expireAt = null 表示永不过期
     */
    @Column(name = "expire_at")
    private ZonedDateTime expireAt;

    @Column(name = "assigned_agent_uid", length = 64)
    private String assignedAgentUid;

    @Column(name = "assigned_at")
    private ZonedDateTime assignedAt;

    @Column(name = "released_at")
    private ZonedDateTime releasedAt;

    /**
     * Type of agent_seat (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "agent_seat_type")
    private String type = AgentSeatTypeEnum.THREAD.name();

}
