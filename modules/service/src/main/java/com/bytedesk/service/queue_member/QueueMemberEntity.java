/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:23:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 15:21:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

    @Column(name = "queue_uid", nullable = false)
    private String queueUid;  // 关联队列

    @Column(name = "thread_topic", nullable = false)
    private String threadTopic;  // 关联会话

    @Column(name = "visitor_uid", nullable = false)
    private String visitorUid;  // 访客ID

    @Builder.Default
    @Column(name = "queue_number")
    private int queueNumber = 1;  // 排队号码

    @Builder.Default
    @Column(nullable = false)
    private String status = QueueMemberStatusEnum.WAITING.name();  // 成员状态

    @Column(name = "join_time", nullable = false)
    private LocalDateTime joinTime;  // 加入时间

    @Column(name = "start_time")
    private LocalDateTime startTime;  // 开始服务时间

    @Column(name = "first_response_time")
    private LocalDateTime firstResponseTime;  // 首次响应时间

    @Column(name = "first_response")
    private boolean firstResponse;  // 是否首次响应

    @Column(name = "close_time")
    private LocalDateTime closeTime;  // 结束时间

    @Column(name = "agent_uid")
    private String agentUid;  // 服务客服

    @Builder.Default
    @Column(name = "priority")
    private int priority = 0;  // 优先级(0-100)

    /**
     * 计算等待时间(秒)
     */
    public long getWaitTime() {
        if (joinTime == null) return 0;
        LocalDateTime endWaitTime = startTime != null ? startTime : LocalDateTime.now();
        return Duration.between(joinTime, endWaitTime).getSeconds();
    }

    /**
     * 更新状态
     */
    public void updateStatus(String newStatus, String agentUid) {
        this.status = newStatus;
        this.agentUid = agentUid;
        
        if (QueueMemberStatusEnum.PROCESSING.name().equals(newStatus)) {
            this.startTime = LocalDateTime.now();
        } else if (QueueMemberStatusEnum.valueOf(newStatus).isEndStatus()) {
            this.closeTime = LocalDateTime.now();
        }
    }
}
