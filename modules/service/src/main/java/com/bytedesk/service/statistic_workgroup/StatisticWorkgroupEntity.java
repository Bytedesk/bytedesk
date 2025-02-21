/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 08:57:19
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-21 13:08:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_workgroup;

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
 * 某个技能组的统计数据
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_statistic_workgroup", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"workgroupUid", "date"})
})
public class StatisticWorkgroupEntity extends BaseEntity {

    @Embedded
    private StatisticIndex statisticIndex;


    private String workgroupUid;

    // 统计时间，按天统计
    private String date;

    //////////////////////////////////////////////////////////

}
