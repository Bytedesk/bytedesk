/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 10:17:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 10:26:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.feature;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_core_feature")
@EqualsAndHashCode(callSuper = true)
public class FeatureEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;        // 功能代码,如: im.chat, ticket.basic 

    @Column(nullable = false)
    private String name;        // 功能名称

    private String description; // 功能描述
    
    @Column(name = "module_name")
    private String moduleName;  // 所属模块,如: im, ticket, forum等

    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序

    private Boolean enabled = true; // 是否启用

    @Column(name = "is_premium")
    private Boolean isPremium = false; // 是否高级功能

    @Column(name = "min_version")
    private String minVersion;  // 最低支持版本

    @Column(name = "config_schema", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String configSchema = BytedeskConsts.EMPTY_JSON_STRING; // 配置项schema(JSON)

    @Column(name = "default_config", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String defaultConfig = BytedeskConsts.EMPTY_JSON_STRING; // 默认配置(JSON)
} 