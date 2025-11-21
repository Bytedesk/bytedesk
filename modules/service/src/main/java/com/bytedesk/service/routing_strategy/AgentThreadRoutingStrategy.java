/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 07:03:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueAutoAssignService;
import com.bytedesk.service.queue.QueueAutoAssignTriggerSource;
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
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.service.utils.WelcomeContentUtils;
import com.bytedesk.core.message.content.QueueContent;
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
    private final PresenceFacadeService presenceFacadeService;
    private final QueueAutoAssignService queueAutoAssignService;

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
        long startTime = System.currentTimeMillis();
        log.info("开始创建客服线程 - visitorUid: {}, agentUid: {}",
                visitorRequest.getUid(), visitorRequest.getSid());

        // 1. 验证和获取客服信息
        log.debug("步骤1: 开始获取客服信息 - agentUid: {}", visitorRequest.getSid());
        AgentEntity agentEntity = getAgentEntity(visitorRequest.getSid());
        log.info("步骤1完成: 成功获取客服信息 - agentUid: {}, 最大接待数: {}, 在线且可用: {}",
                agentEntity.getUid(), agentEntity.getMaxThreadCount(),
                presenceFacadeService.isAgentOnlineAndAvailable(agentEntity));

        // 2. 处理现有线程或创建新线程
        log.debug("步骤2: 开始处理线程创建或获取");
        String topic = TopicUtils.formatOrgAgentThreadTopic(visitorRequest.getSid(), visitorRequest.getUid());
        log.debug("生成线程主题: {}", topic);
        ThreadEntity thread = getOrCreateThread(visitorRequest, agentEntity, topic);
        log.info("步骤2完成: 线程处理完成 - threadUid: {}, 状态: {}, 是否新建: {}",
                thread.getUid(), thread.getStatus(), thread.isNew());

        // 3. 如果是已存在的线程，直接返回相应消息
        if (isExistingActiveThread(thread)) {
            log.info("检测到现有活跃线程，直接返回 - threadUid: {}, 状态: {}",
                    thread.getUid(), thread.getStatus());
            MessageProtobuf result = handleExistingThread(visitorRequest, thread, agentEntity);
            log.info("创建客服线程完成(现有线程) - 总耗时: {}ms", System.currentTimeMillis() - startTime);
            return result;
        }

        // 4. 新线程处理：加入队列并根据客服状态路由
        log.debug("步骤4: 开始新线程路由处理");
        MessageProtobuf result = routeNewThread(thread, agentEntity, visitorRequest);
        log.info("创建客服线程完成(新线程) - threadUid: {}, 总耗时: {}ms",
                thread.getUid(), System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 获取或创建线程
     */
    private ThreadEntity getOrCreateThread(VisitorRequest visitorRequest, AgentEntity agentEntity, String topic) {
        long startTime = System.currentTimeMillis();
        log.debug("开始获取或创建线程 - topic: {}, visitorUid: {}, agentUid: {}",
                topic, visitorRequest.getUid(), agentEntity.getUid());

        try {
            // 当强制新建会话时，直接创建新会话，跳过复用逻辑
            if (Boolean.TRUE.equals(visitorRequest.getForceNewThread())) {
                log.debug("forceNewThread=true, 创建新的客服线程 - topic: {}", topic);
                ThreadEntity newThread = visitorThreadService.createAgentThread(visitorRequest, agentEntity, topic);
                log.info("新线程创建完成(强制) - threadUid: {}, 总耗时: {}ms",
                        newThread.getUid(), System.currentTimeMillis() - startTime);
                return newThread;
            }

            // 查询现有线程
            long dbStartTime = System.currentTimeMillis();
            log.debug("开始查询现有线程 - topic: {}", topic);

            Optional<ThreadEntity> existingThread = threadRestService.findFirstByTopic(topic);
            log.debug("线程查询完成 - 耗时: {}ms", System.currentTimeMillis() - dbStartTime);

            if (existingThread.isPresent()) {
                ThreadEntity thread = existingThread.get();
                log.info("发现现有线程 - threadUid: {}, 状态: {}, 创建时间: {}, 查询耗时: {}ms",
                        thread.getUid(), thread.getStatus(), thread.getCreatedAt(),
                        System.currentTimeMillis() - dbStartTime);

                // 处理不同状态的现有线程
                if (thread.isNew() || thread.isChatting() || thread.isQueuing()) {
                    log.debug("现有线程状态可直接使用 - 状态: {}", thread.getStatus());
                    return thread;
                } else if (thread.isOffline() && !presenceFacadeService.isAgentOnlineAndAvailable(agentEntity)) {
                    log.debug("客服离线且线程离线状态，继续使用现有线程");
                    return thread;
                }
                log.debug("现有线程状态不符合条件，将创建新线程 - 当前状态: {}, 在线且可用: {}",
                        thread.getStatus(), presenceFacadeService.isAgentOnlineAndAvailable(agentEntity));
            } else {
                log.debug("未找到现有线程，将创建新线程");
            }

            // 创建新线程
            log.debug("开始创建新线程 - topic: {}", topic);
            long createStartTime = System.currentTimeMillis();
            ThreadEntity newThread = visitorThreadService.createAgentThread(visitorRequest, agentEntity, topic);
            log.info("新线程创建完成 - threadUid: {}, 创建耗时: {}ms, 总耗时: {}ms",
                    newThread.getUid(), System.currentTimeMillis() - createStartTime,
                    System.currentTimeMillis() - startTime);
            return newThread;

        } catch (Exception e) {
            log.error("获取或创建线程失败 - topic: {}, visitorUid: {}, agentUid: {}, 错误: {}, 耗时: {}ms",
                    topic, visitorRequest.getUid(), agentEntity.getUid(), e.getMessage(),
                    System.currentTimeMillis() - startTime, e);
            throw new RuntimeException("Failed to get or create thread", e);
        }
    }

    /**
     * 检查是否为已存在的活跃线程
     */
    private boolean isExistingActiveThread(ThreadEntity thread) {
        log.debug("检查线程活跃状态 - threadUid: {}, 状态: {}", thread.getUid(), thread.getStatus());
        boolean isActive = thread.isChatting() || thread.isQueuing();
        log.debug("线程活跃状态检查结果 - threadUid: {}, 是否活跃: {}, 是否聊天中: {}, 是否排队中: {}",
                thread.getUid(), isActive, thread.isChatting(), thread.isQueuing());
        return isActive;
    }

    /**
     * 处理已存在的线程
     */
    private MessageProtobuf handleExistingThread(VisitorRequest request, ThreadEntity thread, AgentEntity agentEntity) {
        if (thread.isChatting()) {
            // 重新初始化会话额外信息
            ThreadEntity updatedThread = visitorThreadService.reInitAgentThreadExtra(request, thread, agentEntity);
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
        log.debug("开始新线程路由 - threadUid: {}, agentUid: {}", thread.getUid(), agentEntity.getUid());

        // 加入队列
        QueueService.QueueEnqueueResult enqueueResult = queueService.enqueueAgentWithResult(thread, agentEntity, visitorRequest);
        QueueMemberEntity queueMemberEntity = enqueueResult.queueMember();

        // 根据客服状态路由
        log.debug("开始根据客服状态进行路由 - 可用状态: {}", agentEntity.isAvailable());
        if (presenceFacadeService.isAgentOnlineAndAvailable(agentEntity)) {
            log.info("客服在线且可用，路由到在线客服处理");
            return routeOnlineAgent(thread, agentEntity, queueMemberEntity);
        } else {
            log.info("客服不可用，路由到离线处理 -  可用状态: {}", agentEntity.isAvailable());
            return handleOfflineAgent(thread, agentEntity, queueMemberEntity);
        }
    }

    /**
     * 路由在线客服
     */
    private MessageProtobuf routeOnlineAgent(ThreadEntity thread, AgentEntity agentEntity, QueueMemberEntity queueMemberEntity) {
        long startTime = System.currentTimeMillis();
        log.info("开始在线客服路由处理 - threadUid: {}, agentUid: {}, agentNickname: {}",
                thread.getUid(), agentEntity.getUid(), agentEntity.getNickname());

        // 检查是否达到最大接待人数
        int currentChattingCount = queueMemberEntity.getAgentQueue().getChattingCount();
        int maxThreadCount = agentEntity.getMaxThreadCount();

        log.info("检查客服接待容量 - 当前接待: {}, 最大接待: {}, agentUid: {}, 队列ID: {}",
                currentChattingCount, maxThreadCount, agentEntity.getUid(), queueMemberEntity.getAgentQueue().getUid());

        MessageProtobuf result;
        if (currentChattingCount < maxThreadCount) {
            log.info("客服容量充足，直接分配给客服 - agentUid: {}, 剩余容量: {}",
                    agentEntity.getUid(), maxThreadCount - currentChattingCount);
            result = handleAvailableAgent(thread, agentEntity, queueMemberEntity);
        } else {
            log.info("客服已达最大接待数，线程进入排队 - agentUid: {}, 当前排队数: {}",
                    agentEntity.getUid(), queueMemberEntity.getAgentQueue().getQueuingCount());
            result = handleQueuedAgent(thread, agentEntity, queueMemberEntity);
        }

        log.info("在线客服路由处理完成 - threadUid: {}, agentUid: {}, 处理耗时: {}ms",
                thread.getUid(), agentEntity.getUid(), System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 处理可用客服（客服在线且未达到最大接待人数）
     */
    private MessageProtobuf handleAvailableAgent(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理可用客服分配 - threadUid: {}, agentUid: {}, agentNickname: {}",
                threadFromRequest.getUid(), agent.getUid(), agent.getNickname());

        try {
            validateThread(threadFromRequest, "handle available agent");
            Assert.notNull(agent, "AgentEntity must not be null");
            Assert.notNull(queueMemberEntity, "QueueMemberEntity must not be null");

            // 获取并更新线程状态
            log.debug("获取最新线程状态并更新为聊天状态");
            ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());
            String tip = getAgentWelcomeMessage(agent);
            WelcomeContent wc = WelcomeContentUtils.buildAgentWelcomeContent(agent, tip);
            String jsonWelcome = wc != null ? wc.toJson() : null;
            thread.setChatting().setContent(jsonWelcome);
            log.debug("线程状态更新完成 - 状态: {}, 欢迎消息长度: {}",
                    thread.getStatus(), jsonWelcome != null ? jsonWelcome.length() : 0);

            // 保存线程
            long saveStartTime = System.currentTimeMillis();
            log.debug("开始保存线程状态");
            ThreadEntity savedThread = saveThread(thread);
            log.debug("线程保存完成 - 耗时: {}ms", System.currentTimeMillis() - saveStartTime);

            // 更新队列成员状态
            log.debug("开始更新队列成员状态为已接受");
            updateQueueMemberForAcceptance(queueMemberEntity);
            log.debug("队列成员状态更新完成");

            // 发布事件
            log.debug("开始发布线程事件");
            publishThreadEvents(savedThread);
            log.debug("线程事件发布完成");

            // 发送欢迎消息
            log.debug("开始发送欢迎消息");
            long msgStartTime = System.currentTimeMillis();
            MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(wc, savedThread);
            messageSendService.sendProtobufMessage(messageProtobuf);
            log.info("可用客服处理完成 - threadUid: {}, agentUid: {}, 消息发送耗时: {}ms, 总处理耗时: {}ms",
                    savedThread.getUid(), agent.getUid(), System.currentTimeMillis() - msgStartTime,
                    System.currentTimeMillis() - startTime);

            return messageProtobuf;

        } catch (IllegalArgumentException e) {
            log.error("可用客服处理失败，参数错误 - threadUid: {}, agentUid: {}, 错误: {}",
                    threadFromRequest.getUid(), agent.getUid(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("可用客服处理失败，系统异常 - threadUid: {}, agentUid: {}, 错误: {}, 耗时: {}ms",
                    threadFromRequest.getUid(), agent.getUid(), e.getMessage(),
                    System.currentTimeMillis() - startTime, e);
            throw new RuntimeException("Failed to handle available agent", e);
        }
    }

    /**
     * 处理排队客服（客服在线但已达到最大接待人数）
     */
    private MessageProtobuf handleQueuedAgent(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理客服排队情况 - threadUid: {}, agentUid: {}, agentNickname: {}",
                threadFromRequest.getUid(), agent.getUid(), agent.getNickname());

        validateThread(threadFromRequest, "handle queued agent");

        // 获取并更新线程状态
        log.debug("获取最新线程状态用于排队处理");
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());

        log.debug("生成排队消息内容");
        int queuingCount = queueMemberEntity.getAgentQueue().getQueuingCount();
        String queueContentText = generateAgentQueueMessage(queueMemberEntity);
        QueueContent.QueueContentBuilder<?, ?> builder = QueueContent.builder()
                .content(queueContentText)
                .position(queueMemberEntity.getQueueNumber())
                .queueSize(queuingCount)
                .serverTimestamp(System.currentTimeMillis());
        if (queuingCount > 0) {
            int estimatedMinutes = queuingCount * ESTIMATED_WAIT_TIME_PER_PERSON;
            builder.waitSeconds(estimatedMinutes * 60)
                    .estimatedWaitTime("约" + estimatedMinutes + "分钟");
        } else {
            builder.waitSeconds(0).estimatedWaitTime("即将开始");
        }
        QueueContent qc = builder.build();
        thread.setQueuing().setContent(qc.toJson());
        log.debug("线程状态设置为排队 - threadUid: {}, 排队消息长度: {}",
                thread.getUid(), queueContentText != null ? queueContentText.length() : 0);

        // 保存线程
        log.debug("保存排队状态的线程");
        ThreadEntity savedThread = saveThread(thread);
        log.debug("排队线程保存完成 - threadUid: {}", savedThread.getUid());

        // 发布事件
        log.debug("发布排队线程相关事件");
        publishThreadEvents(savedThread);

        // 发送排队消息
        log.debug("开始发送排队消息");
        long msgStartTime = System.currentTimeMillis();
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(qc, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        log.info("排队消息发送完成 - threadUid: {}, 消息发送耗时: {}ms, 总处理耗时: {}ms",
                savedThread.getUid(), System.currentTimeMillis() - msgStartTime,
                System.currentTimeMillis() - startTime);

        return messageProtobuf;
    }

    /**
     * 处理离线客服
     */
    private MessageProtobuf handleOfflineAgent(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理离线客服情况 - threadUid: {}, agentUid: {}, agentNickname: {}, presenceOnline: {}",
                threadFromRequest.getUid(), agent.getUid(), agent.getNickname(),
                presenceFacadeService.isAgentOnline(agent));

        validateThread(threadFromRequest, "handle offline agent");

        // 获取并更新线程状态
        log.debug("获取最新线程状态用于离线处理");
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());

        log.debug("生成离线消息内容");
        String offlineContent = getAgentOfflineMessage(agent);
        thread.setOffline().setContent(offlineContent);
        log.debug("线程状态设置为离线 - threadUid: {}, 离线消息长度: {}",
                thread.getUid(), offlineContent != null ? offlineContent.length() : 0);

        // 保存线程
        log.debug("保存离线状态的线程");
        ThreadEntity savedThread = saveThread(thread);
        log.debug("离线线程保存完成 - threadUid: {}", savedThread.getUid());

        // 更新队列状态
        log.debug("更新队列成员离线状态 - queueMemberUid: {}", queueMemberEntity.getUid());
        queueMemberEntity.setAgentOffline(true);
        QueueMemberEntity savedQueueMember = queueMemberRestService.save(queueMemberEntity);
        log.debug("队列成员离线状态更新完成 - queueMemberUid: {}", savedQueueMember.getUid());

        // 创建离线消息
        log.debug("创建离线消息实体");
        long msgCreateStartTime = System.currentTimeMillis();
        MessageEntity message = ThreadMessageUtil.getAgentThreadOfflineMessage(offlineContent, savedThread);
        MessageEntity savedMessage = messageRestService.save(message);
        log.debug("离线消息实体创建完成 - messageUid: {}, 创建耗时: {}ms",
                savedMessage.getUid(), System.currentTimeMillis() - msgCreateStartTime);

        // 发送离线消息
        log.debug("开始发送离线消息");
        long msgSendStartTime = System.currentTimeMillis();
        MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(savedMessage, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        log.info("离线消息发送完成 - threadUid: {}, 发送耗时: {}ms",
                savedThread.getUid(), System.currentTimeMillis() - msgSendStartTime);

        // 发布事件
        log.debug("发布离线线程相关事件");
        publishThreadEvents(savedThread);

        log.info("离线客服处理完成 - threadUid: {}, agentUid: {}, 总处理耗时: {}ms",
                savedThread.getUid(), agent.getUid(), System.currentTimeMillis() - startTime);
        return messageProtobuf;
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取客服实体
     */
    private AgentEntity getAgentEntity(String agentUid) {
        long startTime = System.currentTimeMillis();
        log.debug("开始获取客服实体 - agentUid: {}", agentUid);

        try {
            validateUid(agentUid, "Agent");

            log.debug("开始查询客服实体 - agentUid: {}", agentUid);
            Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);

            if (!agentOptional.isPresent()) {
                log.error("客服实体不存在 - agentUid: {}", agentUid);
                throw new IllegalArgumentException("Agent uid " + agentUid + " not found");
            }

            AgentEntity agent = agentOptional.get();
            log.info("客服实体获取成功 - agentUid: {}, nickname: {}, 可用状态: {}, 最大接待数: {}, 查询耗时: {}ms",
                    agent.getUid(), agent.getNickname(), agent.isAvailable(),
                    agent.getMaxThreadCount(), System.currentTimeMillis() - startTime);
            return agent;

        } catch (IllegalArgumentException e) {
            log.error("客服实体获取失败，参数错误 - agentUid: {}, 错误: {}", agentUid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("客服实体获取失败，系统异常 - agentUid: {}, 错误: {}, 耗时: {}ms",
                    agentUid, e.getMessage(), System.currentTimeMillis() - startTime, e);
            throw new RuntimeException("Failed to get agent entity: " + agentUid, e);
        }
    }

    /**
     * 获取客服欢迎消息
     */
    private String getAgentWelcomeMessage(AgentEntity agent) {
        log.debug("获取客服欢迎消息 - agentUid: {}", agent.getUid());

        String customMessage = null;
        try {
            customMessage = agent.getSettings() != null && agent.getSettings().getServiceSettings() != null
                    ? agent.getSettings().getServiceSettings().getWelcomeTip()
                    : null;
            log.debug("客服自定义欢迎消息 - agentUid: {}, 消息长度: {}",
                    agent.getUid(), customMessage != null ? customMessage.length() : 0);
        } catch (Exception e) {
            log.warn("获取客服自定义欢迎消息失败 - agentUid: {}, 错误: {}", agent.getUid(), e.getMessage());
        }

        String welcomeMessage = getValidWelcomeMessage(customMessage);
        log.info("客服欢迎消息获取完成 - agentUid: {}, 最终消息长度: {}",
                agent.getUid(), welcomeMessage != null ? welcomeMessage.length() : 0);
        return welcomeMessage;
    }

    /**
     * 获取客服离线消息
     */
    private String getAgentOfflineMessage(AgentEntity agent) {
        log.debug("获取客服离线消息 - agentUid: {}", agent.getUid());

        String customMessage = null;
        try {
            customMessage = agent.getSettings() != null && agent.getSettings().getMessageLeaveSettings() != null
                    ? agent.getSettings().getMessageLeaveSettings().getMessageLeaveTip()
                    : null;
            log.debug("客服自定义离线消息 - agentUid: {}, 消息长度: {}",
                    agent.getUid(), customMessage != null ? customMessage.length() : 0);
        } catch (Exception e) {
            log.warn("获取客服自定义离线消息失败 - agentUid: {}, 错误: {}", agent.getUid(), e.getMessage());
        }

        String offlineMessage = getValidOfflineMessage(customMessage);
        log.info("客服离线消息获取完成 - agentUid: {}, 最终消息长度: {}",
                agent.getUid(), offlineMessage != null ? offlineMessage.length() : 0);
        return offlineMessage;
    }

    /**
     * 生成客服排队消息
     */
    private String generateAgentQueueMessage(QueueMemberEntity queueMemberEntity) {
        log.debug("开始生成客服排队消息 - queueMemberUid: {}", queueMemberEntity.getUid());

        int queuingCount = queueMemberEntity.getAgentQueue().getQueuingCount();
        log.debug("获取队列信息 - queueUid: {}, 当前排队数: {}",
                queueMemberEntity.getAgentQueue().getUid(), queuingCount);

        String queueMessage = generateQueueMessage(queuingCount);
        log.info("客服排队消息生成完成 - queueMemberUid: {}, 排队数: {}, 消息长度: {}",
                queueMemberEntity.getUid(), queuingCount, queueMessage != null ? queueMessage.length() : 0);
        return queueMessage;
    }

    /**
     * 更新队列成员接受状态
     */
    private void updateQueueMemberForAcceptance(QueueMemberEntity queueMemberEntity) {
        // long startTime = System.currentTimeMillis();
        log.debug("开始更新队列成员接受状态 - queueMemberUid: {}, threadUid: {}",
                queueMemberEntity.getUid(), queueMemberEntity.getThread().getUid());

        ZonedDateTime acceptTime = BdDateUtils.now();
        queueMemberEntity.setAgentAcceptedAt(acceptTime);
        queueMemberEntity.setAgentAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());

        log.debug("设置队列成员接受状态 - queueMemberUid: {}, 接受时间: {}, 接受类型: {}",
                queueMemberEntity.getUid(), acceptTime, QueueMemberAcceptTypeEnum.AUTO.name());

        queueMemberRestService.save(queueMemberEntity);
        // log.info("队列成员接受状态更新完成 - queueMemberUid: {}, 更新耗时: {}ms",
        // savedEntity.getUid(), System.currentTimeMillis() - startTime);
    }

    /**
     * 发布线程相关事件
     */
    private void publishThreadEvents(ThreadEntity savedThread) {
        log.debug("开始发布线程相关事件 - threadUid: {}, 状态: {}", savedThread.getUid(), savedThread.getStatus());

        try {
            // 发布添加主题事件
            log.debug("发布ThreadAddTopicEvent事件 - threadUid: {}, topic: {}",
                    savedThread.getUid(), savedThread.getTopic());
            bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, savedThread));

            // 发布线程处理创建事件
            log.debug("发布ThreadProcessCreateEvent事件 - threadUid: {}", savedThread.getUid());
            bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        } catch (Exception e) {
            log.error("发布线程事件失败 - threadUid: {}, 错误信息: {}", savedThread.getUid(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取客服继续对话消息
     */
    private MessageProtobuf getAgentContinueMessage(ThreadEntity thread) {
        log.debug("生成客服继续对话消息 - threadUid: {}", thread.getUid());

        UserProtobuf user = thread.getAgentProtobuf();
        if (user == null) {
            log.warn("线程中未找到客服信息 - threadUid: {}", thread.getUid());
            throw new IllegalStateException("Thread agent protobuf is null");
        }

        log.info("客服继续对话消息生成完成 - threadUid: {}, agentUid: {}, agentNickname: {}",
                thread.getUid(), user.getUid(), user.getNickname());
        return ThreadMessageUtil.getThreadContinueMessage(user, thread);
    }

    /**
     * 获取客服排队消息
     */
    private MessageProtobuf getAgentQueuingMessage(ThreadEntity thread) {
        log.debug("生成客服排队消息 - threadUid: {}", thread.getUid());

        // 线程content可能已是结构化QueueContent JSON；尝试解析，否则构造最小QueueContent
        QueueContent qc = null;
        try {
            if (thread.getContent() != null && thread.getContent().trim().startsWith("{")) {
                qc = com.alibaba.fastjson2.JSON.parseObject(thread.getContent(), QueueContent.class);
            }
        } catch (Exception e) {
            log.debug("解析线程排队内容失败，使用降级模式 - threadUid: {}, error: {}", thread.getUid(), e.getMessage());
        }
        if (qc == null) {
            qc = QueueContent.builder()
                    .content("正在排队，请稍候...")
                    .serverTimestamp(System.currentTimeMillis())
                    .build();
        }
        return ThreadMessageUtil.getThreadQueueMessage(qc, thread);
    }

    @EventListener
    public void handleAgentStatusAutoAssign(AgentUpdateStatusEvent event) {
        AgentEntity agent = event.getAgent();
        if (!shouldTriggerAgentAutoAssign(agent)) {
            return;
        }
        queueAutoAssignService.triggerAgentAutoAssign(
                agent.getUid(),
                QueueAutoAssignTriggerSource.AGENT_STATUS_EVENT,
                estimateAvailableSlots(agent));
    }

    private boolean shouldTriggerAgentAutoAssign(AgentEntity agent) {
        return agent != null
                && StringUtils.hasText(agent.getUid())
                && presenceFacadeService.isAgentOnlineAndAvailable(agent);
    }

    private int estimateAvailableSlots(AgentEntity agent) {
        int maxThreadCount = agent.getMaxThreadCount();
        return maxThreadCount > 0 ? maxThreadCount : 1;
    }
}