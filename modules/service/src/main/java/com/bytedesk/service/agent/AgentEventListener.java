/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-12 17:58:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 15:37:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.Optional;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.kbase.kbase.KbaseRequest;
import com.bytedesk.kbase.kbase.KbaseResponse;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;
import com.bytedesk.kbase.quick_reply.QuickReplyRequest;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.service.agent.event.AgentCreateEvent;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.kbase.llm_faq.FaqEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AgentEventListener {

    private final AgentRestService agentRestService;
    private final KbaseRestService kbaseRestService;
    private final IMessageSendService messageSendService;
    private final QuickReplyRestService quickReplyRestService;
    private final QueueMemberRestService queueMemberRestService;

    // 新注册管理员，创建组织之后，自动生成一个客服账号，主要方便入手
    @Order(6)
    @EventListener
    @Transactional
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        UserEntity user = organization.getUser();
        String orgUid = organization.getUid();
        // log.info("agent - organization created: {}", organization.getName());
        String mobile = user.getMobile();
        if (StringUtils.hasText(mobile)) {
            agentRestService.createFromMember(mobile, orgUid);
            return;
        }
        String email = user.getEmail();
        if (StringUtils.hasText(email)) {
            agentRestService.createFromMemberByEmail(email, orgUid);
            return;
        }
        log.warn("agent - skip default agent creation, mobile/email empty for org {}", orgUid);
    }
    
    // 新创建客服，创建默认知识库
    // 因为依赖关系的原因，无法将该方法放到kbase事件监听中，所以放在此处
    @EventListener
    @Transactional
    public void onAgentCreateEvent(AgentCreateEvent event) {
        // AgentCreateEvent agentCreateEvent = (AgentCreateEvent) event.getObject();
        AgentEntity agent = event.getAgent();
        // log.info("agent onAgentCreateEvent: {}", agent.getUid());
        initAgentKbase(agent);
        // 为客服创建默认排队助手会话
        queueMemberRestService.createAgentQueueThread(agent);
    }

    // 客服接待数量发生变化，增加接待数量，发送欢迎语
    @EventListener
    public void onThreadAcceptEvent(ThreadAcceptEvent event) {
        // log.info("agent onThreadAcceptEvent: {}", event);
        ThreadEntity thread = event.getThread();
        // log.info("agent onThreadAcceptEvent: {}", agentString);
        UserProtobuf agentProtobuf = thread.getAgentProtobuf();
        Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentProtobuf.getUid());
        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            // 发送欢迎语
            String tip = agent.getSettings() != null && agent.getSettings().getServiceSettings() != null
                ? agent.getSettings().getServiceSettings().getWelcomeTip()
                : null;
            WelcomeContent.WelcomeContentBuilder<?, ?> builder = WelcomeContent.builder().content(tip);
            if (agent.getSettings() != null && agent.getSettings().getServiceSettings() != null) {
                var settings = agent.getSettings().getServiceSettings();
                builder.kbUid(settings.getWelcomeKbUid());
                if (settings.getWelcomeFaqs() != null && !settings.getWelcomeFaqs().isEmpty()) {
                    java.util.List<WelcomeContent.QA> qas = new java.util.ArrayList<>();
                    for (FaqEntity f : settings.getWelcomeFaqs()) {
                        qas.add(WelcomeContent.QA.builder()
                                .uid(f.getUid())
                                .question(f.getQuestion())
                                .answer(f.getAnswer())
                                .type(f.getType())
                                .build());
                    }
                    builder.faqs(qas);
                }
            }
            MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(builder.build(), thread);
            messageSendService.sendProtobufMessage(messageProtobuf);
        } else {
            log.error("agent not found {}", agentProtobuf.getUid());
        }
    }

    private void initAgentKbase(AgentEntity agent) {
        // 创建快捷回复知识库
        KbaseRequest kbaseQuickReply = KbaseRequest.builder()
                .name(agent.getNickname() +  "Kb")
                .descriptionHtml(agent.getNickname() + "Kb")
                .language(LanguageEnum.ZH_CN.name())
                .level(LevelEnum.AGENT.name())
                .type(KbaseTypeEnum.QUICKREPLY.name())
                .agentUid(agent.getUid())
                .orgUid(agent.getOrgUid())
                .build();
        KbaseResponse kb = kbaseRestService.create(kbaseQuickReply);

        // 初始化一条个人快捷回复（仅坐席本人可见）
        try {
            QuickReplyRequest qr = QuickReplyRequest.builder()
                .title("我的第一条快捷回复")
                .content("这是你的个人快捷回复。你可以在右侧面板编辑、设置快捷键并快速插入到会话中。")
                .kbUid(kb.getUid())
                .agentUid(agent.getUid())
                .orgUid(agent.getOrgUid())
                .level(LevelEnum.AGENT.name())
                .type(MessageTypeEnum.TEXT.name())
                .build();
            quickReplyRestService.create(qr);
        } catch (Exception ex) {
            log.warn("Failed to create default personal quick reply for agent {}: {}", agent.getUid(), ex.getMessage());
        }
    }

    
}
