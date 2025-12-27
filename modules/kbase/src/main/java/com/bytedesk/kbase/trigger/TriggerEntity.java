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
package com.bytedesk.kbase.trigger;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;

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
 * Trigger entity for content categorization and organization
 * Provides triggerging functionality for various system entities
 * 
 * Database Table: bytedesk_kbase_trigger
 * Purpose: Stores trigger definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({TriggerEntityListener.class})
@Table(name = "bytedesk_kbase_trigger")
public class TriggerEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the trigger
     */
    private String name;

    /**
     * Description of the trigger
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /** Whether this trigger is enabled */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    /**
     * Trigger behavior key (stable identifier used by runtime).
     */
    @Builder.Default
    @Column(name = "trigger_key")
    private String triggerKey = TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE;

    /**
     * Trigger config as JSON string. Concrete schema depends on triggerKey.
     */
    @Builder.Default
    @Column(name = "trigger_config", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String config = "{}";

    /**
     * Type of trigger (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "trigger_type")
    private String type = TriggerTypeEnum.CUSTOMER.name();


}
