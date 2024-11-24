/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 16:22:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.faq.FaqConsts;
import com.bytedesk.service.worktime.WorktimeService;

import lombok.AllArgsConstructor;

@Component
@DependsOn("agentInitializer")
@AllArgsConstructor
public class WorkgroupInitializer implements SmartInitializingSingleton {

    private final WorkgroupRepository workgroupRepository;

    private final WorkgroupService workgroupService;

    private final WorktimeService worktimeService;

    @Override
    public void afterSingletonsInstantiated() {
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
        List<String> faqUids = Arrays.asList(
                orgUid + FaqConsts.FAQ_DEMO_UID_1,
                orgUid + FaqConsts.FAQ_DEMO_UID_2);
        //
        List<String> worktimeUids = new ArrayList<>();
        String worktimeUid = worktimeService.createDefault();
        worktimeUids.add(worktimeUid);
        //
        // add workgroups
        WorkgroupRequest workgroupRequest = WorkgroupRequest.builder()
                .nickname(I18Consts.I18N_WORKGROUP_NICKNAME)
                .agentUids(agentUids)
                .build();
        workgroupRequest.setUid(BytedeskConsts.DEFAULT_WORKGROUP_UID);
        workgroupRequest.setOrgUid(orgUid);
        //
        workgroupRequest.getServiceSettings().setFaqUids(faqUids);
        workgroupRequest.getServiceSettings().setQuickFaqUids(faqUids);
        workgroupRequest.getServiceSettings().setWorktimeUids(worktimeUids);
        //
        workgroupService.create(workgroupRequest);
    }
    
}
