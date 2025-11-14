/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-20 10:27:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.ticket.ticket_settings.sub.TicketBasicSettingsEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
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
 * TicketSettings entity for content categorization and organization
 * Provides ticketSettings functionality for various system entities
 * 
 * Database Table: bytedesk_core_ticketSettings
 * Purpose: Stores ticketSettings definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({TicketSettingsEntityListener.class})
@Table(name = "bytedesk_ticket_settings")
public class TicketSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the ticketSettings
     */
    private String name;

    /**
     * Description of the ticketSettings
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Whether this is a default settings template for new entities
     * Only one settings per organization should have isDefault=true
     */
    @lombok.Builder.Default
    private Boolean isDefault = false;

     /**
     * Whether the settings is enabled
     */
    @lombok.Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    // 






    // ====== 发布版本 ======
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private TicketBasicSettingsEntity basicSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketStatusFlowSettingsEntity statusFlowSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketPrioritySettingsEntity prioritySettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketAssignmentSettingsEntity assignmentSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketNotificationSettingsEntity notificationSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketCustomFieldSettingsEntity customFieldSettings;

    // ====== 草稿版本 ======
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private TicketBasicSettingsEntity draftBasicSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketStatusFlowSettingsEntity draftStatusFlowSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketPrioritySettingsEntity draftPrioritySettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketAssignmentSettingsEntity draftAssignmentSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketNotificationSettingsEntity draftNotificationSettings;

    // @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // private TicketCustomFieldSettingsEntity draftCustomFieldSettings;
    
    /**
     * Whether there are unpublished changes in draft
     */
    @lombok.Builder.Default
    private Boolean hasUnpublishedChanges = false;

    /**
     * Last published time
     */
    private ZonedDateTime publishedAt;

}
