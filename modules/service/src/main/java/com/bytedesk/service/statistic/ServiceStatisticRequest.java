/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 17:09:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 16:47:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStatisticRequest extends BaseRequest {
    
    // 工作组统计
    private String workgroupUid;            // 工作组ID

    // 处理人统计
    private String agentUid;             // 处理人ID

    // 机器人统计
    private String robotUid;             // 机器人ID
    
    // 时间范围
    private String statisticStartTime;    // 统计开始时间
    
    private String statisticEndTime;      // 统计结束时间

     // 日期，每个orgUid，每个日期一个统计
     private String date;

}
