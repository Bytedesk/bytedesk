/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 10:01:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.provider.event.LlmProviderCreateEvent;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class LlmModelEventListener {

    private final LlmModelRestService llmModelRestService;

    private final LlmProviderRestService llmProviderRestService;

    @EventListener
    @Transactional
    public void onLlmProviderCreateEvent(LlmProviderCreateEvent event) {
        // LlmProviderCreateEvent lpm = event.getObject();
        LlmProviderEntity lpmEntity = event.getLlmProvider();
        if (lpmEntity.getLevel().equals(LevelEnum.PLATFORM.name())) {
            return;
        }
        // 
        List<LlmProviderEntity> llmProviderPlatformList = llmProviderRestService.findByType(lpmEntity.getType(), LevelEnum.PLATFORM.name());
        if (!llmProviderPlatformList.isEmpty()) {
            LlmProviderEntity llmProviderPlatform = llmProviderPlatformList.get(0);
            List<LlmModelEntity> llmModelEntitiesPlatform = llmModelRestService.findByProviderUid(llmProviderPlatform.getUid());
            for (LlmModelEntity llmModel : llmModelEntitiesPlatform) {
                // 
                LlmModelRequest request = LlmModelRequest.builder()
                    .providerUid(lpmEntity.getUid())
                    // .providerName(lpmEntity.getName())
                    .name(llmModel.getName())
                    .nickname(llmModel.getNickname())
                    .description(llmModel.getDescription())
                    .type(llmModel.getType())
                    .level(LevelEnum.ORGANIZATION.name())
                    .orgUid(lpmEntity.getOrgUid())
                    .build();
                llmModelRestService.create(request);
            }
        } else {
            log.error("LlmModelEventListener onLlmProviderCreateEvent not found {}", lpmEntity.getType());
        }
    }
    
}
