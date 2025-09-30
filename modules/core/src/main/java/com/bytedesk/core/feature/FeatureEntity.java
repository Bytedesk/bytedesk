/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 10:17:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-11 17:01:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.feature;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Feature entity for system feature management and licensing
 * Manages feature flags, configurations, and premium feature access
 * 
 * Database Table: bytedesk_core_feature
 * Purpose: Stores feature definitions, configurations, and access control settings
 */
@Data
@Entity
@Table(name = "bytedesk_core_feature")
@EqualsAndHashCode(callSuper = true)
public class FeatureEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * Unique feature code identifier (e.g., im.chat, ticket.basic)
     */
    @Column(nullable = false, unique = true)
    private String code;        // 功能代码,如: im.chat, ticket.basic 

    /**
     * Display name of the feature
     */
    @Column(nullable = false)
    private String name;        // 功能名称

    /**
     * Description of the feature functionality
     */
    private String description; // 功能描述
    
    /**
     * Module name that the feature belongs to (e.g., im, ticket, forum)
     */
    @Column(name = "module_name")
    private String moduleName;  // 所属模块,如: im, ticket, forum等

    /**
     * Sort order for feature display
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序

    /**
     * Whether the feature is enabled
     */
    private Boolean enabled = true; // 是否启用

    /**
     * Whether this is a premium feature requiring special access
     */
    @Column(name = "is_premium")
    private Boolean isPremium = false; // 是否高级功能

    /**
     * Minimum version required to support this feature
     */
    @Column(name = "min_version")
    private String minVersion;  // 最低支持版本

    /**
     * JSON schema for feature configuration options
     */
    @Column(name = "config_schema", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String configSchema = BytedeskConsts.EMPTY_JSON_STRING; // 配置项schema(JSON)

    /**
     * Default configuration values for the feature in JSON format
     */
    @Column(name = "default_config", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String defaultConfig = BytedeskConsts.EMPTY_JSON_STRING; // 默认配置(JSON)
} 