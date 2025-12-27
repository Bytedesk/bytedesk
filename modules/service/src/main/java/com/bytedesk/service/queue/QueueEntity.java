/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-28 10:08:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.queue_member.QueueMemberEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 队列实体类：
 * 三个维度：
 * 1. 客服账号，某个客服账号当天接待服务多少人
 * 2. 工作组，某个工作组当天接待服务多少人
 * 3. 机器人，某个机器人当天接待服务多少人
 * 
 * @author jackning
 * @date 2024-02-22
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({ "selectedMembers" })
@EntityListeners({ QueueEntityListener.class })
@Table(name = "bytedesk_service_queue",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_queue_topic_day", columnNames = { "queue_topic", "queue_day" })
        })
public class QueueEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 队列名称
    @Column(name = "queue_nickname")
    private String nickname;

    // 队列类型
    @Builder.Default
    @Column(name = "queue_type")
    private String type = ThreadTypeEnum.WORKGROUP.name();

    // 区别于thread topic，此处的topic是队列的主题，用于访客监听排队人数变化
    @Column(name = "queue_topic")
    private String topic;

    // 队列日期(YYYY-MM-DD)
    @Column(name = "queue_day")
    private String day;

    // 队列状态
    @Builder.Default
    @Column(name = "queue_status")
    private String status = QueueStatusEnum.ACTIVE.name(); // 队列状态

    // 仅用于在工作组情况下，记录存储robot/agent接待来自工作组数量
    // 添加新的一对多关系 - 作为工作组队列
    @OneToMany(mappedBy = "workgroupQueue", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("workgroupQueueMembers")
    @Builder.Default
    private List<QueueMemberEntity> workgroupQueueMembers = new ArrayList<>();

    @OneToMany(mappedBy = "agentQueue", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("agentQueueMembers")
    @Builder.Default
    private List<QueueMemberEntity> agentQueueMembers = new ArrayList<>();

    @OneToMany(mappedBy = "robotQueue", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("robotQueueMembers")
    @Builder.Default
    private List<QueueMemberEntity> robotQueueMembers = new ArrayList<>();

    // 将queueMembers中的thread, 按照thread.created按照24小时分组，统计每个分组的数量？
    @JsonIgnore
    public List<Integer> getThreadsCountByHour() {
        List<Integer> threadsCountByHour = new ArrayList<>();
        List<QueueMemberEntity> members = getSelectedMembers();
        for (int i = 0; i < 24; i++) {
            threadsCountByHour.add(countThreadsCreatedInHour(members, i));
        }
        return threadsCountByHour;
    }

    private int countThreadsCreatedInHour(List<QueueMemberEntity> members, int hour) {
        if (members == null) {
            return 0;
        }
        // Align every timestamp to the display timezone before extracting the hour bucket
        return (int) members.stream()
                .filter(member -> member.getThread() != null && member.getThread().getCreatedAt() != null)
                .filter(member -> {
                    ZonedDateTime displayTime = member.getThread().getCreatedAt()
                            .withZoneSameInstant(BdDateUtils.getDisplayZoneId());
                    return displayTime.getHour() == hour;
                })
                .count();
    }

    /**
     * 获取当天请求服务总人数（当前分配的排队号码）
     */
    public int getTotalCount() {
        return getSelectedMembers().size();
    }

    /**
     * 获取请求时客服离线的人数（包括当前离线和曾经离线但已关闭的）
     */
    public int getOfflineCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> (member.getThread() != null && member.getAgentOffline()))
                .count();
    }

    // messageLeaveCount
    public int getMessageLeaveCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> (member.getMessageLeave()))
                .count();
    }

    // robotToAgentCount
    public int getRobotToAgentCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> member.getRobotToAgent())
                .count();
    }

    // getRobotingCount
    public int getRobotingCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> member.isRoboting())
                .count();
    }

    /**
     * 获取当前排队中的人数
     */
    public int getQueuingCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> member.isQueuing())
                .count();
    }

    /**
     * 获取当前正在会话的人数
     */
    public int getChattingCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> member.isChatting())
                .count();
    }

    /**
     * 获取已有客服响应的人数（至少发送过一条客服消息）
     */
    public int getAgentServedCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> member.getAgentMessageCount() != null && member.getAgentMessageCount() > 0)
                .count();
    }

    /**
     * 获取已结束会话的人数
     */
    public int getClosedCount() {
        return (int) getSelectedMembers().stream()
                .filter(member -> member.isClosed())
                .count();
    }

    /**
     * 获取平均等待时间(秒)
     */
    public int getAvgWaitTime() {
        List<QueueMemberEntity> servedMembers = getSelectedMembers().stream()
                .filter(member -> member.getAgentAcceptedAt() != null)
                .toList();

        if (servedMembers.isEmpty()) {
            return 0;
        }

        long totalWaitTime = servedMembers.stream()
                .mapToLong(QueueMemberEntity::getWaitLength)
                .sum();

        return (int) (totalWaitTime / servedMembers.size());
    }

    /**
     * 获取平均解决时间(秒)
     */
    public int getAvgResolveTime() {
        List<QueueMemberEntity> closedMembers = getSelectedMembers().stream()
                .filter(member -> member.isClosed())
                .filter(member -> member.getAgentAcceptedAt() != null
                        && member.getAgentClosedAt() != null)
                .toList();

        if (closedMembers.isEmpty()) {
            return 0;
        }

        long totalResolveTime = closedMembers.stream()
                .mapToLong(member -> java.time.Duration.between(
                        member.getAgentAcceptedAt(),
                        member.getAgentClosedAt())
                        .getSeconds())
                .sum();

        return (int) (totalResolveTime / closedMembers.size());
    }

    private List<QueueMemberEntity> getSelectedMembers() {
        if (ThreadTypeEnum.AGENT.name().equals(type)) {
            return agentQueueMembers != null ? agentQueueMembers : java.util.Collections.emptyList();
        }
        if (ThreadTypeEnum.ROBOT.name().equals(type)) {
            return robotQueueMembers != null ? robotQueueMembers : java.util.Collections.emptyList();
        }
        if (ThreadTypeEnum.WORKGROUP.name().equals(type)) {
            return workgroupQueueMembers != null ? workgroupQueueMembers : java.util.Collections.emptyList();
        }
        return mergeAllMembers();
    }

    private List<QueueMemberEntity> mergeAllMembers() {
        if ((workgroupQueueMembers == null || workgroupQueueMembers.isEmpty())
                && (agentQueueMembers == null || agentQueueMembers.isEmpty())
                && (robotQueueMembers == null || robotQueueMembers.isEmpty())) {
            return java.util.Collections.emptyList();
        }

        List<QueueMemberEntity> mergedMembers = new ArrayList<>();
        if (workgroupQueueMembers != null) {
            mergedMembers.addAll(workgroupQueueMembers);
        }
        if (agentQueueMembers != null) {
            mergedMembers.addAll(agentQueueMembers);
        }
        if (robotQueueMembers != null) {
            mergedMembers.addAll(robotQueueMembers);
        }
        return mergedMembers;
    }

    /**
     * 获取下一个排队号码
     */
    public int getNextNumber() {
        return getTotalCount() + 1;
    }

    /**
     * 检查是否可以加入队列
     */
    public Boolean canEnqueue() {
        return status.equals(QueueStatusEnum.ACTIVE.name());
    }

}