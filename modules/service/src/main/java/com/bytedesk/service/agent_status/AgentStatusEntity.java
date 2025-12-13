/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:19:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 15:00:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.user.UserProtobuf;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 接待状态变更记录
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_agent_status")
public class AgentStatusEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String status;

    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Duration (in seconds) that the agent stayed in this status before switching.
     */
    @Builder.Default
    @Column(name = "status_duration_seconds")
    private Long durationSeconds = 0L;

    public UserProtobuf getAgent() {
        return JSON.parseObject(agent, UserProtobuf.class);
    }

    public String getAgentString() {
        return agent;
    }

}
