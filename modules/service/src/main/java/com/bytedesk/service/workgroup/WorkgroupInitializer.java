/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 18:07:34
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
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;

@Component("workgroupInitializer")
@DependsOn("agentInitializer")
@AllArgsConstructor
public class WorkgroupInitializer implements SmartInitializingSingleton {

    // private final MemberInitializer memberInitializer;
    // private final AgentInitializer agentInitializer;
    // private final WorkgroupRepository workgroupRepository;
    private final WorkgroupRestService workgroupService;

    @Override
    public void afterSingletonsInstantiated() {
        // 确保 MemberInitializer 先执行完成
        // memberInitializer.init();
        // // 确保 AgentInitializer 先执行完成
        // agentInitializer.init();
        // // 
        // init();
    }


    // 迁移到 unifiedInitializer 执行
    public void init() {
        
        // if (workgroupRepository.count() > 0) {
        //     return;
        // }

        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        List<String> agentUids = Arrays.asList(BytedeskConsts.DEFAULT_AGENT_UID);
        //
        // add workgroups
        WorkgroupRequest workgroupRequest = WorkgroupRequest.builder()
                .uid(BytedeskConsts.DEFAULT_WORKGROUP_UID)
                .nickname(I18Consts.I18N_WORKGROUP_NICKNAME)
                .description(I18Consts.I18N_WORKGROUP_DESCRIPTION)
                .agentUids(agentUids)
                .orgUid(orgUid)
                .build();
        // workgroupRequest.setUid(BytedeskConsts.DEFAULT_WORKGROUP_UID);
        // workgroupRequest.setOrgUid(orgUid);
        // 写入 faq uid 到 welcomeFaqUids
        if (orgUid.equals(BytedeskConsts.DEFAULT_ORGANIZATION_UID)) {
            // 将 faq_001 ~ faq_005 写入到 welcomeFaqUids
            for (int i = 1; i <= 5; i++) {
                String faqUid = Utils.formatUid(orgUid, "faq_00" + i);
                workgroupRequest.getServiceSettings().getWelcomeFaqUids().add(faqUid);
            }
        }
        workgroupService.create(workgroupRequest);
        // 
        // add workgroup before
        WorkgroupRequest workgroupBeforeRequest = WorkgroupRequest.builder()
                .uid(BytedeskConsts.DEFAULT_WORKGROUP_UID_BEFORE)
                .nickname(I18Consts.I18N_WORKGROUP_BEFORE_NICKNAME)
                .description(I18Consts.I18N_WORKGROUP_BEFORE_DESCRIPTION)
                .agentUids(agentUids)
                .orgUid(orgUid)
                .build();
        workgroupService.create(workgroupBeforeRequest);
        // add workgroup after
        WorkgroupRequest workgroupAfterRequest = WorkgroupRequest.builder()
                .uid(BytedeskConsts.DEFAULT_WORKGROUP_UID_AFTER)
                .nickname(I18Consts.I18N_WORKGROUP_AFTER_NICKNAME)
                .description(I18Consts.I18N_WORKGROUP_AFTER_DESCRIPTION)
                .agentUids(agentUids)
                .orgUid(orgUid)
                .build();
        workgroupService.create(workgroupAfterRequest);
    }
    
}
