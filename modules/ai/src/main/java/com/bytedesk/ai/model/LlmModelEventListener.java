/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 22:48:49
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
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.provider.LlmProviderCreateEvent;
import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
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
    public void onLlmProviderCreateEvent(LlmProviderCreateEvent event) {
        // LlmProviderCreateEvent lpm = event.getObject();
        LlmProviderEntity lpmEntity = event.getLlmProvider();
        if (lpmEntity.getLevel().equals(LevelEnum.PLATFORM.name())) {
            return;
        }
        log.info("LlmModelEventListener onLlmProviderCreateEvent {}", lpmEntity.getName());
        // 
        Optional<LlmProviderEntity> llmProviderPlatform = llmProviderRestService.findByName(lpmEntity.getName(), LevelEnum.PLATFORM.name());
        if (llmProviderPlatform.isPresent()) {
            List<LlmModelEntity> llmModelEntitiesPlatform = llmModelRestService.findByProviderUid(llmProviderPlatform.get().getUid());
            for (LlmModelEntity llmModel : llmModelEntitiesPlatform) {
                // 
                LlmModelRequest request = LlmModelRequest.builder()
                    .providerUid(lpmEntity.getUid())
                    .name(llmModel.getName())
                    .nickname(llmModel.getNickname())
                    .level(LevelEnum.ORGANIZATION.name())
                    .build();
                request.setOrgUid(lpmEntity.getOrgUid());
                llmModelRestService.create(request);
            }
        } else {
            log.error("LlmModelEventListener onLlmProviderCreateEvent not found {}", lpmEntity.getName());
        }
    }
    
}
