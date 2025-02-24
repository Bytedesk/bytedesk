/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 23:03:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.service.agent.AgentInitializer;
import com.bytedesk.team.member.MemberInitializer;

import lombok.AllArgsConstructor;

@Component
@DependsOn("agentInitializer")
@AllArgsConstructor
public class WorkgroupInitializer implements SmartInitializingSingleton {

    private final MemberInitializer memberInitializer;
    private final AgentInitializer agentInitializer;
    private final WorkgroupRepository workgroupRepository;
    private final WorkgroupRestService workgroupService;

    @Override
    public void afterSingletonsInstantiated() {
        // 确保 MemberInitializer 先执行完成
        memberInitializer.init();
        // 确保 AgentInitializer 先执行完成
        agentInitializer.init();
        // 
        init();
    }

    // @PostConstruct
    public void init() {
        
        if (workgroupRepository.count() > 0) {
            return;
        }

        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        List<String> agentUids = Arrays.asList(BytedeskConsts.DEFAULT_AGENT_UID);
        //
        // add workgroups
        WorkgroupRequest workgroupRequest = WorkgroupRequest.builder()
                .nickname(I18Consts.I18N_WORKGROUP_NICKNAME)
                .agentUids(agentUids)
                .build();
        workgroupRequest.setUid(BytedeskConsts.DEFAULT_WORKGROUP_UID);
        workgroupRequest.setOrgUid(orgUid);
        //
        workgroupService.create(workgroupRequest);
    }
    
}
