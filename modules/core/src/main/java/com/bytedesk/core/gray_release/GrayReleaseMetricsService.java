/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:10:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 11:13:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;
@Service
@RequiredArgsConstructor
public class GrayReleaseMetricsService {
    
    private final MetricsRepository metricsRepository;

    /**
     * 记录功能使用情况
     */
    public void recordFeatureUsage(String feature, String userUid, boolean success) {
        FeatureMetrics metrics = FeatureMetrics.builder()
            .feature(feature)
            .userUid(userUid)
            .success(success)
            .timestamp(LocalDateTime.now())
            .build();
        
        metricsRepository.save(metrics);
    }

    /**
     * 获取功能的使用统计
     */
    public FeatureStatistics getFeatureStatistics(String feature, LocalDateTime start, LocalDateTime end) {
        List<FeatureMetrics> metrics = metricsRepository.findByFeatureAndTimestampBetween(
            feature, start, end);
        
        return FeatureStatistics.builder()
            .totalUsage(metrics.size())
            .successRate(calculateSuccessRate(metrics))
            .uniqueUsers(calculateUniqueUsers(metrics))
            .build();
    }
}
