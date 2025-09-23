/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 13:17:11
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
import com.bytedesk.core.llm.LlmProviderConstants;

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
        String level = LevelEnum.PLATFORM.name();
        LlmProviderEntity lpmEntity = event.getLlmProvider();
        if (level.equals(lpmEntity.getLevel())) {
            return;
        }
        
        String providerType = lpmEntity.getType();
        
        // 第三方知识库类型，直接创建同名模型
        if (isThirdPartyKnowledgeBase(providerType)) {
            LlmModelRequest request = LlmModelRequest.builder()
                .providerUid(lpmEntity.getUid())
                .name(providerType)
                .nickname(lpmEntity.getNickname())
                .description("第三方知识库: " + providerType)
                .type(LlmModelTypeEnum.TEXT.name())
                .level(LevelEnum.ORGANIZATION.name())
                .orgUid(lpmEntity.getOrgUid())
                .build();
            llmModelRestService.create(request);
            return;
        }
        
        // 复制平台版本的模型到组织版本
        List<LlmProviderEntity> llmProviderPlatformList = llmProviderRestService.findByType(providerType, level);
        if (!llmProviderPlatformList.isEmpty()) {
            LlmProviderEntity llmProviderPlatform = llmProviderPlatformList.get(0);
            List<LlmModelEntity> llmModelEntitiesPlatform = llmModelRestService.findByProviderUid(llmProviderPlatform.getUid());
            for (LlmModelEntity llmModel : llmModelEntitiesPlatform) {
                // 
                LlmModelRequest request = LlmModelRequest.builder()
                    .providerUid(lpmEntity.getUid())
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
            log.error("LlmModelEventListener onLlmProviderCreateEvent not found {}", providerType);
        }
    }
    
    /**
     * 判断是否为第三方知识库类型
     */
    private boolean isThirdPartyKnowledgeBase(String providerType) {
        return LlmProviderConstants.COZE.equals(providerType) || 
               LlmProviderConstants.DIFY.equals(providerType) || 
               LlmProviderConstants.N8N.equals(providerType) || 
               LlmProviderConstants.MAXKB.equals(providerType) || 
               LlmProviderConstants.RAGFLOW.equals(providerType);
    }
    
}
