/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 16:20:36
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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.service.utils.WelcomeContentUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 机器人线程路由策略
 * 
 * <p>
 * 功能特点：
 * - 纯机器人接待，不支持转人工
 * - 自动创建和管理机器人会话
 * - 支持会话复用和重新初始化
 * 
 * <p>
 * 处理流程：
 * 1. 验证机器人配置
 * 2. 检查现有会话
 * 3. 创建或复用会话
 * 4. 发送欢迎消息
 * 
 * @author jackning 270580156@qq.com
 * @since 1.0.0
 */
@Slf4j
@Component("robotThreadStrategy")
@AllArgsConstructor
public class RobotThreadRoutingStrategy extends AbstractThreadRoutingStrategy {

    private final RobotRestService robotRestService;
    private final ThreadRestService threadRestService;
    private final VisitorThreadService visitorThreadService;
    private final QueueService queueService;
    private final QueueMemberRestService queueMemberRestService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageRestService messageRestService;

    @Override
    protected ThreadRestService getThreadRestService() {
        return threadRestService;
    }

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return executeWithExceptionHandling("create robot thread", visitorRequest.getSid(),
                () -> createRobotThread(visitorRequest));
    }

    /**
     * 创建机器人会话
     * 
     * @param request 访客请求
     * @return 消息协议对象
     */
    public MessageProtobuf createRobotThread(VisitorRequest request) {
        // 1. 验证和获取机器人配置
        RobotEntity robotEntity = getRobotEntity(request.getSid());

        // 2. 生成会话主题并检查现有会话
        String topic = TopicUtils.formatOrgRobotThreadTopic(robotEntity.getUid(), request.getUid());
        ThreadEntity thread = getOrCreateRobotThread(request, robotEntity, topic);

        // 3. 处理现有活跃会话
        if (isExistingRobotThread(thread)) {
            return handleExistingRobotThread(robotEntity, thread);
        }

        // 4. 处理新会话或重新激活的会话
        return processNewRobotThread(request, thread, robotEntity);
    }

    /**
     * 获取机器人实体
     */
    private RobotEntity getRobotEntity(String robotUid) {
        validateUid(robotUid, "Robot");

        return robotRestService.findByUid(robotUid)
                .orElseThrow(() -> {
                    log.error("Robot uid {} not found", robotUid);
                    return new IllegalArgumentException("Robot uid " + robotUid + " not found");
                });
    }

    /**
     * 获取或创建机器人会话
     */
    private ThreadEntity getOrCreateRobotThread(VisitorRequest request, RobotEntity robotEntity, String topic) {
        // 当强制新建会话时，直接创建新会话，跳过复用逻辑
        if (Boolean.TRUE.equals(request.getForceNewThread())) {
            log.debug("forceNewThread=true, creating new robot thread for topic: {}", topic);
            return visitorThreadService.createRobotThread(request, robotEntity, topic);
        }

        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);

        if (threadOptional.isPresent()) {
            ThreadEntity existingThread = threadOptional.get();

            // 检查现有会话状态
            if (existingThread.isNew()) {
                log.debug("Found new robot thread: {}", topic);
                return existingThread;
            } else if (existingThread.isRoboting()) {
                log.debug("Found existing roboting thread: {}", topic);
                // 重新初始化会话用于测试
                return visitorThreadService.reInitRobotThreadExtra(request, existingThread, robotEntity);
            }
        }

        // 创建新会话
        log.debug("Creating new robot thread for topic: {}", topic);
        return visitorThreadService.createRobotThread(request, robotEntity, topic);
    }

    /**
     * 检查是否为现有的机器人会话
     */
    private boolean isExistingRobotThread(ThreadEntity thread) {
        return thread.isRoboting() && !thread.isNew();
    }

    /**
     * 处理现有的机器人会话
     */
    private MessageProtobuf handleExistingRobotThread(RobotEntity robotEntity, ThreadEntity thread) {
        log.info("Continuing existing robot thread: {}", thread.getUid());
        return getRobotContinueMessage(robotEntity, thread);
    }

    /**
     * 处理新的机器人会话
     */
    private MessageProtobuf processNewRobotThread(VisitorRequest request, ThreadEntity thread,
            RobotEntity robotEntity) {
        // 1. 加入队列
        UserProtobuf robotProtobuf = robotEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robotProtobuf, request);
        log.info("Robot enqueued to queue: {}", queueMemberEntity.getUid());

        // 2. 配置线程状态
        String tip = getRobotWelcomeMessage(robotEntity);
    WelcomeContent wc = WelcomeContentUtils.buildRobotWelcomeContent(robotEntity, tip);
        thread.setRoboting().setContent(wc != null ? wc.toJson() : null);

        // 3. 设置机器人信息
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robotEntity);
        thread.setRobot(robotString);

        // 4. 保存线程
        ThreadEntity savedThread = saveThread(thread);

        // 5. 更新队列状态
        updateQueueMemberForRobot(queueMemberEntity);

        // 6. 发布事件
        publishRobotThreadEvent(savedThread);

        // 7. 创建并保存欢迎消息
        return createAndSaveWelcomeMessage(wc, savedThread);
    }

    /**
     * 获取机器人欢迎消息
     */
    private String getRobotWelcomeMessage(RobotEntity robotEntity) {
        String customMessage = robotEntity.getSettings() != null
                && robotEntity.getSettings().getServiceSettings() != null
                        ? robotEntity.getSettings().getServiceSettings().getWelcomeTip()
                        : null;
        return getValidWelcomeMessage(customMessage);
    }

    /**
     * 更新队列成员状态为机器人自动接受
     */
    private void updateQueueMemberForRobot(QueueMemberEntity queueMemberEntity) {
        try {
            queueMemberEntity.robotAutoAcceptThread();
            queueMemberRestService.save(queueMemberEntity);
            log.debug("Updated queue member status for robot auto-accept: {}", queueMemberEntity.getUid());
        } catch (Exception e) {
            log.error("Failed to update queue member for robot auto-accept: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update queue member status", e);
        }
    }

    /**
     * 发布机器人线程事件
     */
    private void publishRobotThreadEvent(ThreadEntity savedThread) {
        try {
            applicationEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
            log.debug("Published thread process create event for robot thread: {}", savedThread.getUid());
        } catch (Exception e) {
            log.warn("Failed to publish thread event for robot thread {}: {}", savedThread.getUid(), e.getMessage());
        }
    }

    /**
     * 创建并保存欢迎消息
     */
    private MessageProtobuf createAndSaveWelcomeMessage(WelcomeContent wc, ThreadEntity thread) {
        try {
            MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(wc, thread);
            messageRestService.save(message);

            MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
            log.debug("Created robot welcome message for thread: {}", thread.getUid());

            return messageProtobuf;
        } catch (Exception e) {
            log.error("Failed to create welcome message for robot thread {}: {}", thread.getUid(), e.getMessage(), e);
            throw new RuntimeException("Failed to create welcome message", e);
        }
    }

    /**
     * 获取机器人继续对话消息
     */
    private MessageProtobuf getRobotContinueMessage(RobotEntity robotEntity, @Nonnull ThreadEntity thread) {
        validateThread(thread, "get robot continue message");

        String tip = getRobotWelcomeMessage(robotEntity);
        WelcomeContent wc = buildRobotWelcomeContent(robotEntity, tip);
        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(wc, thread);

        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    /**
     * 根据机器人设置构建结构化 WelcomeContent
     */
    // 已迁移到 WelcomeContentUtils
    @Deprecated
    private WelcomeContent buildRobotWelcomeContent(RobotEntity robotEntity, String tip) {
        return WelcomeContentUtils.buildRobotWelcomeContent(robotEntity, tip);
    }
}
