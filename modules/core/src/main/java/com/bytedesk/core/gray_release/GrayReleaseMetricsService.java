/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:10:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:07:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.utils.BdDateUtils;

/**
 * 灰度发布指标服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrayReleaseMetricsService {
    
    private final GrayReleaseRepository grayReleaseRepository;

    /**
     * 记录功能使用情况
     */
    public void recordFeatureUsage(String userUid, GrayReleaseFeature feature, boolean success) {
        try {
            GrayReleaseMetrics metrics = GrayReleaseMetrics.builder()
                .userUid(userUid)
                .feature(feature.getCode())
                .success(success)
                .timestamp(BdDateUtils.now())
                .build();
            
            grayReleaseRepository.save(metrics);
            log.info("Recorded feature usage: user={}, feature={}, success={}", 
                    userUid, feature.getCode(), success);
        } catch (Exception e) {
            log.error("Failed to record feature usage", e);
        }
    }

    /**
     * 获取功能的使用统计
     */
    public GrayReleaseFeatureStatistics getFeatureStatistics(GrayReleaseFeature feature, 
            ZonedDateTime start, ZonedDateTime end) {
        
        List<GrayReleaseMetrics> metrics = grayReleaseRepository
            .findByFeatureAndTimestampBetween(feature.getCode(), start, end);
        
        long totalUsage = metrics.size();
        long successCount = metrics.stream().filter(GrayReleaseMetrics::getSuccess).count();
        long uniqueUsers = metrics.stream()
            .map(GrayReleaseMetrics::getUserUid)
            .distinct()
            .count();
        
        return GrayReleaseFeatureStatistics.builder()
            .feature(feature.getCode())
            .totalUsage(totalUsage)
            .successCount(successCount)
            .uniqueUsers(uniqueUsers)
            .successRate(totalUsage > 0 ? (double) successCount / totalUsage : 0.0)
            .build();
    }
}
