/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:40:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:26:25
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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.utils.BdDateUtils;

/**
 * 灰度发布控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gray-release")
public class GrayReleaseController {

    private final GrayReleaseService grayReleaseService;
    private final GrayReleaseMetricsService metricsService;
    
    /**
     * 初始化功能的灰度发布
     */
    @PostMapping("/features/{feature}/init")
    public ResponseEntity<?> initializeFeature(@PathVariable String feature) {
        GrayReleaseFeature grayFeature = GrayReleaseFeature.getByCode(feature);
        if (grayFeature == null) {
            return ResponseEntity.badRequest().body("Invalid feature code");
        }
        
        grayReleaseService.initializeFeatureRollout(grayFeature);
        return ResponseEntity.ok().build();
    }

    /**
     * 调整灰度比例
     */
    @PutMapping("/features/{feature}/rollout")
    public ResponseEntity<?> adjustRollout(
            @PathVariable String feature,
            @RequestParam int percentage) {
        
        if (percentage < 0 || percentage > 100) {
            return ResponseEntity.badRequest().body("Percentage must be between 0 and 100");
        }

        grayReleaseService.increaseRolloutPercentage(feature, percentage);
        return ResponseEntity.ok().build();
    }

    /**
     * 暂停灰度发布
     */
    @PostMapping("/features/{feature}/pause")
    public ResponseEntity<?> pauseRollout(@PathVariable String feature) {
        grayReleaseService.pauseRollout(feature);
        return ResponseEntity.ok().build();
    }

    /**
     * 恢复灰度发布
     */
    @PostMapping("/features/{feature}/resume")
    public ResponseEntity<?> resumeRollout(@PathVariable String feature) {
        grayReleaseService.resumeRollout(feature);
        return ResponseEntity.ok().build();
    }

    /**
     * 完成灰度发布
     */
    @PostMapping("/features/{feature}/complete")
    public ResponseEntity<?> completeRollout(@PathVariable String feature) {
        grayReleaseService.completeRollout(feature);
        return ResponseEntity.ok().build();
    }

    /**
     * 添加白名单用户
     */
    @PostMapping("/features/{feature}/whitelist/{userUid}")
    public ResponseEntity<?> addToWhitelist(
            @PathVariable String feature,
            @PathVariable String userUid) {
        grayReleaseService.addToWhitelist(feature, userUid);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取功能状态
     */
    @GetMapping("/features/{feature}")
    public ResponseEntity<GrayReleaseStatus> getFeatureStatus(
            @PathVariable String feature) {
        return ResponseEntity.ok(grayReleaseService.getFeatureStatus(feature));
    }
    
    /**
     * 获取功能统计数据
     */
    @GetMapping("/features/{feature}/stats")
    public ResponseEntity<GrayReleaseFeatureStatistics> getStats(
            @PathVariable String feature,
            @RequestParam(required = false) Integer hours) {
        
        ZonedDateTime end = BdDateUtils.now();
        ZonedDateTime start = end.minusHours(hours != null ? hours : 24);
        
        GrayReleaseFeature grayFeature = GrayReleaseFeature.getByCode(feature);
        if (grayFeature == null) {
            return ResponseEntity.badRequest().body(null);
        }

        GrayReleaseFeatureStatistics stats = metricsService.getFeatureStatistics(
            grayFeature, start, end);
        
        return ResponseEntity.ok(stats);
    }
}
