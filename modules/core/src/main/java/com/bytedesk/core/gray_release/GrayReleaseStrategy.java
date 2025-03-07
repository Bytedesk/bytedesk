/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:10:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 11:10:58
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
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrayReleaseStrategy {
    
    private final GrayReleaseService grayReleaseService;
    private final GrayReleaseMetricsService metricsService;

    /**
     * 自动调整灰度比例
     */
    @Scheduled(cron = "0 0 * * * *")  // 每小时执行
    public void autoAdjustRollout() {
        List<String> features = grayReleaseService.getActiveFeatures();
        
        for (String feature : features) {
            FeatureStatistics stats = metricsService.getFeatureStatistics(
                feature, 
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
            );
            
            // 如果成功率高于95%，增加灰度比例
            if (stats.getSuccessRate() > 0.95) {
                grayReleaseService.increaseRolloutPercentage(feature, 10);
            }
            // 如果成功率低于80%，暂停灰度发布
            else if (stats.getSuccessRate() < 0.8) {
                grayReleaseService.pauseRollout(feature);
            }
        }
    }
}