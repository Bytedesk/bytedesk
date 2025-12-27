/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 17:11:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-19 18:16:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DependsOn("llmProviderInitializer")
@Component("llmModelInitializer")
@AllArgsConstructor
public class LlmModelInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        init();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = LlmModelPermissions.LLM_MODEL_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

    // @PostConstruct
    public void init() {
        // 不能保证在provider初始化之后执行,所以迁移到providerInitializer中执行
        // log.warn("init LLM models...");
    }
    
}
