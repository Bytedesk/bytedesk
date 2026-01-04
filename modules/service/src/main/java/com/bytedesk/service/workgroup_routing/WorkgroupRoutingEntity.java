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
package com.bytedesk.service.workgroup_routing;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
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
 * WorkgroupRouting entity for content categorization and organization
 * Provides workgroup_routingging functionality for various system entities
 * 
 * Database Table: bytedesk_service_workgroup_routing
 * Purpose: Stores workgroup_routing definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({WorkgroupRoutingEntityListener.class})
@Table(name = "bytedesk_service_workgroup_routing")
public class WorkgroupRoutingEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the workgroup_routing
     */
    private String name;

    /**
     * Description of the workgroup_routing
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of workgroup_routing (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    // @Builder.Default
    // @Column(name = "workgroup_routing_type")
    // private String type = WorkgroupRoutingTypeEnum.CUSTOMER.name();

    // ==================== Routing State (Workgroup -> Next Agent) ====================

    /**
     * 所属工作组 UID（用于维护该工作组的路由状态）。
     */
    @Column(name = "workgroup_uid")
    private String workgroupUid;

    /**
     * 当前使用的路由模式（来自 WorkgroupSettingsEntity.routingMode）。
     */
    @Column(name = "routing_mode")
    private String routingMode;

    /**
     * 预计算好的“下一个将被分配的客服”UID。
     */
    @Column(name = "next_agent_uid")
    private String nextAgentUid;

    /**
     * 轮询游标：用于 ROUND_ROBIN 在持久化层维护 nextAgent。
     */
    @Builder.Default
    @Column(name = "routing_cursor")
    private Long cursor = 0L;

    /**
     * 预留扩展字段（例如缓存可视化需要的快照）。
     */
    @Builder.Default
    @Column(name = "routing_extra", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String routingExtra = BytedeskConsts.EMPTY_JSON_STRING;

}
