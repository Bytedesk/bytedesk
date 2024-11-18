/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 17:11:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 12:39:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DependsOn("llmProviderInitializer")
@Component("llmModelInitializer")
@AllArgsConstructor
public class LlmModelInitializer implements SmartInitializingSingleton {

    // private final LlmModelService llmModelService;

    // private final LlmModelJsonService llmModelJsonService;

    // private final LlmProviderService llmProviderService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {
        // 不能保证在provider初始化之后执行,所以迁移到providerInitializer中执行
        // log.warn("init LLM models...");
        // Map<String, List<ModelJson>> modelJsonMap = llmModelJsonService.loadModels();
        // for (Map.Entry<String, List<ModelJson>> entry : modelJsonMap.entrySet()) {
        //     String providerName = entry.getKey();
        //     Optional<LlmProviderEntity> provider = llmProviderService.findByName(providerName);
        //     if (provider.isPresent()) {
        //         String providerUid = provider.get().getUid();
        //         log.warn("provider exists {} {} ", providerName, providerUid);
        //         List<ModelJson> modelJsons = entry.getValue();
        //         for (ModelJson modelJson : modelJsons) {
        //             if (!llmModelService.existsByNameAndProviderUid(modelJson.getName(), providerUid)) {
        //                 llmModelService.createFromModelJson(providerUid, modelJson);
        //             }
        //         }
        //     } else {
        //         log.warn("provider not exists {} ", providerName);
        //     }
        // }
    }
    
}
