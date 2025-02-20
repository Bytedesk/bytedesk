/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 14:14:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import com.bytedesk.core.base.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TicketStatisticRequest extends BaseRequest {
    
    // 工作组统计
    private String workgroupUid;            // 工作组ID

    // 处理人统计
    private String assigneeUid;             // 处理人ID

    // 时间范围
    private String statisticStartTime;    // 统计开始时间
    
    private String statisticEndTime;      // 统计结束时间

     // 日期，每个orgUid，每个日期一个统计
     private String date;
} 