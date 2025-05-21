/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:07:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:08:20
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 灰度发布服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrayReleaseService {
    
    // private final GrayReleaseRepository grayReleaseRepository;
    private final GrayReleaseMetricsService metricsService;

    // 内存缓存，用于存储功能的灰度配置
    private final Map<String, GrayReleaseStatus> featureStatusCache = new ConcurrentHashMap<>();

    public void enableFeature(String userUid) {
        // 检查用户是否在灰度范围内
        if (isUserInGrayRelease(userUid, 
                GrayReleaseFeature.PROACTIVE_TRIGGER.getCode())) {
            // 启用功能
            // ...
        }
    }

    public void handleMessage(String userUid, String message) {
        try {
            // 处理消息
            // ...
            
            // 记录成功
            metricsService.recordFeatureUsage(userUid, 
                GrayReleaseFeature.PROACTIVE_TRIGGER, true);
        } catch (Exception e) {
            // 记录失败
            metricsService.recordFeatureUsage(userUid, 
                GrayReleaseFeature.PROACTIVE_TRIGGER, false);
            throw e;
        }
    }


    public void monitorFeature(GrayReleaseFeature feature) {
        GrayReleaseFeatureStatistics stats = metricsService.getFeatureStatistics(
            feature,
            LocalDateTime.now().minusHours(24),
            LocalDateTime.now()
        );
        
        if (stats.getFailureRate() > 0.1) {
            // 发送告警
            // sendAlert("Feature " + feature.getCode() + 
            //         " has high failure rate: " + stats.getFailureRate());
        }
    }

    /**
     * 检查用户是否在灰度范围内
     */
    public Boolean isUserInGrayRelease(String userUid, String feature) {
        // TODO: 实现检查逻辑
        return true;
    }

    
    /**
     * 初始化功能的灰度发布
     */
    @Transactional
    public void initializeFeatureRollout(GrayReleaseFeature feature) {
        Assert.notNull(feature, "Feature must not be null");
        log.info("Initializing gray release for feature: {}", feature.getCode());

        // 创建初始状态
        GrayReleaseStatus status = GrayReleaseStatus.builder()
            .feature(feature.getCode())
            .enabled(true)
            .percentage(0)
            .status(GrayReleaseStatus.STATUS_PENDING)
            .startTime(LocalDateTime.now())
            .build();

        // 更新缓存
        featureStatusCache.put(feature.getCode(), status);
        
        // 记录初始化事件
        metricsService.recordFeatureUsage(
            "system", 
            feature, 
            true
        );
    }

    /**
     * 增加灰度比例
     */
    @Transactional
    public void increaseRolloutPercentage(String feature, int targetPercentage) {
        Assert.hasText(feature, "Feature must not be empty");
        Assert.isTrue(targetPercentage >= 0 && targetPercentage <= 100, 
                "Percentage must be between 0 and 100");

        GrayReleaseStatus status = getFeatureStatus(feature);
        if (status == null) {
            throw new IllegalStateException("Feature not initialized: " + feature);
        }

        // 检查是否可以增加比例
        if (!status.canIncreaseRollout()) {
            throw new IllegalStateException("Cannot increase rollout percentage for feature: " + feature);
        }

        // 更新比例
        status.setPercentage(targetPercentage);
        if (targetPercentage >= 100) {
            status.setStatus(GrayReleaseStatus.STATUS_COMPLETED);
            status.setEndTime(LocalDateTime.now());
        }

        // 更新缓存
        featureStatusCache.put(feature, status);
        
        log.info("Updated rollout percentage for feature: {} to {}%", feature, targetPercentage);
    }

    /**
     * 暂停灰度发布
     */
    @Transactional
    public void pauseRollout(String feature) {
        Assert.hasText(feature, "Feature must not be empty");

        GrayReleaseStatus status = getFeatureStatus(feature);
        if (status == null) {
            throw new IllegalStateException("Feature not initialized: " + feature);
        }

        status.setStatus(GrayReleaseStatus.STATUS_PAUSED);
        featureStatusCache.put(feature, status);
        
        log.info("Paused rollout for feature: {}", feature);
    }

    /**
     * 恢复灰度发布
     */
    @Transactional
    public void resumeRollout(String feature) {
        Assert.hasText(feature, "Feature must not be empty");

        GrayReleaseStatus status = getFeatureStatus(feature);
        if (status == null) {
            throw new IllegalStateException("Feature not initialized: " + feature);
        }

        if (!GrayReleaseStatus.STATUS_PAUSED.equals(status.getStatus())) {
            throw new IllegalStateException("Feature is not paused: " + feature);
        }

        status.setStatus(GrayReleaseStatus.STATUS_ACTIVE);
        featureStatusCache.put(feature, status);
        
        log.info("Resumed rollout for feature: {}", feature);
    }

    /**
     * 完成灰度发布
     */
    @Transactional
    public void completeRollout(String feature) {
        Assert.hasText(feature, "Feature must not be empty");

        GrayReleaseStatus status = getFeatureStatus(feature);
        if (status == null) {
            throw new IllegalStateException("Feature not initialized: " + feature);
        }

        status.setStatus(GrayReleaseStatus.STATUS_COMPLETED);
        status.setPercentage(100);
        status.setEndTime(LocalDateTime.now());
        featureStatusCache.put(feature, status);
        
        log.info("Completed rollout for feature: {}", feature);
    }

    /**
     * 添加白名单用户
     */
    @Transactional
    public void addToWhitelist(String feature, String userUid) {
        Assert.hasText(feature, "Feature must not be empty");
        Assert.hasText(userUid, "UserUid must not be empty");

        // TODO: 实现白名单存储逻辑
        log.info("Added user {} to whitelist for feature: {}", userUid, feature);
    }

    /**
     * 获取功能的灰度状态
     */
    public GrayReleaseStatus getFeatureStatus(String feature) {
        Assert.hasText(feature, "Feature must not be empty");

        // 优先从缓存获取
        GrayReleaseStatus status = featureStatusCache.get(feature);
        if (status != null) {
            return status;
        }

        // 缓存未命中，从数据库加载
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = now.minusDays(1);
        
        // 获取最近24小时的统计数据
        GrayReleaseFeatureStatistics stats = metricsService.getFeatureStatistics(
            GrayReleaseFeature.getByCode(feature), 
            dayStart, 
            now
        );
        
        // 创建新的状态对象
        status = GrayReleaseStatus.builder()
            .feature(feature)
            .enabled(true)
            .percentage(0)
            .status(GrayReleaseStatus.STATUS_PENDING)
            .activeUsers(stats.getUniqueUsers())
            .totalUsers(1000)  // TODO: 从用户系统获取
            .successRate(stats.getSuccessRate())
            .failureRate(stats.getFailureRate())
            .startTime(dayStart)
            .build();

        // 更新缓存
        featureStatusCache.put(feature, status);
        
        return status;
    }

    /**
     * 检查用户是否可以使用某个功能
     */
    public Boolean canUserAccessFeature(String userUid, String feature) {
        Assert.hasText(userUid, "UserUid must not be empty");
        Assert.hasText(feature, "Feature must not be empty");

        GrayReleaseStatus status = getFeatureStatus(feature);
        if (status == null || !status.getEnabled()) {
            return false;
        }

        // 如果功能已完成灰度发布，所有用户都可以访问
        if (GrayReleaseStatus.STATUS_COMPLETED.equals(status.getStatus())) {
            return true;
        }

        // 如果功能已暂停，任何用户都不能访问
        if (GrayReleaseStatus.STATUS_PAUSED.equals(status.getStatus())) {
            return false;
        }

        // TODO: 检查白名单
        // if (isUserInWhitelist(userUid, feature)) {
        //     return true;
        // }

        // 根据用户ID和灰度比例判断是否可以访问
        if (GrayReleaseStatus.STATUS_ACTIVE.equals(status.getStatus())) {
            return isUserInPercentage(userUid, status.getPercentage());
        }

        return false;
    }

    /**
     * 根据用户ID和灰度比例判断用户是否在灰度范围内
     */
    private Boolean isUserInPercentage(String userUid, int percentage) {
        if (percentage >= 100) return true;
        if (percentage <= 0) return false;

        // 使用用户ID的哈希值来确保同一用户的结果一致
        int hash = Math.abs(userUid.hashCode());
        return hash % 100 < percentage;
    }

    /**
     * 获取所有活跃的灰度功能
     */
    public List<GrayReleaseFeature> getActiveFeatures() {
        return List.of(GrayReleaseFeature.values());
    }
}