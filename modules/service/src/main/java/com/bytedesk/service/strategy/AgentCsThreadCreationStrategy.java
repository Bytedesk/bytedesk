/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 15:02:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.strategy;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberAcceptTypeEnum;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
// import com.bytedesk.service.routing.RouteService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
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

    // private final RouteService routeService;
    private final IMessageSendService messageSendService;

    private final AgentRestService agentRestService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService;

    private final MessageRestService messageRestService;
    
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
        Optional<AgentEntity> agentOptional = agentService.findByUid(agentUid);
        if (agentOptional.isPresent()) {
            agent = agentOptional.get();
        } else {
            log.info("Agent uid {} not found", agentUid);
            throw new RuntimeException("Agent uid " + agentUid + " not found");
        }
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent() ) {
            // 
            if (threadOptional.get().isStarted()) {
                thread = threadOptional.get();
                // 重新初始化会话额外信息，例如客服状态等
                thread = visitorThreadService.reInitAgentThreadExtra(thread, agent);
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing thread {}", topic);
                return getAgentContinueMessage(visitorRequest, thread);
            } else if (threadOptional.get().isQueuing()) {
                thread = threadOptional.get();
                // 返回排队中的会话
                return getAgentQueuingMessage(visitorRequest, thread);
            }
            //  else {
            //     // 关闭或者离线状态，返回初始化状态的会话
            //     thread = thread.reInit(false);
            // }
        }
        // 
        if (thread == null) {
            // 不存在会话，创建会话
            thread = visitorThreadService.createAgentThread(visitorRequest, agent, topic);
            // 重新初始化会话额外信息，例如客服状态等
            thread = visitorThreadService.reInitAgentThreadExtra(thread, agent);
        }

        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueAgent(thread, agent, visitorRequest);
        log.info("routeAgent Enqueued to queue {}", queueMemberEntity.getQueueNickname());
        // 判断客服是否在线且接待状态
        if (agent.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            // 判断是否达到最大接待人数，如果达到则进入排队
            if (agent.canAcceptMore()) {
                // 未满则接待
                return handleAvailableAgent(thread, agent, queueMemberEntity);
            } else {
                return handleQueuedAgent(thread, agent, queueMemberEntity);
            }
        } else {
            return handleOfflineAgent(thread, agent, queueMemberEntity);
        }

        // 人工客服
        // return routeService.routeToAgent(visitorRequest, thread, agent);
        // return routeToAgent(visitorRequest, thread, agent);
    }

    private MessageProtobuf handleAvailableAgent(ThreadEntity thread, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        Assert.notNull(thread, "ThreadEntity must not be null");
        Assert.notNull(agent, "AgentEntity must not be null");
        Assert.notNull(queueMemberEntity, "QueueMemberEntity must not be null");
        
        // 未满则接待
        thread.setStarted();
        thread.setUnreadCount(1);
        thread.setContent(agent.getServiceSettings().getWelcomeTip());
        thread.setQueueNumber(queueMemberEntity.getQueueNumber());
        // 增加接待数量，待优化
        agent.increaseThreadCount();
        agentRestService.save(agent);
        // 更新排队状态，待优化
        queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
        queueMemberEntity.setAcceptTime(LocalDateTime.now());
        queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
        queueMemberRestService.save(queueMemberEntity);
        //
        thread.setRobot(false);
        threadService.save(thread);
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agent, thread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        return messageProtobuf;
    }

    private MessageProtobuf handleQueuedAgent(ThreadEntity thread, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        // 已满则排队
        // String queueTip = agent.getQueueSettings().getQueueTip();
        String content = "";
        if (queueMemberEntity.getBeforeNumber() == 0) {
            // 客服接待刚满员，下一个就是他，
            content = "请稍后，下一个就是您";
            // String content = String.format(queueTip, queueMemberEntity.getQueueNumber(),
            // queueMemberEntity.getWaitTime());
        } else {
            // 前面有排队人数
            content = " 当前排队人数：" + queueMemberEntity.getBeforeNumber() + " 大约等待时间："
                    + queueMemberEntity.getBeforeNumber() * 2 + "  分钟";
        }
        // 进入排队队列
        thread.setQueuing();
        thread.setUnreadCount(0);
        thread.setContent(content);
        thread.setQueueNumber(queueMemberEntity.getQueueNumber());
        thread.setRobot(false);
        threadService.save(thread);
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getAgentThreadQueueMessage(agent, thread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        return messageProtobuf;
    }

    private MessageProtobuf handleOfflineAgent(ThreadEntity thread, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        // 客服离线或小休不接待状态，则进入留言
        thread.setOffline();
        thread.setUnreadCount(0);
        thread.setContent(agent.getMessageLeaveSettings().getMessageLeaveTip());
        thread.setQueueNumber(queueMemberEntity.getQueueNumber());
        threadService.save(thread);
        //
        MessageEntity message = ThreadMessageUtil.getAgentThreadOfflineMessage(agent, thread);
        // 保存留言消息
        messageRestService.save(message);
        // 返回留言消息
        // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
        MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        return messageProtobuf;
    }


    private MessageProtobuf getAgentContinueMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getAgentContinueMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        return ThreadMessageUtil.getThreadContinueMessage(user, thread);
    }

    private MessageProtobuf getAgentQueuingMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getAgentQueuingMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        return ThreadMessageUtil.getThreadQueuingMessage(user, thread);
    }

    

}