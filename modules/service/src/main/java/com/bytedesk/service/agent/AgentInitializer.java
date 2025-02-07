/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-17 12:31:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;

import lombok.AllArgsConstructor;

@Component("agentInitializer")
@DependsOn("memberInitializer") // 假设MemberInitializer的bean名称是memberInitializer
@AllArgsConstructor
public class AgentInitializer implements SmartInitializingSingleton {

    private final AgentRestService agentService;
    private final AgentRepository agentRepository;
    private final BytedeskProperties bytedeskProperties;

    @Override
    public void afterSingletonsInstantiated() {
        // 迁移到 WorkgroupInitializer执行
        // init();
    }

    // @PostConstruct
    public void init() {

        if (agentRepository.count() > 0) {
            return;
        }
        // 
        String mobile = bytedeskProperties.getMobile();
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String agentUid = BytedeskConsts.DEFAULT_AGENT_UID;
        agentService.createFromMember(mobile, orgUid, agentUid);
    }
    
}
