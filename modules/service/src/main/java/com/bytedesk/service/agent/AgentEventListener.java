/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-12 17:58:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-25 10:23:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.event.MqttConnectedEvent;
import com.bytedesk.core.event.MqttDisconnectedEvent;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.worktime.WorktimeService;
import com.bytedesk.team.member.Member;
import com.bytedesk.team.member.MemberService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AgentEventListener {

    private final AgentService agentService;

    private final MemberService memberService;

    private final WorktimeService worktimeService;
    
    // private final BytedeskProperties bytedeskProperties;

    private final UidUtils uidUtils;

    // 新注册管理员，创建组织之后，自动生成一个客服账号，主要方便入手
    @Order(6) // membereventlistener是1，robot是2，agent是3，wg是4
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        Organization organization = (Organization) event.getSource();
        User user = organization.getUser();
        String orgUid = organization.getUid();
        log.info("agent - organization created: {}", organization.getName());
        //
        Optional<Member> memberOptional = memberService.findByMobileAndOrgUid(user.getMobile(), organization.getUid());
        if (!memberOptional.isPresent()) {
            return;
        }
        Member member = memberOptional.get();
        // 创建默认客服
        AgentRequest agent1Request = AgentRequest.builder()
                .nickname(I18Consts.I18N_AGENT_NICKNAME)
                .email(member.getEmail())
                .mobile(member.getMobile())
                // .password(bytedeskProperties.getPasswordDefault())
                .memberUid(member.getUid())
                // .userUid(user.getUid())
                // .orgUid(organization.getUid())
                .build();
        agent1Request.setUid(uidUtils.getCacheSerialUid());
        agent1Request.setOrgUid(orgUid);
        //
        List<String> faqUids = Arrays.asList(
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        //
        List<String> quickButtonUids = Arrays.asList(
                orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_2);

        List<String> worktimeUids = new ArrayList<>();
        String worktimeUid = worktimeService.createDefault();
        worktimeUids.add(worktimeUid);
        //
        agent1Request.getServiceSettings().setFaqUids(faqUids);
        agent1Request.getServiceSettings().setQuickButtonUids(quickButtonUids);
        agent1Request.getServiceSettings().setWorktimeUids(worktimeUids);
        // 
        agentService.create(agent1Request);
    }

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("agent onMqttConnectedEvent uid {}, clientId {}", uid, clientId);
        //
        agentService.updateConnect(uid, true);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("agent onMqttDisconnectedEvent uid {}, clientId {}", uid, clientId);
        //
        agentService.updateConnect(uid, false);
    }

}
