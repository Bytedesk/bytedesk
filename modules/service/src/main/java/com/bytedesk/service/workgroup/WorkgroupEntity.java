/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:46:07
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

    // @Embedded
    // @Builder.Default
    // private InviteSettings inviteSettings = new InviteSettings();

    // @Embedded
    // @Builder.Default
    // private IntentionSettings intentionSettings = new IntentionSettings();

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
    public List<AgentEntity> getAvailableAgents() {
        return this.agents.stream().filter(agent -> agent.isConnectedAndAvailable()).collect(Collectors.toList());
    }

    /**
     * 检查是否超载
     */
    public boolean isOverloaded() {
        // 1. 检查总会话数是否超限
        // if (getCurrentThreadCount() >= getMaxConcurrentThreads()) {
        //     return true;
        // }

        // 2. 检查等待队列是否超限 
        // if (getWaitingThreadCount() >= getMaxWaitingThreads()) {
        //     return true;
        // }

        // 3. 检查客服平均负载是否超限
        // if (getOnlineAgentCount() > 0) {
        //     double avgLoad = (double) getCurrentThreadCount() / getOnlineAgentCount();
        //     if (avgLoad >= getMaxThreadPerAgent()) {
        //         return true;
        //     }
        // }

        // 4. 检查负载率是否超过告警阈值
        // double loadRate = (double) getCurrentThreadCount() / getMaxConcurrentThreads();
        // if (loadRate >= getAlertThreshold()) {
        //     return true;
        // }

        return false;
    }

    // TODO: 根据算法选择一个agent
    // TODO: 增加agent-currentThreadCount数量
    // TODO: 模拟测试10000个访客分配给10个客服，每个客服平均分配50个访客
    // public AgentEntity nextAgent() {

        // TODO: 所有客服都离线或小休不接待状态，则进入留言

        // TODO:  所有客服都达到最大接待人数，则进入排队

        // TODO: 排队人数动态变化，随时通知访客端。数据库记录排队人数变动时间点

        // TODO: 首先完善各个客服的统计数据，比如接待量、等待时长等

    //     if (routingMode.equals(WorkgroupRoutingModeEnum.ROUND_ROBIN.name())) {
    //         // return assignAgentByRobin();

    //     } else if (routingMode.equals(WorkgroupRoutingModeEnum.LEAST_ACTIVE.name())) {

    //     }

    //     return getAgents().iterator().next();
    // }

    /**
     * 路由队列，用于分配客服
     */
    // @Transient
    // @Builder.Default
    // private Queue<AgentEntity> agentQueue = new LinkedList<>();

    /**
     * 轮询分配算法实现访客到客服的分配
     * @return 分配到的客服
     */
    // public AgentEntity assignAgentByRobin() {
    //     if (agentQueue.isEmpty()) {
    //         Iterator<AgentEntity> iterator = agents.iterator();
    //         while (iterator.hasNext()) {
    //             AgentEntity agent = iterator.next();
    //             if (agent.isConnected() && agent.isAvailable()) {
    //                 agentQueue.add(agent);
    //             }
    //         }
    //     }
    //     // 从队列头部获取一个客服
    //     AgentEntity assignedAgent = agentQueue.poll();
    //     // 为了保证轮询的连续性，将该客服重新加入队列尾部
    //     agentQueue.offer(assignedAgent);
    //     // 返回分配到的客服
    //     return assignedAgent;
    // }

    public boolean isConnected() {
        if (this.agents == null || this.agents.isEmpty()) {
            return false;
        }
        return this.agents.stream().anyMatch(agent -> agent.isConnected());
    }

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
    public long getConnectedAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isConnected()).count();
    }

    // agent available count
    public long getAvailableAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isAvailable()).count();
    }

    // agent offline count
    public long getOfflineAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isOffline()).count();
    }

    // agent busy count
    public long getBusyAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isBusy()).count();
    }
    
    // agent away count
    public long getAwayAgentCount() {
        if (this.agents == null || this.agents.isEmpty()) {
            return 0;
        }
        return this.agents.stream().filter(agent -> agent.isAway()).count();
    }
    
}
