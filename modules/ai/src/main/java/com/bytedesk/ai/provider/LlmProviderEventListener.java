/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:11:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-19 17:36:30
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

import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class LlmProviderEventListener {

    private final LlmProviderRestService llmProviderRestService;

    private final ModelMapper modelMapper;

    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", orgUid);
        // 
        String status = LlmProviderStatusEnum.PRODUCTION.name();
        String level = LevelEnum.PLATFORM.name();
        List<LlmProviderEntity> providers = llmProviderRestService.findByStatusAndLevelAndDeletedFalse(status, level);
        // 遍历providers，复制provider，设置orgUid，保存到数据库
        for (LlmProviderEntity provider : providers) {
            LlmProviderRequest providerRequest = modelMapper.map(provider, LlmProviderRequest.class);
            providerRequest.setLevel(LevelEnum.ORGANIZATION.name());
            providerRequest.setOrgUid(orgUid);
            llmProviderRestService.create(providerRequest);
        }
    }
    
    
}
