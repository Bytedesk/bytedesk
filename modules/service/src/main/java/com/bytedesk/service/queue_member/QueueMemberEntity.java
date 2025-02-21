/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:23:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 14:53:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.LocalDateTime;
import java.time.Duration;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Jackning <270580156@qq.com>
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ QueueMemberEntityListener.class })
@Table(name = "bytedesk_service_queue_member")
public class QueueMemberEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String queueUid;  // 关联队列

    private String queueNickname;  // 队列名称

    @Column(nullable = false)
    private String queueTopic;  // 队列主题，用于查询

    @Column(nullable = false)
    private String queueDay;  // 队列日期，用于查询

    @Column(nullable = false)
    private String threadUid;  // 关联会话

    @Column(nullable = false)
    private String visitorUid;  // 访客ID

    private String visitorNickname;  // 访客昵称

    private String visitorAvatar;  // 访客头像

    private String agentUid;  // 服务客服ID

    private String agentNickname;  // 客服昵称

    private String agentAvatar;  // 客服头像

    private String workgroupUid;  // 工作组ID

    private String workgroupName;  // 工作组名称

    @Builder.Default
    private int beforeNumber = 0;  // 前面排队人数

    @Builder.Default
    private int waitTime = 0;  // 等待时间(秒)

    @Builder.Default
    private int queueNumber = 0;  // 排队号码

    @Builder.Default
    @Column(nullable = false)
    private String status = QueueMemberStatusEnum.WAITING.name();  // 成员状态

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime enqueueTime = LocalDateTime.now();  // 加入时间

    private String acceptType ;  // 接单方式, 自动、手动，不设置默认

    private LocalDateTime acceptTime;  // 开始服务时间

    private LocalDateTime firstResponseTime;  // 首次人工响应时间

    @Builder.Default
    private boolean firstResponse = false;  // 是否首次响应

    @Builder.Default
    private int avgResponseTime = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private int maxResponseTime = 0;  // 最长响应时间(秒)

    @Builder.Default
    private int agentMessageCount = 0;  // 客服消息数量

    @Builder.Default
    private int visitorMessageCount = 0;  // 访客消息数量

    @Builder.Default
    @Column(name = "is_timeout")
    private boolean timeout = false; // 是否超时

    private LocalDateTime lastResponseTime;  // 最后响应时间

    private LocalDateTime leaveTime;  // 离开时间

    private LocalDateTime closeTime;  // 结束时间

    @Builder.Default
    private int priority = 0;  // 优先级(0-100)

    // 是否已解决
    @Builder.Default
    @Column(name = "is_solved")
    private boolean solved = false;

    // 是否已评价
    @Builder.Default
    @Column(name = "is_rated")
    private boolean rated = false;

    private String client;  // 客户端类型

    private int hour;  // 小时

    private String date;  // 日期

    /**
     * 计算等待时间(秒)
     */
    public long getWaitTime() {
        if (enqueueTime == null) return 0;
        LocalDateTime endWaitTime = acceptTime != null ? acceptTime : LocalDateTime.now();
        return Duration.between(enqueueTime, endWaitTime).getSeconds();
    }

    /**
     * 更新状态
     */
    public void updateStatus(String newStatus, String agentUid) {
        this.status = newStatus;
        this.agentUid = agentUid;
        
        if (QueueMemberStatusEnum.SERVING.name().equals(newStatus)) {
            this.acceptTime = LocalDateTime.now();
        } else if (QueueMemberStatusEnum.valueOf(newStatus).isEndStatus()) {
            this.closeTime = LocalDateTime.now();
        }
    }

    public void acceptThread() {
        this.status = QueueMemberStatusEnum.SERVING.name();
        this.acceptType = QueueMemberAcceptTypeEnum.MANUAL.name();
        this.acceptTime = LocalDateTime.now();
        // 计算等待时间
        this.waitTime = (int) Duration.between(enqueueTime, acceptTime).getSeconds();
    }
}
