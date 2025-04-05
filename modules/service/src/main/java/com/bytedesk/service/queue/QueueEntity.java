/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 18:05:39
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

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

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

    @Builder.Default
    private int newCount = 0;  // 今日请求服务人数，当前排队号码

    @Builder.Default
    private int offlineCount = 0;  // 请求时，客服离线或非接待状态的请求人次

    @Builder.Default
    private int queuingCount = 0;  // 排队中人数

    @Builder.Default
    private int chattingCount = 0;  // 正在服务人数   

    @Builder.Default
    private int closedCount = 0;  // 对话结束人数

    @Builder.Default
    private int avgWaitTime = 0;  // 平均等待时间(秒)

    @Builder.Default
    private int avgResolveTime = 0;  // 平均解决时间(秒)

    /**
     * 获取下一个排队号码
     */
    public int getNextNumber() {
        return ++newCount;
    }

    /**
     * 减少等待人数
     */
    public void decreaseWaitingNumber() {
        this.queuingCount--;
    }

    /**
     * 增加等待人数
     */
    public void increaseWaitingNumber() {
        this.queuingCount++;
    }

    /**
     * 增加正在服务人数
     */
    public void increaseServingNumber() {
        this.chattingCount++;
    }

    /**
     * 减少正在服务人数
     */
    public void decreaseServingNumber() {
        this.chattingCount--;
    }

    /**
     * 增加已完成人数
     */
    public void increaseServedNumber() {
        this.closedCount++;
    }

    /**
     * 更新队列统计
     */
    public void updateStats(int waiting, int serving, int served, int avgWait) {
        this.queuingCount = waiting;
        this.chattingCount = serving;
        this.closedCount = served;
        this.avgWaitTime = avgWait;
    }

    /**
     * 检查是否可以加入队列
     * TODO: 需要根据队列的最大等待人数来判断
     * TODO: 增加节假日判断
     * @return true: 可以加入队列; false: 不可以加入队列
     */
    public boolean canEnqueue() {
        return status.equals(QueueStatusEnum.ACTIVE.name());
            // && waitingNumber < maxWaiting;
    }

    public void acceptThread() {
        this.chattingCount++;
        this.queuingCount--;
        // TODO: 计算平均等待时间
        this.avgWaitTime = (this.avgWaitTime * this.closedCount + this.newCount) / (this.closedCount + 1);
    }
}
