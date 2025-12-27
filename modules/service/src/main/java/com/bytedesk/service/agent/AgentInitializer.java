/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 15:59:41
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
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component("agentInitializer")
@DependsOn("memberInitializer") // 假设MemberInitializer的bean名称是memberInitializer
@AllArgsConstructor
public class AgentInitializer implements SmartInitializingSingleton {

    private final AgentRestService agentRestService;
    private final BytedeskProperties bytedeskProperties;
    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        // 迁移到 WorkgroupInitializer执行
        // init();
        initAuthority();
    }

    public void init() {
        // 
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String mobile = bytedeskProperties.getMobile();
        String nickname = bytedeskProperties.getNickname();
        String email = bytedeskProperties.getEmail();
        String memberUid = BytedeskConsts.DEFAULT_MEMBER_UID;
        String agentUid = BytedeskConsts.DEFAULT_AGENT_UID;
         // 创建默认客服
         AgentRequest agentRequest = AgentRequest.builder()
                .uid(agentUid)
                .nickname(nickname)
                .email(email)
                .mobile(mobile)
                .memberUid(memberUid)
                .orgUid(orgUid)
                .build();
        agentRestService.create(agentRequest);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = AgentPermissions.AGENT_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
    
}
