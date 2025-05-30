/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 09:11:45
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
import java.util.stream.Collectors;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;
import com.bytedesk.service.settings.RobotSettings;

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
 * WorkgroupEntity和skills的区别主要在于:
 * - 组织结构 vs 能力标签
 * - WorkgroupEntity是组织结构层面的分组,比如"售前组"、"售后组"、"技术支持组"等
 * - Skills是能力标签层面的标识,比如"Java"、"Python"、"数据库"等技术能力
 * 
 * WorkgroupEntity和agent的区别主要在于:
 * - agent：一对一人工客服，不支持机器人接待
 * - robot：机器人客服，不支持转人工
 * - workgroup：工作组，支持机器人接待，支持转人工
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
@Table(name = "bytedesk_service_workgroup")
public class WorkgroupEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultWorkGroupAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_WORKGROUP_DESCRIPTION;

    @Builder.Default
    private String routingMode = WorkgroupRoutingModeEnum.ROUND_ROBIN.name();

    @Builder.Default
    private String status = WorkgroupStateEnum.AVAILABLE.name();

    // 留言设置
    @Embedded
    @Builder.Default
    private MessageLeaveSettings messageLeaveSettings = new MessageLeaveSettings();

    @Embedded
    @Builder.Default
    private RobotSettings robotSettings = new RobotSettings();

    @Embedded
    @Builder.Default
    private ServiceSettings serviceSettings = new ServiceSettings();

    @Embedded
    @Builder.Default
    private QueueSettings queueSettings = new QueueSettings();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    // 为方便路由分配客服，特修改成list
    private List<AgentEntity> agents = new ArrayList<>();

    // 留言处理agent - 多个工作组可以共用同一个留言处理客服（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    private AgentEntity messageLeaveAgent;

    // 监控管理员agents
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<AgentEntity> monitorAgents = new ArrayList<>();

    /** 存储下一个待分配的客服等信息 */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 邀请设置
    @ManyToOne(fetch = FetchType.LAZY)
    private InviteSettingsEntity inviteSettings;

    // 意图识别
    @ManyToOne(fetch = FetchType.LAZY)
    private IntentionSettingsEntity intentionSetting;

    /**
     * 获取可用客服
     * @return 可用客服列表
     */
    @JsonIgnore
    public List<AgentEntity> getAvailableAgents() {
        if (this.agents == null) {
            return new ArrayList<>();
        }
        return this.agents.stream().filter(agent -> agent.isConnectedAndAvailable()).collect(Collectors.toList());
    }

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
    public Boolean isConnected() {
        if (this.agents == null || this.agents.isEmpty()) {
            return false;
        }
        return this.agents.stream().anyMatch(agent -> agent.getConnected());
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

    // 监控客服组登录坐席、开启自动领取坐席数、空闲坐席数、领取会话数、已处理会话数、流失会话数、留言数。
    // agent connected count
    @JsonIgnore
    public long getConnectedAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.getConnected()).count();
    }

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
