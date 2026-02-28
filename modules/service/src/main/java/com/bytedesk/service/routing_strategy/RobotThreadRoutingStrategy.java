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
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.utils.WelcomeContentUtils;
import com.bytedesk.service.visitor.VisitorCallTypeEnum;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import com.bytedesk.video.webrtc.WebrtcService;
import com.bytedesk.video.webrtc.dto.WebrtcInviteRequest;

import jakarta.annotation.Nonnull;
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
    private final WebrtcService webrtcService;

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
        VisitorCallTypeEnum callType = normalizeCallType(request);

        // 1. 验证和获取机器人配置
        RobotEntity robotEntity = getRobotEntity(request.getSid());
        ensureRealtimeAudioCapabilityIfNeeded(robotEntity, callType);

        // 2. 生成会话主题并检查现有会话
        String visitorUidForTopic = resolveVisitorUidForThreadTopic(request);
        String topic = TopicUtils.formatOrgRobotThreadTopic(robotEntity.getUid(), visitorUidForTopic);
        ThreadEntity thread = getOrCreateRobotThread(request, robotEntity, topic, callType);

        // 3. 处理现有活跃会话
        if (isExistingRobotThread(thread)) {
            return handleExistingRobotThread(request, robotEntity, thread, callType);
        }

        // 4. 处理新会话或重新激活的会话
        return processNewRobotThread(request, thread, robotEntity, callType);
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
    private ThreadEntity getOrCreateRobotThread(VisitorRequest request, RobotEntity robotEntity, String topic,
            VisitorCallTypeEnum callType) {
        // 当强制新建会话时，直接创建新会话，跳过复用逻辑
        if (Boolean.TRUE.equals(request.getForceNewThread())) {
            log.debug("forceNewThread=true, creating new robot thread for topic: {}", topic);
            ThreadEntity thread = visitorThreadService.createRobotThread(request, robotEntity, topic);
            thread.setExtra(mergeCallTypeToExtra(thread.getExtra(), callType.name()));
            return saveThread(thread);
        }

        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopicNotClosed(topic);
        if (threadOptional.isPresent()) {
            ThreadEntity existingThread = threadOptional.get();

            // 检查现有会话状态
            if (existingThread.isNew()) {
                log.debug("Found new robot thread: {}", topic);
                existingThread.setExtra(mergeCallTypeToExtra(existingThread.getExtra(), callType.name()));
                return saveThread(existingThread);
            } else if (existingThread.isRoboting()) {
                log.debug("Found existing roboting thread: {}", topic);
                // 重新初始化会话用于测试
                ThreadEntity thread = visitorThreadService.reInitRobotThreadExtra(request, existingThread, robotEntity);
                thread.setExtra(mergeCallTypeToExtra(thread.getExtra(), callType.name()));
                return saveThread(thread);
            }
        }

        // 创建新会话
        log.debug("Creating new robot thread for topic: {}", topic);
        ThreadEntity thread = visitorThreadService.createRobotThread(request, robotEntity, topic);
        thread.setExtra(mergeCallTypeToExtra(thread.getExtra(), callType.name()));
        return saveThread(thread);
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
    private MessageProtobuf handleExistingRobotThread(VisitorRequest request, RobotEntity robotEntity, ThreadEntity thread,
            VisitorCallTypeEnum callType) {
        log.info("Continuing existing robot thread: {}", thread.getUid());
        triggerAutoCallInviteIfNeeded(request, thread, robotEntity, callType);
        return getRobotContinueMessage(request, robotEntity, thread);
    }

    /**
     * 处理新的机器人会话
     */
    private MessageProtobuf processNewRobotThread(VisitorRequest request, ThreadEntity thread,
            RobotEntity robotEntity, VisitorCallTypeEnum callType) {
        // 1. 加入队列
        UserProtobuf robotProtobuf = robotEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robotProtobuf, request);
        log.info("Robot enqueued to queue: {}", queueMemberEntity.getUid());

        // 2. 配置线程状态
        String tip = getRobotWelcomeMessage(request, robotEntity);
        WelcomeContent welcomeContent = WelcomeContentUtils.buildRobotWelcomeContent(robotEntity, tip);
        String payload = welcomeContent != null ? welcomeContent.toJson() : null;
        thread.setRoboting().setContent(ThreadContent.of(MessageTypeEnum.WELCOME, tip, payload).toJson());

        // 3. 设置机器人信息（只存储基础信息，避免 prompt 过长导致字段超限）
        String robotString = ConvertAiUtils.convertToRobotProtobufBasicString(robotEntity);
        thread.setRobot(robotString);

        // 4. 保存线程
        thread.setExtra(mergeCallTypeToExtra(thread.getExtra(), callType.name()));
        ThreadEntity savedThread = saveThread(thread);

        // 5. 更新队列状态
        updateQueueMemberForRobot(queueMemberEntity);

        // 6. 发布事件
        publishRobotThreadEvent(savedThread);

        // 7. 自动触发音视频邀请并创建欢迎消息
        triggerAutoCallInviteIfNeeded(request, savedThread, robotEntity, callType);
        return createAndSaveWelcomeMessage(welcomeContent, savedThread);
    }

    /**
     * 获取机器人欢迎消息
     */
    private boolean isDraftEnabled(VisitorRequest visitorRequest) {
        return visitorRequest != null && Boolean.TRUE.equals(visitorRequest.getDraft());
    }

    private String getRobotWelcomeMessage(VisitorRequest visitorRequest, RobotEntity robotEntity) {
        String customMessage = null;
        boolean useDraft = isDraftEnabled(visitorRequest);
        if (robotEntity.getSettings() != null) {
            if (useDraft && robotEntity.getSettings().getDraftServiceSettings() != null) {
                customMessage = robotEntity.getSettings().getDraftServiceSettings().getWelcomeTip();
            }
            if (customMessage == null || customMessage.isEmpty()) {
                customMessage = robotEntity.getSettings().getServiceSettings() != null
                        ? robotEntity.getSettings().getServiceSettings().getWelcomeTip()
                        : null;
            }
        }
        return getValidWelcomeMessage(customMessage);
    }

    /**
     * 更新队列成员状态为机器人自动接受
     */
    private void updateQueueMemberForRobot(QueueMemberEntity queueMemberEntity) {
        try {
            queueMemberEntity.robotAutoAcceptThread();
            queueMemberRestService.saveAsyncBestEffort(queueMemberEntity);
            log.debug("Queued async queue member update for robot auto-accept: {}", queueMemberEntity.getUid());
        } catch (Exception e) {
            // best-effort: 不影响主流程
            log.warn("Failed to queue async update for robot auto-accept: {}", e.getMessage(), e);
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
    private MessageProtobuf getRobotContinueMessage(VisitorRequest request, RobotEntity robotEntity,
            @Nonnull ThreadEntity thread) {
        validateThread(thread, "get robot continue message");

        String tip = getRobotWelcomeMessage(request, robotEntity);
        WelcomeContent wc = WelcomeContentUtils.buildRobotWelcomeContent(robotEntity, tip);
        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(wc, thread);

        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    /**
     * 统一规范 callType，并透传到 extra，便于后续机器人音视频链路扩展
     */
    private VisitorCallTypeEnum normalizeCallType(VisitorRequest visitorRequest) {
        VisitorCallTypeEnum callType = visitorRequest != null
                ? visitorRequest.formatCallType()
                : VisitorCallTypeEnum.TEXT;
        if (visitorRequest == null) {
            return callType;
        }
        visitorRequest.setCallType(callType.name());
        visitorRequest.setExtra(mergeCallTypeToExtra(visitorRequest.getExtra(), callType.name()));
        return callType;
    }

    private String mergeCallTypeToExtra(String extra, String callType) {
        try {
            JSONObject obj = StringUtils.hasText(extra) ? JSON.parseObject(extra) : new JSONObject();
            if (obj == null) {
                obj = new JSONObject();
            }
            obj.put("callType", callType);
            return obj.toJSONString();
        } catch (Exception e) {
            return extra;
        }
    }

    /**
     * 音视频/电话场景要求机器人已配置音频模型能力（作为 ASR/TTS 能力前置约束）
     */
    private void ensureRealtimeAudioCapabilityIfNeeded(RobotEntity robotEntity, VisitorCallTypeEnum callType) {
        if (callType != VisitorCallTypeEnum.AUDIO
                && callType != VisitorCallTypeEnum.VIDEO
                && callType != VisitorCallTypeEnum.PHONE) {
            return;
        }

        boolean audioEnabled = robotEntity != null
                && robotEntity.getLlm() != null
                && Boolean.TRUE.equals(robotEntity.getLlm().getAudioEnabled())
                && StringUtils.hasText(robotEntity.getLlm().getAudioProvider())
                && StringUtils.hasText(robotEntity.getLlm().getAudioModel());

        if (!audioEnabled) {
            throw new IllegalStateException(String.format(
                    "Robot uid %s has no audio model configured. Realtime %s conversation requires ASR/TTS capability (llm.audioEnabled/audioProvider/audioModel)",
                    robotEntity != null ? robotEntity.getUid() : "unknown", callType.name()));
        }
    }

    /**
     * 音视频接入意图：机器人会话建立/继续时自动发起邀请（失败不影响文本主流程）
     */
    private void triggerAutoCallInviteIfNeeded(VisitorRequest request, ThreadEntity thread,
            RobotEntity robotEntity, VisitorCallTypeEnum callType) {
        if (request == null || thread == null || robotEntity == null) {
            return;
        }
        if (callType != VisitorCallTypeEnum.AUDIO && callType != VisitorCallTypeEnum.VIDEO) {
            return;
        }

        String callerUid = resolveVisitorUidForThreadTopic(request);
        String calleeUid = robotEntity.getUid();
        if (!StringUtils.hasText(callerUid) || !StringUtils.hasText(calleeUid)) {
            log.warn("skip robot auto call invite: invalid caller/callee, threadUid={}, callerUid={}, calleeUid={}",
                    thread.getUid(), callerUid, calleeUid);
            return;
        }

        try {
            WebrtcInviteRequest inviteRequest = new WebrtcInviteRequest();
            inviteRequest.setThreadUid(thread.getUid());
            inviteRequest.setCallerUid(callerUid);
            inviteRequest.setCalleeUid(calleeUid);
            inviteRequest.setCallType(callType.name());
            webrtcService.invite(inviteRequest);
            log.info("robot auto call invite sent, threadUid={}, callType={}, callerUid={}, calleeUid={}",
                    thread.getUid(), callType.name(), callerUid, calleeUid);
        } catch (Exception e) {
            log.warn("robot auto call invite failed, threadUid={}, callType={}, reason={}",
                    thread.getUid(), callType.name(), e.getMessage());
        }
    }

    // buildRobotWelcomeContent 已迁移至 WelcomeContentUtils.buildRobotWelcomeContent(robotEntity, tip)
}
