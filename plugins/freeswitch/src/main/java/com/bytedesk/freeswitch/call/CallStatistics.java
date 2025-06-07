/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 15:10:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 15:10:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.call;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 呼叫统计数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallStatistics {
    
    /**
     * 今日呼叫总数
     */
    private int todayCallCount;
    
    /**
     * 平均通话时长（分钟）
     */
    private double averageCallDuration;
    
    /**
     * 在线座席数量
     */
    private int onlineAgentsCount;
    
    /**
     * 当前活跃呼叫数量
     */
    private int activeCallsCount;
    
    /**
     * 呼入量
     */
    private int inboundCallsCount;
    
    /**
     * 呼出量
     */
    private int outboundCallsCount;
    
    /**
     * 已接通呼叫数量
     */
    private int answeredCallsCount;
    
    /**
     * 未接通呼叫数量
     */
    private int missedCallsCount;
    
    /**
     * 接通率百分比
     */
    private double answerRate;
}
