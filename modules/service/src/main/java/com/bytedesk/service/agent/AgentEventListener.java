/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-12 17:58:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-18 16:11:09
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

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.socket.mqtt.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.MqttDisconnectedEvent;
// import com.bytedesk.core.thread.ThreadCreateEvent;
// import com.bytedesk.core.thread.ThreadUpdateEvent;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.knowledge_base.KnowledgebaseRequest;
import com.bytedesk.kbase.knowledge_base.KnowledgebaseService;
import com.bytedesk.kbase.knowledge_base.KnowledgebaseTypeEnum;
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

    private final KnowledgebaseService knowledgebaseService;
    
    private final UidUtils uidUtils;

    // 新注册管理员，创建组织之后，自动生成一个客服账号，主要方便入手
    @Order(6)
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
                .memberUid(member.getUid())
                // .userUid(user.getUid())
                .build();
        agent1Request.setUid(uidUtils.getCacheSerialUid());
        agent1Request.setOrgUid(orgUid);
        //
        List<String> faqUids = Arrays.asList(
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        //
        // List<String> quickButtonUids = Arrays.asList(
        //         orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_1,
        //         orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_2);

        List<String> worktimeUids = new ArrayList<>();
        String worktimeUid = worktimeService.createDefault();
        worktimeUids.add(worktimeUid);
        //
        agent1Request.getServiceSettings().setFaqUids(faqUids);
        agent1Request.getServiceSettings().setQuickFaqUids(faqUids);
        agent1Request.getServiceSettings().setWorktimeUids(worktimeUids);
        // 
        agentService.create(agent1Request);
    }


    // 因为依赖关系的原因，无法将该方法放到kbase事件监听中，所以放在此处
    @EventListener
    public void onAgentCreateEvent(GenericApplicationEvent<AgentCreateEvent> event) {
        AgentCreateEvent agentCreateEvent = (AgentCreateEvent) event.getObject();
        Agent agent = agentCreateEvent.getAgent();
        log.info("agent onAgentCreateEvent: {}", agent.getUid());
        // 创建快捷回复知识库
        KnowledgebaseRequest kownledgebaseRequeqstQuickReply = KnowledgebaseRequest.builder()
                .name(agent.getNickname() +  "Kb")
                .descriptionHtml(agent.getNickname() + "Kb")
                .language(LanguageEnum.ZH_CN.name())
                .level(LevelEnum.AGENT.name())
                .build();
        kownledgebaseRequeqstQuickReply.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
        kownledgebaseRequeqstQuickReply.setOrgUid(agent.getOrgUid());
        kownledgebaseRequeqstQuickReply.setAgentUid(agent.getUid());
        knowledgebaseService.create(kownledgebaseRequeqstQuickReply);
    }


    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        // log.info("agent onMqttConnectedEvent uid {}, clientId {}", uid, clientId);
        //
        agentService.updateConnect(uid, true);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        // log.info("agent onMqttDisconnectedEvent uid {}, clientId {}", uid, clientId);
        //
        agentService.updateConnect(uid, false);
    }

        
    // TODO: 定时ping客服，检查在线状态
    // @EventListener
    // public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
    //     // log.info("agent quartz five second event: " + event);
    // }

    // // TODO: 新创建会话，更新客服当前接待数量
    // @EventListener
    // public void onThreadCreateEvent(ThreadCreateEvent event) {
    //     // log.info("agent onThreadCreateEvent: " + event);
    // }

    // // TODO: 会话关闭，更新客服当前接待数量
    // @EventListener
    // public void onThreadUpdateEvent(ThreadUpdateEvent event) {
    //     // log.info("agent onThreadUpdateEvent: " + event);
    // }

    
}
