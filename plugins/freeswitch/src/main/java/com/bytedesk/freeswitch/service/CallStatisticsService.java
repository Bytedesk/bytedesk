/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 15:15:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 15:15:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.service;

import com.bytedesk.freeswitch.model.CallStatistics;

/**
 * 呼叫统计服务接口
 */
public interface CallStatisticsService {
    
    /**
     * 获取当前呼叫统计数据
     * 
     * @return 呼叫统计数据
     */
    CallStatistics getCallStatistics();
    
    /**
     * 根据时间周期获取呼叫统计数据
     * 
     * @param period 时间周期（today, week, month, year）
     * @return 呼叫统计数据
     */
    CallStatistics getCallStatisticsByPeriod(String period);
}
