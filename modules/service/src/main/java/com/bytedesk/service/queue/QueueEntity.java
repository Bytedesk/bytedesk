/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 11:33:41
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
@Table(name = "bytedesk_service_queue")
public class QueueEntity extends BaseEntity {

    // 队列基本信息
    @Builder.Default
    private int currentNumber = 0;  // 当前排队号码

    @Builder.Default
    private int waitingNumber = 0;  // 等待人数

    @Builder.Default
    private int servingNumber = 0;  // 正在服务人数

    @Builder.Default
    private int finishedNumber = 0;  // 已完成人数

    @Builder.Default
    private int avgWaitTime = 0;  // 平均等待时间(秒)

    // 队列配置
    @Builder.Default
    private int maxWaiting = 10000;  // 最大等待人数

    @Builder.Default
    private int maxWaitTime = 24 * 60 * 60;  // 最大等待时间(秒)

    // 队列状态
    @Builder.Default
    @Column(nullable = false)
    private String status = QueueStatusEnum.ACTIVE.name();  // 队列状态

    @Column(name = "queue_day")
    private String day;  // 队列日期(YYYY-MM-DD)

    // agentUid or workgroupUid
    private String queueTopic;

    @Builder.Default
    private String queueType = ThreadTypeEnum.WORKGROUP.name();  // 队列类型，AGENT或WORKGROUP

    /**
     * 获取下一个排队号码
     */
    public int getNextNumber() {
        return ++currentNumber;
    }

    /**
     * 更新队列统计
     */
    public void updateStats(int waiting, int serving, int finished, int avgWait) {
        this.waitingNumber = waiting;
        this.servingNumber = serving;
        this.finishedNumber = finished;
        this.avgWaitTime = avgWait;
    }

    /**
     * 检查是否可以加入队列
     */
    public boolean canEnqueue() {
        return status.equals(QueueStatusEnum.ACTIVE.name())
            && waitingNumber < maxWaiting;
    }
}
