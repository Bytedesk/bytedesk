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
package com.bytedesk.service.agent_settings;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AgentSettingsService {

    private final AgentSettingsRepository agentSettingsRepository;
    
    private final UidUtils uidUtils;

    /**
     * 创建默认的Agent配置
     */
    @Transactional
    public AgentSettingsEntity createDefault(String orgUid) {
        AgentSettingsEntity settings = AgentSettingsEntity.builder()
                .name("默认客服配置")
                .description("系统默认客服配置")
                .isDefault(true)
                .enabled(true)
                .serviceSettings(ServiceSettings.builder().build())
                .messageLeaveSettings(MessageLeaveSettings.builder().build())
                .autoReplySettings(AutoReplySettings.builder().build())
                .queueSettings(QueueSettings.builder().build())
                .rateDownSettings(RatedownSettingsEntity.builder().build())
                .build();
        settings.setUid(uidUtils.getUid());
        settings.setOrgUid(orgUid);
        
        return agentSettingsRepository.save(settings);
    }

    /**
     * 根据组织UID查找或创建默认配置
     */
    @Transactional
    public AgentSettingsEntity getOrCreateDefault(String orgUid) {
        Optional<AgentSettingsEntity> defaultSettings = agentSettingsRepository
                .findByOrgUidAndIsDefaultTrue(orgUid);
        
        if (defaultSettings.isPresent()) {
            return defaultSettings.get();
        }
        
        log.info("Creating default agent settings for orgUid: {}", orgUid);
        return createDefault(orgUid);
    }

    /**
     * 创建自定义配置
     */
    @Transactional
    public AgentSettingsEntity create(AgentSettingsEntity settings) {
        if (settings.getUid() == null || settings.getUid().isEmpty()) {
            settings.setUid(uidUtils.getUid());
        }
        return agentSettingsRepository.save(settings);
    }

    /**
     * 更新配置
     */
    @Transactional
    public AgentSettingsEntity update(AgentSettingsEntity settings) {
        return agentSettingsRepository.save(settings);
    }

    /**
     * 根据UID查找配置
     */
    public Optional<AgentSettingsEntity> findByUid(String uid) {
        return agentSettingsRepository.findByUid(uid);
    }

    /**
     * 删除配置
     */
    @Transactional
    public void delete(String uid) {
        Optional<AgentSettingsEntity> settings = findByUid(uid);
        settings.ifPresent(agentSettingsRepository::delete);
    }
}
