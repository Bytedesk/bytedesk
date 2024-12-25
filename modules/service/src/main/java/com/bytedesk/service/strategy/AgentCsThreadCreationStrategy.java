/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 14:52:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.routing.RouteService;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;

import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jackning 270580156@qq.com
 */
@Slf4j
@Component("agentCsThreadStrategy")
@AllArgsConstructor
public class AgentCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final AgentRestService agentService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    // private final IRouteService routeService;
    private final RouteService routeService;
    
    // private final IMessageSendService messageSendService;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createAgentCsThread(visitorRequest);
    }

    // 一对一人工客服，不支持机器人接待
    public MessageProtobuf createAgentCsThread(VisitorRequest visitorRequest) {
        //
        String agentUid = visitorRequest.getSid();
        String topic = TopicUtils.formatOrgAgentThreadTopic(agentUid, visitorRequest.getUid());
        // 是否已经存在会话
        ThreadEntity thread = null;
        AgentEntity agent = null;
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent() ) {
            thread = threadOptional.get();
            // 
            if (thread.isStarted()) {
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing thread {}", topic);
                return getAgentContinueMessage(visitorRequest, threadOptional.get());
            } else if (thread.isQueuing()) {
                // 返回排队中的会话
                return getAgentQueuingMessage(visitorRequest, threadOptional.get());
            } else {
                // 关闭或者离线状态，返回初始化状态的会话
                thread = threadOptional.get().reInitAgent();
                agent = agentService.findByUid(agentUid).orElseThrow(() -> new RuntimeException("Agent uid " + agentUid + " not found"));
            }
        } else {
            // 不存在会话，创建会话
            agent = agentService.findByUid(agentUid).orElseThrow(() -> new RuntimeException("Agent uid " + agentUid + " not found"));
            thread = visitorThreadService.getAgentThread(visitorRequest, agent, topic);
        }
        // 重新初始化会话额外信息，例如客服状态等
        thread = visitorThreadService.reInitAgentThreadExtra(thread, agent);
        // 人工客服
        return routeService.routeAgent(visitorRequest, thread, agent);
    }

    private MessageProtobuf getAgentContinueMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getAgentContinueMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadContinueMessage(user, thread);
        // 重复进入，无需推送消息
        // messageSendService.sendProtobufMessage(messageProtobuf);

        return messageProtobuf;
    }

    private MessageProtobuf getAgentQueuingMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getAgentQueuingMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadQueuingMessage(user, thread);
        // 重复进入，无需推送消息
        // messageSendService.sendProtobufMessage(messageProtobuf);

        return messageProtobuf;
    }

    

}