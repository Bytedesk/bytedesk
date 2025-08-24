/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 16:35:24
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

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 客服线程路由策略
 * 负责处理一对一人工客服的线程创建和路由逻辑
 * 
 * @author jackning 270580156@qq.com
 */
@Slf4j
@Component("agentThreadStrategy")
@AllArgsConstructor
public class AgentThreadRoutingStrategy extends AbstractThreadRoutingStrategy {

    private final AgentRestService agentRestService;
    private final ThreadRestService threadRestService;
    private final VisitorThreadService visitorThreadService;
    private final IMessageSendService messageSendService;
    private final QueueService queueService;
    private final QueueMemberRestService queueMemberRestService;
    private final MessageRestService messageRestService;
    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    protected ThreadRestService getThreadRestService() {
        return threadRestService;
    }

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return executeWithExceptionHandling("create agent thread", visitorRequest.getSid(),
                () -> createAgentThread(visitorRequest));
    }

    /**
     * 创建客服线程
     * 一对一人工客服，不支持机器人接待
     */
    public MessageProtobuf createAgentThread(VisitorRequest visitorRequest) {
        // 1. 验证和获取客服信息
        AgentEntity agentEntity = getAgentEntity(visitorRequest.getSid());
        
        // 2. 处理现有线程或创建新线程
        String topic = TopicUtils.formatOrgAgentThreadTopic(visitorRequest.getSid(), visitorRequest.getUid());
        ThreadEntity thread = getOrCreateThread(visitorRequest, agentEntity, topic);
        
        // 3. 如果是已存在的线程，直接返回相应消息
        if (isExistingActiveThread(thread)) {
            return handleExistingThread(thread, agentEntity);
        }
        
        // 4. 新线程处理：加入队列并根据客服状态路由
        return routeNewThread(thread, agentEntity, visitorRequest);
    }

    /**
     * 获取或创建线程
     */
    private ThreadEntity getOrCreateThread(VisitorRequest visitorRequest, AgentEntity agentEntity, String topic) {
        Optional<ThreadEntity> existingThread = threadRestService.findFirstByTopic(topic);
        
        if (existingThread.isPresent()) {
            ThreadEntity thread = existingThread.get();
            
            // 处理不同状态的现有线程
            if (thread.isNew() || thread.isChatting() || thread.isQueuing()) {
                return thread;
            } else if (thread.isOffline() && !agentEntity.isConnectedAndAvailable()) {
                return thread;
            }
        }
        
        // 创建新线程
        return visitorThreadService.createAgentThread(visitorRequest, agentEntity, topic);
    }

    /**
     * 检查是否为已存在的活跃线程
     */
    private boolean isExistingActiveThread(ThreadEntity thread) {
        return thread.isChatting() || thread.isQueuing();
    }

    /**
     * 处理已存在的线程
     */
    private MessageProtobuf handleExistingThread(ThreadEntity thread, AgentEntity agentEntity) {
        if (thread.isChatting()) {
            // 重新初始化会话额外信息
            ThreadEntity updatedThread = visitorThreadService.reInitAgentThreadExtra(thread, agentEntity);
            log.info("Already have a processing thread {}", updatedThread.getAgent());
            return getAgentContinueMessage(updatedThread);
        } else if (thread.isQueuing()) {
            return getAgentQueuingMessage(thread);
        }
        throw new IllegalStateException("Unexpected thread state: " + thread.getStatus());
    }

    /**
     * 路由新线程
     */
    private MessageProtobuf routeNewThread(ThreadEntity thread, AgentEntity agentEntity, VisitorRequest visitorRequest) {
        // 加入队列
        UserProtobuf agent = agentEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService.enqueueAgent(thread, agent, visitorRequest);
        log.info("Enqueued to queue {}", queueMemberEntity.getUid());
        
        // 根据客服状态路由
        if (agentEntity.isConnectedAndAvailable()) {
            return routeOnlineAgent(thread, agentEntity, queueMemberEntity);
        } else {
            return handleOfflineAgent(thread, agentEntity, queueMemberEntity);
        }
    }

    /**
     * 路由在线客服
     */
    private MessageProtobuf routeOnlineAgent(ThreadEntity thread, AgentEntity agentEntity, QueueMemberEntity queueMemberEntity) {
        // 检查是否达到最大接待人数
        if (queueMemberEntity.getAgentQueue().getChattingCount() < agentEntity.getMaxThreadCount()) {
            return handleAvailableAgent(thread, agentEntity, queueMemberEntity);
        } else {
            return handleQueuedAgent(thread, agentEntity, queueMemberEntity);
        }
    }

    /**
     * 处理可用客服（客服在线且未达到最大接待人数）
     */
    private MessageProtobuf handleAvailableAgent(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        validateThread(threadFromRequest, "handle available agent");
        Assert.notNull(agent, "AgentEntity must not be null");
        Assert.notNull(queueMemberEntity, "QueueMemberEntity must not be null");
        
        // 获取并更新线程状态
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());
        String welcomeContent = getAgentWelcomeMessage(agent);
        thread.setChatting().setContent(welcomeContent);
        
        // 保存线程
        ThreadEntity savedThread = saveThread(thread);
        
        // 更新队列成员状态
        updateQueueMemberForAcceptance(queueMemberEntity);
        
        // 发布事件
        publishThreadEvents(savedThread);
        
        // 发送欢迎消息
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(welcomeContent, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        
        return messageProtobuf;
    }

    /**
     * 处理排队客服（客服在线但已达到最大接待人数）
     */
    private MessageProtobuf handleQueuedAgent(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        validateThread(threadFromRequest, "handle queued agent");

        // 获取并更新线程状态
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());
        String queueContent = generateAgentQueueMessage(queueMemberEntity);
        thread.setQueuing().setContent(queueContent);
        
        // 保存线程
        ThreadEntity savedThread = saveThread(thread);
        
        // 发布事件
        publishThreadEvents(savedThread);
        
        // 发送排队消息
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        
        return messageProtobuf;
    }

    /**
     * 处理离线客服
     */
    private MessageProtobuf handleOfflineAgent(ThreadEntity threadFromRequest, AgentEntity agent, 
            QueueMemberEntity queueMemberEntity) {
        validateThread(threadFromRequest, "handle offline agent");
        
        // 获取并更新线程状态
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());
        String offlineContent = getAgentOfflineMessage(agent);
        thread.setOffline().setContent(offlineContent);
        
        // 保存线程
        ThreadEntity savedThread = saveThread(thread);
        
        // 更新队列状态
        queueMemberEntity.setAgentOffline(true);
        queueMemberRestService.save(queueMemberEntity);
        
        // 创建离线消息
        MessageEntity message = ThreadMessageUtil.getAgentThreadOfflineMessage(offlineContent, savedThread);
        messageRestService.save(message);
        
        // 发送离线消息
        MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        
        // 发布事件
        publishThreadEvents(savedThread);
        
        return messageProtobuf;
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取客服实体
     */
    private AgentEntity getAgentEntity(String agentUid) {
        validateUid(agentUid, "Agent");
        
        return agentRestService.findByUid(agentUid)
                .orElseThrow(() -> {
                    log.error("Agent uid {} not found", agentUid);
                    return new IllegalArgumentException("Agent uid " + agentUid + " not found");
                });
    }

    /**
     * 获取客服欢迎消息
     */
    private String getAgentWelcomeMessage(AgentEntity agent) {
        String customMessage = agent.getServiceSettings().getWelcomeTip();
        return getValidWelcomeMessage(customMessage);
    }

    /**
     * 获取客服离线消息
     */
    private String getAgentOfflineMessage(AgentEntity agent) {
        String customMessage = agent.getMessageLeaveSettings().getMessageLeaveTip();
        return getValidOfflineMessage(customMessage);
    }

    /**
     * 生成客服排队消息
     */
    private String generateAgentQueueMessage(QueueMemberEntity queueMemberEntity) {
        int queuingCount = queueMemberEntity.getAgentQueue().getQueuingCount();
        return generateQueueMessage(queuingCount);
    }

    /**
     * 更新队列成员接受状态
     */
    private void updateQueueMemberForAcceptance(QueueMemberEntity queueMemberEntity) {
        queueMemberEntity.setAgentAcceptedAt(BdDateUtils.now());
        queueMemberEntity.setAgentAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
        queueMemberRestService.save(queueMemberEntity);
    }

    /**
     * 发布线程相关事件
     */
    private void publishThreadEvents(ThreadEntity savedThread) {
        bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, savedThread));
        bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
    }

    /**
     * 获取客服继续对话消息
     */
    private MessageProtobuf getAgentContinueMessage(ThreadEntity thread) {
        UserProtobuf user = thread.getAgentProtobuf();
        return ThreadMessageUtil.getThreadContinueMessage(user, thread);
    }

    /**
     * 获取客服排队消息
     */
    private MessageProtobuf getAgentQueuingMessage(ThreadEntity thread) {
        UserProtobuf user = thread.getAgentProtobuf();
        return ThreadMessageUtil.getThreadQueuingMessage(user, thread);
    }
}