/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:14:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 11:21:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能使用统计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrayReleaseFeatureStatistics {
    
    private String feature;       // 功能代码
    private long totalUsage;      // 总使用次数
    private long successCount;    // 成功次数
    private long uniqueUsers;     // 独立用户数
    private double successRate;   // 成功率

    // 计算失败次数
    public long getFailureCount() {
        return totalUsage - successCount;
    }

    // 计算失败率
    public double getFailureRate() {
        return 1.0 - successRate;
    }

    // 判断是否达到稳定状态（可以继续放量）
    public Boolean isStable() {
        return totalUsage >= 100 &amp;amp;& successRate >= 0.95;
    }
} 