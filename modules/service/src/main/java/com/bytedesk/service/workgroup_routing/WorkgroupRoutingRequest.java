/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 14:24:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup_routing;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class WorkgroupRoutingRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    /**
     * workgroup_routing_type
     * 用途：区分 THREAD/CUSTOMER/TICKET 等不同场景的数据。
     */
    // private String type;

    // ==================== Routing State (Workgroup -> Next Agent) ====================

    /**
     * 所属工作组 UID（用于维护该工作组的路由状态）。
     */
    private String workgroupUid;

    /**
     * 当前路由模式（通常来自 WorkgroupSettingsEntity.routingMode）。
     */
    private String routingMode;

    /**
     * 预计算好的“下一个将被分配的客服”UID。
     */
    private String nextAgentUid;

    /**
     * 轮询游标（ROUND_ROBIN 等模式使用）。
     */
    private Long cursor;

    /**
     * 预留扩展字段。
     */
    private String routingExtra;
}
