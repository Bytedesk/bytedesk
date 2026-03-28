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
package com.bytedesk.core.workflow_settings;

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
 * WorkflowSettings entity for content categorization and organization
 * Provides workflow_settingsging functionality for various system entities
 * 
 * Database Table: bytedesk_core_workflow_settings
 * Purpose: Stores workflow_settings definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({WorkflowSettingsEntityListener.class})
@Table(name = "bytedesk_core_workflow_settings")
public class WorkflowSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the workflow_settings
     */
    private String name;

    /**
     * Description of the workflow_settings
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Whether the workflow settings template is enabled.
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    /**
     * Type of workflow_settings (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "workflow_settings_type")
    private String type = WorkflowSettingsTypeEnum.CUSTOMER.name();

    /**
     * Iframe tabs stored as JSON array.
     */
    @Column(name = "iframe_tabs", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String iframeTabsJson;

}
