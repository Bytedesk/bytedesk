/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:07:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 11:11:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrayReleaseService {
    
    // private final ServiceSettingsRepository serviceSettingsRepository;

    /**
     * 初始化功能的灰度发布
     */
    public void initializeFeatureRollout(String feature, String description) {
        GrayReleaseConfig config = new GrayReleaseConfig();
        config.setEnableGrayRelease(true);
        
        // FeatureConfig.Feature newFeature = new FeatureConfig.Feature();
        // newFeature.setName(feature);
        // newFeature.setEnabled(true);
        // newFeature.setDescription(description);

        // FeatureConfig featureConfig = new FeatureConfig();
        // featureConfig.getFeatures().add(newFeature);

        // config.setFeatures(BytedeskConsts.OBJECT_MAPPER.writeValueAsString(featureConfig));
        // config.setGrayReleasePercentage(0);  // 初始设置为0%
        // config.setStartTime(LocalDateTime.now());
        // config.setStatus("pending");

        // 保存配置
        // serviceSettingsRepository.save(settings);
    }

    /**
     * 增加灰度比例
     */
    public void increaseRolloutPercentage(String feature, int increment) {
        // ServiceSettings settings = serviceSettingsRepository.findByFeature(feature);
        // GrayReleaseConfig config = settings.getGrayReleaseConfig();
        
        // int newPercentage = Math.min(100, config.getGrayReleasePercentage() + increment);
        // config.setGrayReleasePercentage(newPercentage);
        
        // if (newPercentage >= 100) {
        //     config.setStatus("completed");
        //     config.setEndTime(LocalDateTime.now());
        // }

        // serviceSettingsRepository.save(settings);
    }

    /**
     * 添加白名单用户
     */
    public void addToWhitelist(String feature, String userUid) {
        // ServiceSettings settings = serviceSettingsRepository.findByFeature(feature);
        // GrayReleaseConfig config = settings.getGrayReleaseConfig();
        
        // WhitelistConfig whitelist = WhitelistConfig.fromJson(config.getWhitelistUsers());
        // whitelist.getUserUids().add(userUid);
        
        // config.setWhitelistUsers(BytedeskConsts.OBJECT_MAPPER.writeValueAsString(whitelist));
        // serviceSettingsRepository.save(settings);
    }

    /**
     * 获取功能的灰度状态
     */
    public GrayReleaseStatus getFeatureStatus(String feature) {
        // ServiceSettings settings = serviceSettingsRepository.findByFeature(feature);
        // GrayReleaseConfig config = settings.getGrayReleaseConfig();
        
        // return GrayReleaseStatus.builder()
        //     .feature(feature)
        //     .enabled(config.isEnableGrayRelease())
        //     .percentage(config.getGrayReleasePercentage())
        //     .status(config.getStatus())
        //     .activeUsers(calculateActiveUsers(feature))
        //     .build();
    }
}