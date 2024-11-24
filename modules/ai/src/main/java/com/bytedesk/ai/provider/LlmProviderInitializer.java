/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 17:10:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-19 18:19:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.bytedesk.ai.model.LlmModelJsonService;
import com.bytedesk.ai.model.LlmModelJsonService.ModelJson;
import com.bytedesk.ai.model.LlmModelRestService;
import com.bytedesk.ai.provider.LlmProviderJsonService.ProviderJson;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("llmProviderInitializer")
@AllArgsConstructor
public class LlmProviderInitializer implements SmartInitializingSingleton {

    private final LlmProviderRestService llmProviderService;

    private final LlmProviderJsonService llmProviderJsonService;

    private final LlmModelRestService llmModelService;

    private final LlmModelJsonService llmModelJsonService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
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
                        llmModelService.createFromModelJson(providerUid, modelJson);
                    }
                }
            } else {
                log.warn("provider not exists {} ", providerName);
            }
        }
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
