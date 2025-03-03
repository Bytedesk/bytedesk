/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-03 14:38:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 14:46:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.unified;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.service.agent.AgentInitializer;
import com.bytedesk.service.workgroup.WorkgroupInitializer;
import com.bytedesk.team.member.MemberInitializer;

import lombok.AllArgsConstructor;

@Component("unifiedInitializer")
@DependsOn("workgroupInitializer")
@AllArgsConstructor
public class UnifiedInitializer implements SmartInitializingSingleton {

    private final MemberInitializer memberInitializer;
    private final AgentInitializer agentInitializer;
    private final WorkgroupInitializer workgroupInitializer;
    private final UnifiedRepository unifiedRepository;
    private final UnifiedRestService unifiedService;

    @Override
    public void afterSingletonsInstantiated() {
        // 确保 MemberInitializer 先执行完成
        memberInitializer.init();
        // 确保 AgentInitializer 先执行完成
        agentInitializer.init();
        // 确保 WorkgroupInitializer 先执行完成
        workgroupInitializer.init();
        // 
        init();
    }

    // @PostConstruct
    public void init() {
        
        if (unifiedRepository.count() > 0) {
            return;
        }

        // 默认统一入口技能组
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        List<String> workgroupUids = Arrays.asList(BytedeskConsts.DEFAULT_WORKGROUP_UID);

        UnifiedRequest unifiedRequest = UnifiedRequest.builder()
                .nickname(I18Consts.I18N_UNIFIED_NICKNAME)
                .description(I18Consts.I18N_UNIFIED_DESCRIPTION)
                .workgroupUids(workgroupUids)
                .build();
        unifiedRequest.setUid(BytedeskConsts.DEFAULT_UNIFIED_UID);
        unifiedRequest.setType(ThreadTypeEnum.WORKGROUP.name());
        unifiedRequest.setOrgUid(orgUid);
        unifiedService.create(unifiedRequest);
    }
    
}