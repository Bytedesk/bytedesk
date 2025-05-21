/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:10:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 11:20:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 灰度发布策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GrayReleaseStrategy {
    
    private final GrayReleaseService grayReleaseService;
    private final GrayReleaseMetricsService metricsService;

    // 默认的灰度发布步长
    private static final int DEFAULT_INCREMENT = 10;  // 每次增加10%
    private static final double SUCCESS_RATE_THRESHOLD = 0.95;  // 95%成功率
    private static final double FAILURE_RATE_THRESHOLD = 0.20;  // 20%失败率
    private static final int MIN_SAMPLE_SIZE = 100;  // 最小样本数

    /**
     * 自动调整灰度比例
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * *")
    public void autoAdjustRollout() {
        List<GrayReleaseFeature> features = Arrays.asList(GrayReleaseFeature.values());
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(1);

        for (GrayReleaseFeature feature : features) {
            try {
                adjustFeatureRollout(feature, start, end);
            } catch (Exception e) {
                log.error("Failed to adjust rollout for feature: {}", feature.getCode(), e);
            }
        }
    }

    /**
     * 调整单个功能的灰度比例
     */
    private void adjustFeatureRollout(GrayReleaseFeature feature, 
            LocalDateTime start, LocalDateTime end) {
        
        GrayReleaseFeatureStatistics stats = metricsService.getFeatureStatistics(feature, start, end);
        
        // 样本数太少，不调整
        if (stats.getTotalUsage() < MIN_SAMPLE_SIZE) {
            log.info("Sample size too small for feature: {}, current: {}, required: {}", 
                    feature.getCode(), stats.getTotalUsage(), MIN_SAMPLE_SIZE);
            return;
        }

        // 根据统计结果调整灰度比例
        if (stats.getSuccessRate() >= SUCCESS_RATE_THRESHOLD) {
            // 成功率高，增加灰度比例
            grayReleaseService.increaseRolloutPercentage(feature.getCode(), DEFAULT_INCREMENT);
            log.info("Increased rollout percentage for feature: {}, success rate: {}", 
                    feature.getCode(), stats.getSuccessRate());
        } 
        else if (stats.getFailureRate() >= FAILURE_RATE_THRESHOLD) {
            // 失败率高，暂停灰度发布
            grayReleaseService.pauseRollout(feature.getCode());
            log.warn("Paused rollout for feature: {}, failure rate: {}", 
                    feature.getCode(), stats.getFailureRate());
        }
    }

    /**
     * 检查是否可以启动灰度发布
     */
    public Boolean canStartRollout(GrayReleaseFeature feature) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(1);
        
        GrayReleaseFeatureStatistics stats = metricsService.getFeatureStatistics(feature, start, end);
        
        return stats.getTotalUsage() >= MIN_SAMPLE_SIZE && 
               stats.getSuccessRate() >= SUCCESS_RATE_THRESHOLD;
    }
}