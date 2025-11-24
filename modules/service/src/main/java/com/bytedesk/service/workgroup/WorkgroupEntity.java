/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-17 09:06:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Workgroup entity for customer service team management
 * Manages workgroups that support both robot and human agent services
 * 
 * Database Table: bytedesk_service_workgroup
 * Purpose: Stores workgroup configurations, agent assignments, and service settings
 * 
 * Key differences:
 * - WorkgroupEntity vs Skills: Organizational structure vs capability labels
 * - WorkgroupEntity vs Agent: Group support (robot + human) vs individual agent only
 * - WorkgroupEntity vs Robot: Group with routing vs standalone robot
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = { "agents" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(value = { WorkgroupEntityListener.class })
@JsonIgnoreProperties({
    "hibernateLazyInitializer", 
    "handler",
})
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
@Table(
    name = "bytedesk_service_workgroup",
    indexes = {
        @Index(name = "idx_workgroup_uid", columnList = "uuid")
    }
)
public class WorkgroupEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Display name of the workgroup
     */
    @NotBlank
    private String nickname;

    /**
     * Workgroup avatar or profile picture URL
     */
    @Builder.Default
    private String avatar = AvatarConsts.getDefaultWorkGroupAvatarUrl();

    /**
     * Description of the workgroup's function
     */
    @Builder.Default
    private String description = I18Consts.I18N_WORKGROUP_DESCRIPTION;

    // 路由模式迁移至 WorkgroupSettingsEntity
    // 为保持兼容性，保留委托型 getter，从 settings 读取；空值使用默认

    /**
     * Current status of the workgroup (AVAILABLE, BUSY, OFFLINE, etc.)
     */
    @Builder.Default
    private String status = WorkgroupStateEnum.AVAILABLE.name();

    /**
     * Configuration settings reference
     * All settings are managed through the settings entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkgroupSettingsEntity settings;

    /**
     * Agents assigned to this workgroup
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    // 为方便路由分配客服，特修改成list
    private List<AgentEntity> agents = new ArrayList<>();

    /**
     * Agent responsible for handling offline messages
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private AgentEntity messageLeaveAgent;

    /**
     * Additional configuration data stored as JSON
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Whether the workgroup is enabled and active
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @JsonIgnore
    public AgentEntity getMessageLeaveAgent() {
        if (this.messageLeaveAgent == null) {
            if (this.agents == null || this.agents.isEmpty()) {
                // 这里可以考虑记录一个警告日志
                return null;
            }
            return this.agents.stream()
                .findFirst()
                .orElse(null);
        }
        return this.messageLeaveAgent;
    }

    @JsonIgnore
    public UserProtobuf toUserProtobuf() {
        return UserProtobuf.builder()
            .uid(this.getUid())
            .nickname(this.getNickname())
            .avatar(this.getAvatar())
            .type(UserTypeEnum.WORKGROUP.name())
            .build();
    }

    /**
     * 兼容旧代码：获取路由模式
     * 优先从 settings 读取；若无 settings 或为空，使用安全默认值 ROUND_ROBIN
     */
    public String getRoutingMode() {
        if (this.settings != null && this.settings.getRoutingMode() != null) {
            return this.settings.getRoutingMode();
        }
        return WorkgroupRoutingModeEnum.ROUND_ROBIN.name();
    }

    // agent connected count
    // @JsonIgnore
    // public long getConnectedAgentCount() {
    //     if (this.agents == null || this.agents.isEmpty()) {
    //         return 0;
    //     }
    //     return this.agents.stream().filter(agent -> agent.isConnected()).count();
    // }

    // agent available count
    @JsonIgnore
    public long getAvailableAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isAvailable()).count();
    }

    // agent offline count
    @JsonIgnore
    public long getOfflineAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isOffline()).count();
    }

    // agent busy count
    @JsonIgnore
    public long getBusyAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isBusy()).count();
    }
    
    // agent away count
    @JsonIgnore
    public long getAwayAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isAway()).count();
    }

}
