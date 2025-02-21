/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 08:59:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 13:27:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.statistic;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

// 知识库统计数据
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kbase_statistic", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"kbaseUid", "date"})
})
public class StatisticKbaseEntity extends BaseEntity {
    
    private String kbaseUid;

    private String date;
}
