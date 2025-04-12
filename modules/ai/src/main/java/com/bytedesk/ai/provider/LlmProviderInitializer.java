/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 17:10:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 21:24:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.model.LlmModelJsonLoader;
import com.bytedesk.ai.model.LlmModelJsonLoader.ModelJson;
import com.bytedesk.ai.model.LlmModelRestService;
import com.bytedesk.ai.provider.LlmProviderJsonLoader.ProviderJson;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("llmProviderInitializer")
@AllArgsConstructor
public class LlmProviderInitializer implements SmartInitializingSingleton {

    private final LlmProviderRestService llmProviderService;

    private final LlmProviderJsonLoader llmProviderJsonService;

    private final LlmModelRestService llmModelService;

    private final LlmModelJsonLoader llmModelJsonService;

    @Override
    public void afterSingletonsInstantiated() {
        // init();
    }

    // @PostConstruct
    public void init() {
        // init platform providers
        String level = LevelEnum.PLATFORM.name();
        Map<String, ProviderJson> providerJsonMap = llmProviderJsonService.loadProviders();
        for (Map.Entry<String, ProviderJson> entry : providerJsonMap.entrySet()) {
            String providerName = entry.getKey();
            ProviderJson providerJson = entry.getValue();
            // log.info("initialize provider {}", providerName);
            if (!llmProviderService.existsByNameAndLevel(providerName, level)) {
                llmProviderService.createFromProviderJson(providerName, providerJson, level);
            }
        }
        // 
        Map<String, List<ModelJson>> modelJsonMap = llmModelJsonService.loadModels();
        for (Map.Entry<String, List<ModelJson>> entry : modelJsonMap.entrySet()) {
            String providerName = entry.getKey();
            Optional<LlmProviderEntity> provider = llmProviderService.findByName(providerName, level);
            if (provider.isPresent()) {
                String providerUid = provider.get().getUid();
                // log.warn("provider exists {} {} ", providerName, providerUid);
                List<ModelJson> modelJsons = entry.getValue();
                for (ModelJson modelJson : modelJsons) {
                    if (!llmModelService.existsByNameAndProviderUid(modelJson.getName(), providerUid)) {
                        llmModelService.createFromModelJson(providerUid, providerName, modelJson);
                    }
                }
            }
        }
        // 
        // init super admin providers, models will be created in LlmModelEventListener
        level = LevelEnum.ORGANIZATION.name();
        for (Map.Entry<String, ProviderJson> entry : providerJsonMap.entrySet()) {
            String providerName = entry.getKey();
            ProviderJson providerJson = entry.getValue();
            // log.info("initialize provider {}", providerName);
            String status = providerJson.getStatus();
            if (status.equals(LlmProviderStatusEnum.PRODUCTION.name())) {
                if (!llmProviderService.existsByNameAndLevel(providerName, level)) {
                    llmProviderService.createFromProviderJson(providerName, providerJson, level);
                }
            }
        }
    }
    
}
