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
package com.bytedesk.service.agent_next;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
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
 * AgentNext entity for content categorization and organization
 * Provides agent_nextging functionality for various system entities
 * 
 * Database Table: bytedesk_core_agent_next
 * Purpose: Stores agent_next definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({AgentNextEntityListener.class})
@Table(name = "bytedesk_core_agent_next")
public class AgentNextEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the agent_next
     */
    private String name;

    /**
     * Description of the agent_next
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of agent_next (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "agent_next_type")
    private String type = AgentNextTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the agent_next display
     */
    @Builder.Default
    @Column(name = "agent_next_color")
    private String color = "red";

    /**
     * Display order of the agent_next
     */
    @Builder.Default
    @Column(name = "agent_next_order")
    private Integer order = 0;
}
