/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 17:10:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-18 14:42:12
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

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.model.LlmModelJsonLoader;
import com.bytedesk.ai.model.LlmModelJsonLoader.ModelJson;
import com.bytedesk.ai.model.LlmModelRestService;
import com.bytedesk.ai.provider.LlmProviderJsonLoader.ProviderJson;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("llmProviderInitializer")
@AllArgsConstructor
public class LlmProviderInitializer implements SmartInitializingSingleton {

    private final LlmProviderRestService llmProviderRestService;

    private final LlmProviderJsonLoader llmProviderJsonService;

    private final LlmModelRestService llmModelRestService;

    private final LlmModelJsonLoader llmModelJsonService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }
    /**
     * 初始化 LLM 提供商和模型
     * 
     * @see LlmProviderRestService#existsByNameAndLevel(String, String)
     * @see LlmModelRestService#existsByNameAndProviderUid(String, String)
     */
    public void init() {
        // init platform providers
        String level = LevelEnum.PLATFORM.name();
        Map<String, ProviderJson> providerJsonMap = llmProviderJsonService.loadProviders();
        for (Map.Entry<String, ProviderJson> entry : providerJsonMap.entrySet()) {
            String providerName = entry.getKey();
            ProviderJson providerJson = entry.getValue();
            // log.info("initialize provider {}", providerName);
            if (!llmProviderRestService.existsByNameAndLevel(providerName, level)) {
                String orgUid = ""; // 平台版本暂时不支持组织
                llmProviderRestService.createFromProviderJson(providerName, providerJson, level, orgUid);
            } else {
                // 如果已经存在，则更新，因为status有可能从DEVELOPMENT变为PRODUCTION
                List<LlmProviderEntity> providerList = llmProviderRestService.findByName(providerName, level);
                if (!providerList.isEmpty()) {
                    LlmProviderEntity providerEntity = providerList.get(0);
                    String status = providerJson.getStatus();
                    if (!providerEntity.getStatus().equals(status)) {
                        providerEntity.setStatus(status);
                        llmProviderRestService.save(providerEntity);
                    }
                }
            }
        }
        //
        Map<String, List<ModelJson>> modelJsonMap = llmModelJsonService.loadModels();
        for (Map.Entry<String, List<ModelJson>> entry : modelJsonMap.entrySet()) {
            String providerName = entry.getKey();
            List<LlmProviderEntity> providerList = llmProviderRestService.findByName(providerName, level);
            if (!providerList.isEmpty()) {
                LlmProviderEntity provider = providerList.get(0);
                String providerUid = provider.getUid();
                // log.warn("provider exists {} {} ", providerName, providerUid);
                List<ModelJson> modelJsons = entry.getValue();
                for (ModelJson modelJson : modelJsons) {
                    if (!llmModelRestService.existsByNameAndProviderUid(modelJson.getName(), providerUid)) {
                        String orgUid = ""; // 平台版本暂时不支持组织
                        llmModelRestService.createFromModelJson(providerUid, providerName, modelJson, level, orgUid);
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
            if (LlmProviderStatusEnum.PRODUCTION.name().equalsIgnoreCase(status)) {
                if (!llmProviderRestService.existsByNameAndLevelAndStatus(providerName, level, status)) {
                    String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
                    llmProviderRestService.createFromProviderJson(providerName, providerJson, level, orgUid);
                } else {
                    // 如果已经存在，则更新，因为status有可能从DEVELOPMENT变为PRODUCTION
                    List<LlmProviderEntity> providerList = llmProviderRestService.findByName(providerName, level);
                    if (!providerList.isEmpty()) {
                        LlmProviderEntity providerEntity = providerList.get(0);
                        providerEntity.setStatus(status);
                        llmProviderRestService.save(providerEntity);
                    }
                }
            }
        }
    }

}
