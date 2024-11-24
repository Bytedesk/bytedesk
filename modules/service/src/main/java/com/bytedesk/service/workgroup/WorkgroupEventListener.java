/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:20:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 15:27:13
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
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.faq.FaqConsts;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.worktime.WorktimeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class WorkgroupEventListener {

    private final AgentRestService agentService;
    
    private final WorkgroupService workgroupService;

    private final WorktimeService worktimeService;

    private final UidUtils uidUtils;

    @Order(7)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        UserEntity user = organization.getUser();
        String orgUid = organization.getUid();
        // 创建默认workgroup
        log.info("workgroup - organization created: {}", organization.getName());

        List<String> agentUids = new ArrayList<>();
        Optional<AgentEntity> agentOptional = agentService.findByMobileAndOrgUid(user.getMobile(), organization.getUid());
        agentOptional.ifPresent(agent -> {
            agentUids.add(agent.getUid());
        });
        // 
        List<String> faqUids = Arrays.asList(
            orgUid + FaqConsts.FAQ_DEMO_UID_1,
            orgUid + FaqConsts.FAQ_DEMO_UID_2
        );
        // 
        List<String> worktimeUids = new ArrayList<>();
        String worktimeUid = worktimeService.createDefault();
        worktimeUids.add(worktimeUid);
        
        // add workgroups
        WorkgroupRequest workgroupRequest = WorkgroupRequest.builder()
                .nickname(I18Consts.I18N_WORKGROUP_NICKNAME)
                .description(I18Consts.I18N_WORKGROUP_DESCRIPTION)
                .agentUids(agentUids)
                // .orgUid(organization.getUid())
                .build();
        workgroupRequest.setUid(uidUtils.getCacheSerialUid());
        workgroupRequest.setOrgUid(orgUid);
        workgroupRequest.getServiceSettings().setFaqUids(faqUids);
        workgroupRequest.getServiceSettings().setQuickFaqUids(faqUids);
        workgroupRequest.getServiceSettings().setWorktimeUids(worktimeUids);

        workgroupService.create(workgroupRequest);
    }


}
