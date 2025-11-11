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

    /**
     * 关联工作组UID。一个工作组对应一套工单配置；与 orgUid 组合唯一。
     */
    @Column(name = "workgroup_uid")
    private String workgroupUid;

    /**
     * 扩展配置，JSON字符串，存储整体 TicketSettings 结构（basic/statusFlow/priorities/assignment/notifications/customFields）。
     * 采用单列 JSON 方式以减少多表拆分复杂度，后续可按需迁移。
     */
    @Builder.Default
    @Column(name = "settings_json", length = 4096)
    private String settingsJson = com.bytedesk.core.constant.BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 是否已初始化(首次访问时可能为默认模板)。
     */
    @Builder.Default
    @Column(name = "initialized")
    private Boolean initialized = Boolean.FALSE;

    /**
     * 最后修改用户UID，审计用途。
     */
    @Column(name = "last_modified_user_uid")
    private String lastModifiedUserUid;
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of ticketSettings (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "settings_type")
    private String type = TicketSettingsTypeEnum.CUSTOMER.name();

}
