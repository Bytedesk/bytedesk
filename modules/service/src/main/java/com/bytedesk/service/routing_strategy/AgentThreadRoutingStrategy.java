/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:48:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberAcceptTypeEnum;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jackning 270580156@qq.com
 */
@Slf4j
@Component("agentThreadStrategy")
@AllArgsConstructor
public class AgentThreadRoutingStrategy implements ThreadRoutingStrategy {

    private final AgentRestService agentService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    private final IMessageSendService messageSendService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService;

    private final MessageRestService messageRestService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return createAgentThread(visitorRequest);
    }

    // 一对一人工客服，不支持机器人接待
    public MessageProtobuf createAgentThread(VisitorRequest visitorRequest) {
        //
        String agentUid = visitorRequest.getSid();
        String topic = TopicUtils.formatOrgAgentThreadTopic(agentUid, visitorRequest.getUid());
        // 是否已经存在会话
        ThreadEntity thread = null;
        AgentEntity agentEntity = null;
        Optional<AgentEntity> agentOptional = agentService.findByUid(agentUid);
        if (agentOptional.isPresent()) {
            agentEntity = agentOptional.get();
        } else {
            log.info("Agent uid {} not found", agentUid);
            throw new RuntimeException("Agent uid " + agentUid + " not found");
        }
        // 是否已经存在会话
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            //
            if (threadOptional.get().isNew()) {
                thread = threadOptional.get();
            } else if ( threadOptional.get().isChatting()) {
                thread = threadOptional.get();
                // 重新初始化会话额外信息，例如客服状态等
                thread = visitorThreadService.reInitAgentThreadExtra(thread, agentEntity);
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing thread {}", topic);
                return getAgentContinueMessage(visitorRequest, thread);
            } else if (threadOptional.get().isQueuing()) {
                thread = threadOptional.get();
                // 返回排队中的会话
                return getAgentQueuingMessage(visitorRequest, thread);
            } else if (threadOptional.get().isOffline()) {
                // 返回留言状态的会话
                if (!agentEntity.isConnectedAndAvailable()) {
                    thread = threadOptional.get();
                }
            }
        }
        //
        if (thread == null) {
            // 不存在会话，创建会话
            thread = visitorThreadService.createAgentThread(visitorRequest, agentEntity, topic);
        }
        // 排队计数
        UserProtobuf agent = agentEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService.enqueueAgent(thread, agent, visitorRequest);
        log.info("routeAgent Enqueued to queue {}", queueMemberEntity.getUid());
        // 判断客服是否在线且接待状态
        if (agentEntity.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            // 判断是否达到最大接待人数，如果达到则进入排队
            if (queueMemberEntity.getAgentQueue().getQueuingCount() < agentEntity.getMaxThreadCount()) {
                // 未满则接待
                return handleAvailableAgent(thread, agentEntity, queueMemberEntity);
            } else {
                // 已满则排队
                return handleQueuedAgent(thread, agentEntity, queueMemberEntity);
            }
        } else {
            // 客服离线或小休不接待状态，则进入留言
            return handleOfflineAgent(thread, agentEntity, queueMemberEntity);
        }
    }

    private MessageProtobuf handleAvailableAgent(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        Assert.notNull(threadFromRequest, "ThreadEntity must not be null");
        Assert.notNull(agent, "AgentEntity must not be null");
        Assert.notNull(queueMemberEntity, "QueueMemberEntity must not be null");
        // 客服在线 且 接待状态
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        // 
        String content = agent.getServiceSettings().getWelcomeTip();
        if (content == null || content.isEmpty()) {
            content = "您好，请问有什么可以帮助您？";
        }
        ThreadEntity thread = threadOptional.get();
        thread.setChatting().setContent(content).setUnreadCount(1);
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            log.error("Failed to save thread {}", thread.getUid());
            throw new RuntimeException("Failed to save thread " + thread.getUid());
        }
        // 更新排队状态，待优化
        queueMemberEntity.setAgentAcceptedAt(BdDateUtils.now());
        queueMemberEntity.setAgentAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
        queueMemberRestService.save(queueMemberEntity);
        // 
        bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, savedThread));
        bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(content, thread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        // 
        return messageProtobuf;
    }

    private MessageProtobuf handleQueuedAgent(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        Assert.notNull(threadFromRequest, "ThreadEntity must not be null");

        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        ThreadEntity thread = threadOptional.get();
        // 已满则排队
        // String queueTip = agent.getQueueSettings().getQueueTip();
        String content = "";
        if (queueMemberEntity.getAgentQueue().getQueuingCount() == 0) {
            // 客服接待刚满员，下一个就是他，
            content = "请稍后，下一个就是您";
        } else {
            // 前面有排队人数
            content = " 当前排队人数：" + queueMemberEntity.getAgentQueue().getQueuingCount() + " 大约等待时间："
                    + queueMemberEntity.getAgentQueue().getQueuingCount() * 2 + "  分钟";
        }
        // 进入排队队列
        thread.setQueuing().setUnreadCount(0).setContent(content);
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            log.error("Failed to save thread {}", thread.getUid());
            throw new RuntimeException("Failed to save thread " + thread.getUid());
        }
        // 
        bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, savedThread));
        bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getAgentThreadQueueMessage(agent, thread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        // 
        return messageProtobuf;
    }

    private MessageProtobuf handleOfflineAgent(ThreadEntity threadFromRequest, AgentEntity agent, QueueMemberEntity queueMemberEntity) {
        Assert.notNull(threadFromRequest, "ThreadEntity must not be null");
        // 
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        // 
        String content = agent.getMessageLeaveSettings().getMessageLeaveTip();
        if (content == null || content.isEmpty()) {
            content = "您好，请留言，我们会尽快回复您";
        }
        ThreadEntity thread = threadOptional.get();
        // 客服离线或小休不接待状态，则进入留言
        thread.setOffline().setUnreadCount(0).setContent(content);
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            log.error("Failed to save thread {}", thread.getUid());
            throw new RuntimeException("Failed to save thread " + thread.getUid());
        }
        
        // 如果拉取的是访客的消息，会影响前端
        // 查询最新一条消息，如果距离当前时间不超过30分钟，则直接使用之前的消息，否则创建新的消息
        // Optional<MessageEntity> messageOptional = messageRestService.findLatestByThreadUid(savedThread.getUid());
        // if (messageOptional.isPresent()) {
        //     MessageEntity message = messageOptional.get();
        //     if (message.getCreatedAt().isAfter(BdDateUtils.now().minusMinutes(30))) {
        //         // 距离当前时间不超过30分钟，则直接使用之前的消息
        //         // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
        //         MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        //         messageSendService.sendProtobufMessage(messageProtobuf);
        //         return messageProtobuf;
        //     }
        // }
        // 
        queueMemberEntity.setAgentOffline(true);
        queueMemberRestService.save(queueMemberEntity);
        // 创建新的留言消息
        MessageEntity message = ThreadMessageUtil.getAgentThreadOfflineMessage(content, savedThread);
        messageRestService.save(message);
        // 返回留言消息
        // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
        MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        // 
        bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, savedThread));
        bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        // 
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