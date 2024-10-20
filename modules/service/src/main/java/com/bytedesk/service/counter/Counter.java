/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:23:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-18 15:27:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.counter;

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
 * 访问计数器
 * 按照技能组编号，技能组之间的号码无关联
 * 取号：每进入一个访客都需要取号
 * @Author: jackning 270580156@qq.com
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ CounterEntityListener.class })
@Table(name = "service_counter")
public class Counter extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    private String topic;
    
    // 当天该客服/技能组/部门/诊室/医生等计数器编号
    // 每日定时清零，TODO: 或手动清零
    @Builder.Default
    private int currentNumber = 1;

    // 前面等待人数
    @Builder.Default
    private int waitingNumber = 0;

    // 大概仍然需要等待时间
    @Builder.Default
    private int waitSeconds = 0;
    
    // TODO: 服务中人数
    // @Builder.Default
    // private int servingNumber = 0;

    // TODO: 已完成服务人数
    // @Builder.Default
    // private int finishedNumber = 0;

    // 计数器编号增加函数，使其能够链式调用
    public Counter increaseSerialNumber() {
        this.currentNumber++;
        return this;
    }
}
