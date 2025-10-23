/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23 14:35:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-23 14:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_settings;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RobotSettingsService {

    private final RobotSettingsRepository robotSettingsRepository;
    
    private final UidUtils uidUtils;

    /**
     * 创建默认的Robot配置
     */
    @Transactional
    public RobotSettingsEntity createDefault(String orgUid) {
        RobotSettingsEntity settings = RobotSettingsEntity.builder()
                .name("默认机器人配置")
                .description("系统默认机器人配置")
                .isDefault(true)
                .enabled(true)
                .serviceSettings(ServiceSettings.builder()
                        .showFaqs(true)
                        .showQuickFaqs(true)
                        .showGuessFaqs(true)
                        .showHotFaqs(true)
                        .showShortcutFaqs(true)
                        .build())
                .rateDownSettings(RatedownSettingsEntity.builder().build())
                .build();
        settings.setUid(uidUtils.getUid());
        settings.setOrgUid(orgUid);
        
        return robotSettingsRepository.save(settings);
    }

    /**
     * 根据组织UID查找或创建默认配置
     */
    @Transactional
    public RobotSettingsEntity getOrCreateDefault(String orgUid) {
        Optional<RobotSettingsEntity> defaultSettings = robotSettingsRepository
                .findByOrgUidAndIsDefaultTrue(orgUid);
        
        if (defaultSettings.isPresent()) {
            return defaultSettings.get();
        }
        
        log.info("Creating default robot settings for orgUid: {}", orgUid);
        return createDefault(orgUid);
    }

    /**
     * 创建自定义配置
     */
    @Transactional
    public RobotSettingsEntity create(RobotSettingsEntity settings) {
        if (settings.getUid() == null || settings.getUid().isEmpty()) {
            settings.setUid(uidUtils.getUid());
        }
        return robotSettingsRepository.save(settings);
    }

    /**
     * 更新配置
     */
    @Transactional
    public RobotSettingsEntity update(RobotSettingsEntity settings) {
        return robotSettingsRepository.save(settings);
    }

    /**
     * 根据UID查找配置
     */
    public Optional<RobotSettingsEntity> findByUid(String uid) {
        return robotSettingsRepository.findByUid(uid);
    }

    /**
     * 删除配置
     */
    @Transactional
    public void delete(String uid) {
        Optional<RobotSettingsEntity> settings = findByUid(uid);
        settings.ifPresent(robotSettingsRepository::delete);
    }
}
