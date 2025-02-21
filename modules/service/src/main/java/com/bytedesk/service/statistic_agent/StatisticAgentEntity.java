/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 08:55:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:24:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_agent;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.service.statistic.StatisticIndex;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 某个客服的会话统计数据
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_statistic_agent", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"agentUid", "date"})
})
public class StatisticAgentEntity extends BaseEntity {

    @Embedded
    private StatisticIndex statisticIndex;

    ///////////////////////////////////////////////////////////

    private String agentUid;

    // 统计时间，按天统计
    private String date;

    //////////////////////////////////////////////////////////

    // public Boolean canAcceptMore() {
    // return this.currentThreadCount < this.maxThreadCount;
    // }
}
