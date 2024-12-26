/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 11:06:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

@Entity
@Data
@Builder
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

    // 队列基本信息
    @Builder.Default
    private int currentNumber = 0;  // 当前排队号码

    @Builder.Default
    private int waitingNumber = 0;  // 等待人数

    @Builder.Default
    private int servingNumber = 0;  // 正在服务人数

    @Builder.Default
    private int servedNumber = 0;  // 已完成人数

    @Builder.Default
    private int avgWaitTime = 0;  // 平均等待时间(秒)

    // 队列状态
    @Builder.Default
    @Column(name = "queue_status", nullable = false)
    private String status = QueueStatusEnum.ACTIVE.name();  // 队列状态

    // 队列日期(YYYY-MM-DD)
    @Column(name = "queue_day")
    private String day;  

    // agentUid or workgroupUid
    @Column(name = "queue_topic")
    private String topic;

    @Builder.Default
    @Column(name = "queue_type")
    private String type = ThreadTypeEnum.WORKGROUP.name();  // 队列类型，AGENT或WORKGROUP

    /**
     * 获取下一个排队号码
     */
    public int getNextNumber() {
        return ++currentNumber;
    }

    /**
     * 减少等待人数
     */
    public void decreaseWaitingNumber() {
        this.waitingNumber--;
    }

    /**
     * 增加等待人数
     */
    public void increaseWaitingNumber() {
        this.waitingNumber++;
    }

    /**
     * 增加正在服务人数
     */
    public void increaseServingNumber() {
        this.servingNumber++;
    }

    /**
     * 减少正在服务人数
     */
    public void decreaseServingNumber() {
        this.servingNumber--;
    }

    /**
     * 更新队列统计
     */
    public void updateStats(int waiting, int serving, int served, int avgWait) {
        this.waitingNumber = waiting;
        this.servingNumber = serving;
        this.servedNumber = served;
        this.avgWaitTime = avgWait;
    }

    /**
     * 检查是否可以加入队列
     */
    public boolean canEnqueue() {
        return status.equals(QueueStatusEnum.ACTIVE.name());
            // && waitingNumber < maxWaiting;
    }

    public void acceptThread() {
        this.servingNumber++;
        this.waitingNumber--;
        // TODO: 计算平均等待时间
        this.avgWaitTime = (this.avgWaitTime * this.servedNumber + this.currentNumber) / (this.servedNumber + 1);
    }
}
