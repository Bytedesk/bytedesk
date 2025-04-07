/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-07 09:53:23
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
import com.bytedesk.service.queue_member.QueueMemberSourceEnum;

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
@Table(name = "bytedesk_service_queue", 
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"queue_topic", "queue_day", "is_deleted"},
            name = "uk_queue_topic_day_deleted"
        )
    }
)
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
    private String status = QueueStatusEnum.ACTIVE.name();  // 队列状态

    // 添加与QueueMember的一对多关系
    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QueueMemberEntity> queueMembers = new ArrayList<>();

    /**
     * 获取当天请求服务总人数（当前分配的排队号码）
     */
    public int getNewCount() {
        return queueMembers.size();
    }

    /**
     * 获取请求时客服离线的人数（包括当前离线和曾经离线但已关闭的）
     */
    public int getOfflineCount() {
        return (int) queueMembers.stream()
                .filter(member -> 
                       (member.getThread() != null && member.getThread().isOffline()))
                .count();
    }

    /**
     * 获取当前排队中的人数
     */
    public int getQueuingCount() {
        return (int) queueMembers.stream()
                .filter(member -> member.getThread() != null && member.getThread().isQueuing())
                .count();
    }

    /**
     * 获取当前正在会话的人数
     */
    public int getChattingCount() {
        return (int) queueMembers.stream()
                .filter(member -> member.getThread() != null && member.getThread().isChatting())
                .count();
    }

    /**
     * 获取已结束会话的人数
     */
    public int getClosedCount() {
        return (int) queueMembers.stream()
                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                .count();
    }

    /**
     * 获取平均等待时间(秒)
     */
    public int getAvgWaitTime() {
        List<QueueMemberEntity> servedMembers = queueMembers.stream()
                .filter(member -> member.getAcceptTime() != null)
                .toList();
        
        if (servedMembers.isEmpty()) {
            return 0;
        }
        
        long totalWaitTime = servedMembers.stream()
                .mapToLong(QueueMemberEntity::getWaitTime)
                .sum();
        
        return (int) (totalWaitTime / servedMembers.size());
    }

    /**
     * 获取平均解决时间(秒)
     */
    public int getAvgResolveTime() {
        List<QueueMemberEntity> closedMembers = queueMembers.stream()
                .filter(member -> member.getThread() != null && member.getThread().isClosed())
                .filter(member -> member.getAcceptTime() != null && member.getCloseTime() != null)
                .toList();
        
        if (closedMembers.isEmpty()) {
            return 0;
        }
        
        long totalResolveTime = closedMembers.stream()
                .mapToLong(member -> {
                    return java.time.Duration.between(
                            member.getAcceptTime(), 
                            member.getCloseTime())
                            .getSeconds();
                })
                .sum();
        
        return (int) (totalResolveTime / closedMembers.size());
    }

    /**
     * 获取下一个排队号码
     */
    public int getNextNumber() {
        return getNewCount() + 1;
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
    public int getDirectAssignCount() {
        return (int) queueMembers.stream()
                .filter(member -> QueueMemberSourceEnum.DIRECT.name().equals(member.getSourceType()))
                .count();
    }

    /**
     * 获取从工作组分配的会话数量
     */
    public int getWorkgroupAssignCount() {
        return (int) queueMembers.stream()
                .filter(member -> QueueMemberSourceEnum.WORKGROUP.name().equals(member.getSourceType()))
                .count();
    }
}