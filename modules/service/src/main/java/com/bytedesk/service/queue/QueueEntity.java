/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 21:06:09
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
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.service.queue_member.QueueMemberEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
@EntityListeners({ QueueEntityListener.class })
@Table(name = "bytedesk_service_queue", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "queue_topic", "queue_day",
                                "is_deleted" }, name = "uk_queue_topic_day_deleted")
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
        @Column(name = "queue_status", nullable = false)
        private String status = QueueStatusEnum.ACTIVE.name(); // 队列状态

        // 仅用于在工作组情况下，记录存储robot/agent接待来自技能组数量
        // 添加新的一对多关系 - 作为工作组队列
        @OneToMany(mappedBy = "workgroupQueue", cascade = CascadeType.ALL, orphanRemoval = false)
        @Builder.Default
        private List<QueueMemberEntity> workgroupQueueMembers = new ArrayList<>();

        @OneToMany(mappedBy = "agentQueue", cascade = CascadeType.ALL, orphanRemoval = false)
        @Builder.Default
        private List<QueueMemberEntity> agentQueueMembers = new ArrayList<>();

        @OneToMany(mappedBy = "robotQueue", cascade = CascadeType.ALL, orphanRemoval = false)
        @Builder.Default
        private List<QueueMemberEntity> robotQueueMembers = new ArrayList<>();

        // 将queueMembers中的thread, 按照thread.created按照24小时分组，统计每个分组的数量？
        public List<Integer> getThreadsCountByHour() {
                List<Integer> threadsCountByHour = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                        // 读取workgroupQueueMembers.thread.created 按照24小时分组，统计每个分组的数量
                        final int hour = i;
                        int count = (int) workgroupQueueMembers.stream().filter(member -> member.getThread() != null
                                        && member.getThread().getCreatedAt().getHour() == hour).count();
                        // 读取agentQueueMembers.thread.created 按照24小时分组，统计每个分组的数量
                        count += (int) agentQueueMembers.stream().filter(member -> member.getThread() != null
                                        && member.getThread().getCreatedAt().getHour() == hour).count();
                        // 读取robotQueueMembers.thread.created 按照24小时分组，统计每个分组的数量
                        count += (int) robotQueueMembers.stream().filter(member -> member.getThread() != null
                                        && member.getThread().getCreatedAt().getHour() == hour).count();
                        threadsCountByHour.add(count);
                }
                return threadsCountByHour;
        }

        /**
         * 获取当天请求服务总人数（当前分配的排队号码）
         */
        public int getTotalCount() {
                return agentQueueMembers.size() + robotQueueMembers.size() + workgroupQueueMembers.size();
        }

        /**
         * 获取请求时客服离线的人数（包括当前离线和曾经离线但已关闭的）
         */
        public int getOfflineCount() {
                int count1 = (int) agentQueueMembers.stream()
                                .filter(member -> (member.getThread() != null && member.isAgentOffline()))
                                .count();

                int count2 = (int) robotQueueMembers.stream()
                                .filter(member -> (member.getThread() != null && member.isAgentOffline()))
                                .count();

                int count3 = (int) workgroupQueueMembers.stream()
                                .filter(member -> (member.getThread() != null && member.isAgentOffline()))
                                .count();

                return count1 + count2 + count3;
        }

        // leaveMsgCount
        public int getMessageLeaveCount() {
                int count1 = (int) agentQueueMembers.stream()
                                .filter(member -> (member.isMessageLeave()))
                                .count();

                int count2 = (int) robotQueueMembers.stream()
                                .filter(member -> (member.isMessageLeave()))
                                .count();

                int count3 = (int) workgroupQueueMembers.stream()
                                .filter(member -> (member.isMessageLeave()))
                                .count();

                return count1 + count2 + count3;
        }

        // robotToAgentCount
        public int getRobotToAgentCount() {
                int count1 = (int) agentQueueMembers.stream()
                                .filter(member -> member.isRobotToAgent())
                                .count();

                int count2 = (int) robotQueueMembers.stream()
                                .filter(member -> member.isRobotToAgent())
                                .count();

                int count3 = (int) workgroupQueueMembers.stream()
                                .filter(member -> member.isRobotToAgent())
                                .count();

                return count1 + count2 + count3;
        }

        // getRobotingCount
        public int getRobotingCount() {
                int count1 = (int) agentQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isRoboting())
                                .count();

                int count2 = (int) robotQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isRoboting())
                                .count();

                int count3 = (int) workgroupQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isRoboting())
                                .count();

                return count1 + count2 + count3;
        }

        /**
         * 获取当前排队中的人数
         */
        public int getQueuingCount() {
                int count1 = (int) agentQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isQueuing())
                                .count();

                int count2 = (int) robotQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isQueuing())
                                .count();

                int count3 = (int) workgroupQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isQueuing())
                                .count();

                return count1 + count2 + count3;
        }

        /**
         * 获取当前正在会话的人数
         */
        public int getChattingCount() {
                int count1 = (int) agentQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isChatting())
                                .count();

                int count2 = (int) robotQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isChatting())
                                .count();

                int count3 = (int) workgroupQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isChatting())
                                .count();

                return count1 + count2 + count3;
        }

        /**
         * 获取已结束会话的人数
         */
        public int getClosedCount() {
                int count1 = (int) agentQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                                .count();

                int count2 = (int) robotQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                                .count();

                int count3 = (int) workgroupQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                                .count();

                return count1 + count2 + count3;
        }

        /**
         * 获取平均等待时间(秒)
         */
        public int getAvgWaitTime() {
                List<QueueMemberEntity> servedMembers1 = agentQueueMembers.stream()
                                .filter(member -> member.getAgentAcceptedAt() != null)
                                .toList();

                List<QueueMemberEntity> servedMembers2 = robotQueueMembers.stream()
                                .filter(member -> member.getAgentAcceptedAt() != null)
                                .toList();

                List<QueueMemberEntity> servedMembers3 = workgroupQueueMembers.stream()
                                .filter(member -> member.getAgentAcceptedAt() != null)
                                .toList();

                int totalCount = servedMembers1.size() + servedMembers2.size() + servedMembers3.size();

                if (totalCount == 0) {
                        return 0;
                }

                long totalWaitTime1 = servedMembers1.stream()
                                .mapToLong(QueueMemberEntity::getWaitTime)
                                .sum();

                long totalWaitTime2 = servedMembers2.stream()
                                .mapToLong(QueueMemberEntity::getWaitTime)
                                .sum();

                long totalWaitTime3 = servedMembers3.stream()
                                .mapToLong(QueueMemberEntity::getWaitTime)
                                .sum();

                return (int) ((totalWaitTime1 + totalWaitTime2 + totalWaitTime3) / totalCount);
        }

        /**
         * 获取平均解决时间(秒)
         */
        public int getAvgResolveTime() {
                List<QueueMemberEntity> closedMembers1 = agentQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                                .filter(member -> member.getAgentAcceptedAt() != null
                                                && member.getAgentClosedAt() != null)
                                .toList();

                List<QueueMemberEntity> closedMembers2 = robotQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                                .filter(member -> member.getAgentAcceptedAt() != null
                                                && member.getAgentClosedAt() != null)
                                .toList();

                List<QueueMemberEntity> closedMembers3 = workgroupQueueMembers.stream()
                                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                                .filter(member -> member.getAgentAcceptedAt() != null
                                                && member.getAgentClosedAt() != null)
                                .toList();

                int totalCount = closedMembers1.size() + closedMembers2.size() + closedMembers3.size();

                if (totalCount == 0) {
                        return 0;
                }

                long totalResolveTime1 = closedMembers1.stream()
                                .mapToLong(member -> {
                                        return java.time.Duration.between(
                                                        member.getAgentAcceptedAt(),
                                                        member.getAgentClosedAt())
                                                        .getSeconds();
                                })
                                .sum();

                long totalResolveTime2 = closedMembers2.stream()
                                .mapToLong(member -> {
                                        return java.time.Duration.between(
                                                        member.getAgentAcceptedAt(),
                                                        member.getAgentClosedAt())
                                                        .getSeconds();
                                })
                                .sum();

                long totalResolveTime3 = closedMembers3.stream()
                                .mapToLong(member -> {
                                        return java.time.Duration.between(
                                                        member.getAgentAcceptedAt(),
                                                        member.getAgentClosedAt())
                                                        .getSeconds();
                                })
                                .sum();

                return (int) ((totalResolveTime1 + totalResolveTime2 + totalResolveTime3) / totalCount);
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
        public boolean canEnqueue() {
                return status.equals(QueueStatusEnum.ACTIVE.name());
        }

        /**
         * 获取直接分配的会话数量
         */
        // public int getDirectAssignCount() {
        // int count1 = (int) queueMembers.stream()
        // .filter(member ->
        // QueueMemberSourceEnum.DIRECT.name().equals(member.getSourceType()))
        // .count();

        // int count2 = (int) workgroupQueueMembers.stream()
        // .filter(member ->
        // QueueMemberSourceEnum.DIRECT.name().equals(member.getSourceType()))
        // .count();

        // return count1 + count2;
        // }

        /**
         * 获取从工作组分配的会话数量
         */
        // public int getWorkgroupAssignCount() {
        // int count1 = (int) queueMembers.stream()
        // .filter(member ->
        // QueueMemberSourceEnum.WORKGROUP.name().equals(member.getSourceType()))
        // .count();

        // int count2 = (int) workgroupQueueMembers.stream()
        // .filter(member ->
        // QueueMemberSourceEnum.WORKGROUP.name().equals(member.getSourceType()))
        // .count();

        // return count1 + count2;
        // }
}