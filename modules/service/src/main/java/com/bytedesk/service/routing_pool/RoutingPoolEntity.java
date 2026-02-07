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
package com.bytedesk.service.routing_pool;

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
 * RoutingPool entity for content categorization and organization
 * Provides routing functionality for various system entities
 * 
 * Database Table: bytedesk_service_routing_pool
 * Purpose: Stores routing definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({RoutingPoolEntityListener.class})
@Table(name = "bytedesk_service_routing_pool")
public class RoutingPoolEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the routing_pool
     */
    private String name;

    /**
     * Description of the routing_pool
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of routing_pool (MANUAL_THREAD, etc.)
     */
    @Builder.Default
    @Column(name = "routing_pool_type")
    private String type = RoutingPoolTypeEnum.MANUAL_THREAD.name();

    /**
     * Manual routing pool status
     */
    @Builder.Default
    @Column(name = "routing_pool_status")
    private String status = RoutingPoolStatusEnum.WAITING.name();

    /**
     * Related workgroup uid (for MANUAL_THREAD)
     */
    @Column(name = "workgroup_uid")
    private String workgroupUid;

    /**
     * Related thread uid (for MANUAL_THREAD)
     */
    @Column(name = "thread_uid")
    private String threadUid;

    /**
     * Related thread topic (for MANUAL_THREAD)
     */
    @Column(name = "thread_topic")
    private String threadTopic;

    /**
     * Visitor/user snapshot (JSON), used by desktop routing pool list.
     */
    @Column(name = "user_json", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String userJson;

}
