/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-12 17:58:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-16 17:04:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.socket.mqtt.MqttConnectionService;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.event.MqttDisconnectedEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.kbase.KbaseRequest;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;
import com.bytedesk.service.agent.event.AgentCreateEvent;
import com.bytedesk.service.utils.ThreadMessageUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AgentEventListener {

    private final AgentRestService agentService;
    private final KbaseRestService knowledgebaseService;
    private final UidUtils uidUtils;
    private final MqttConnectionService mqttConnectionService;
    private final IMessageSendService messageSendService;

    // 新注册管理员，创建组织之后，自动生成一个客服账号，主要方便入手
    @Order(6)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        UserEntity user = organization.getUser();
        String orgUid = organization.getUid();
        log.info("agent - organization created: {}", organization.getName());
        //
        String mobile = user.getMobile();
        String agentUid = uidUtils.getUid();
        agentService.createFromMember(mobile, orgUid, agentUid);
    }
    
    // 新创建客服，创建默认知识库
    // 因为依赖关系的原因，无法将该方法放到kbase事件监听中，所以放在此处
    @EventListener
    public void onAgentCreateEvent(AgentCreateEvent event) {
        // AgentCreateEvent agentCreateEvent = (AgentCreateEvent) event.getObject();
        AgentEntity agent = event.getAgent();
        log.info("agent onAgentCreateEvent: {}", agent.getUid());
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
        // kbaseQuickReply.setType(KbaseTypeEnum.QUICKREPLY.name());
        // kbaseQuickReply.setOrgUid(agent.getOrgUid());
        // kbaseQuickReply.setAgentUid(agent.getUid());
        knowledgebaseService.create(kbaseQuickReply);
    }

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("agent onMqttConnectedEvent uid {}, clientId {}", uid, clientId);
        agentService.updateConnect(uid, true);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("agent onMqttDisconnectedEvent uid {}, clientId {}", uid, clientId);
        agentService.updateConnect(uid, false);
    }

    // 更新agent在线状态
    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("agent QuartzOneMinEvent");
        List<AgentEntity> agents = agentService.findAllConnected();
        Set<String> userIds = mqttConnectionService.getConnectedUserUids();
        // 遍历agents，判断是否在线，如果不在，则更新为离线状态
        for (AgentEntity agent : agents) {
            String userUid = agent.getUserUid();
            if (!userIds.contains(userUid)) {
                log.info("agent updateConnect uid {} offline", userUid);
                agentService.updateConnect(userUid, false);
            }
        }
        // 遍历userIds，更新为在线状态
        for (String userUid : userIds) {
            agentService.updateConnect(userUid, true);            
        }
        // TODO: 查询超时未回复会话
    }

    // 客服接待数量发生变化，增加接待数量，发送欢迎语
    @EventListener
    public void onThreadAcceptEvent(ThreadAcceptEvent event) {
        // log.info("agent onThreadAcceptEvent: {}", event);
        ThreadEntity thread = event.getThread();
        String agentString = thread.getAgent();
        log.info("agent onThreadAcceptEvent: {}", agentString);
        UserProtobuf agentProtobuf = JSON.parseObject(agentString, UserProtobuf.class);
        Optional<AgentEntity> agentOptional = agentService.findByUid(agentProtobuf.getUid());
        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            // agent.increaseThreadCount();
            // agentService.save(agent);
            // 发送欢迎语
            String content = agent.getServiceSettings().getWelcomeTip();
            MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(content, thread);
            messageSendService.sendProtobufMessage(messageProtobuf);
        } else {
            log.error("agent not found");
        }
    }


    
}
