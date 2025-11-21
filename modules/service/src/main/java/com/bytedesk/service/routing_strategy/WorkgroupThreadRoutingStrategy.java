/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-13 22:51:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.service.utils.WelcomeContentUtils;
import com.bytedesk.core.message.content.QueueContent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.thread.event.ThreadAgentOfflineEvent;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.thread.event.ThreadTransferToAgentEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueAutoAssignService;
import com.bytedesk.service.queue.QueueAutoAssignTriggerSource;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_member.mq.QueueMemberMessageService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.workgroup.WorkgroupRoutingService;
import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工作组线程路由策略
 * 
 * <p>
 * 功能特点：
 * - 支持机器人优先接待，可转人工
 * - 智能路由决策：根据工作组配置、服务时间、客服在线状态等因素决定路由方向
 * - 支持排队管理和离线留言
 * - 支持强制转人工和机器人转人工
 * 
 * <p>
 * 路由决策流程：
 * 1. 检查现有会话状态
 * 2. 根据配置决定机器人或人工接待
 * 3. 处理排队和离线场景
 * 4. 支持会话状态转换
 * 
 * @author Jack Ning 270580156@qq.com
 * @since 1.0.0
 */
@Slf4j
@Component("workgroupThreadStrategy")
@AllArgsConstructor
public class WorkgroupThreadRoutingStrategy extends AbstractThreadRoutingStrategy {

    private final WorkgroupRestService workgroupRestService;
    private final ThreadRestService threadRestService;
    private final VisitorThreadService visitorThreadService;
    private final IMessageSendService messageSendService;
    private final QueueService queueService;
    private final QueueMemberRestService queueMemberRestService;
    private final MessageRestService messageRestService;
    private final WorkgroupRoutingService workgroupRoutingService;
    private final BytedeskEventPublisher bytedeskEventPublisher;
    private final QueueMemberMessageService queueMemberMessageService;
    private final PresenceFacadeService presenceFacadeService;
    private final QueueAutoAssignService queueAutoAssignService;
    private final WorkgroupRepository workgroupRepository;

    @Override
    protected ThreadRestService getThreadRestService() {
        return threadRestService;
    }

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return executeWithExceptionHandling("create workgroup thread", visitorRequest.getSid(),
                () -> createWorkgroupThread(visitorRequest));
    }

    /**
     * 创建工作组会话
     * 
     * @param visitorRequest 访客请求
     * @return 消息协议对象
     */
    public MessageProtobuf createWorkgroupThread(VisitorRequest visitorRequest) {
        long startTime = System.currentTimeMillis();
        log.info("开始创建工作组线程 - visitorUid: {}, workgroupUid: {}, forceAgent: {}",
                visitorRequest.getUid(), visitorRequest.getSid(), visitorRequest.getForceAgent());

        // 1. 验证和获取工作组信息
        log.debug("步骤1: 开始获取工作组信息 - workgroupUid: {}", visitorRequest.getSid());
        WorkgroupEntity workgroup = getWorkgroupEntity(visitorRequest.getSid());
        log.info("步骤1完成: 成功获取工作组信息 - workgroupUid: {}, 在线客服数(presence): {}",
                workgroup.getUid(), presenceFacadeService.countOnlineAgents(workgroup));

        // 2. 生成会话主题并检查现有会话
        log.debug("步骤2: 开始处理线程创建或获取");
        String topic = TopicUtils.formatOrgWorkgroupThreadTopic(workgroup.getUid(), visitorRequest.getUid());
        log.debug("生成工作组线程主题: {}", topic);
        ThreadEntity thread = getOrCreateWorkgroupThread(visitorRequest, workgroup, topic);
        log.info("步骤2完成: 线程处理完成 - threadUid: {}, 状态: {}, 是否新建: {}",
                thread.getUid(), thread.getStatus(), thread.isNew());

        // 3. 处理现有活跃会话
        log.debug("步骤3: 检查是否为现有活跃会话");
        MessageProtobuf existingThreadResult = handleExistingWorkgroupThread(visitorRequest, thread, workgroup);
        if (existingThreadResult != null) {
            log.info("检测到现有活跃会话，直接返回 - threadUid: {}, 总耗时: {}ms",
                    thread.getUid(), System.currentTimeMillis() - startTime);
            return existingThreadResult;
        }

        // 4. 新会话路由决策
        log.debug("步骤4: 开始新会话路由决策");
        MessageProtobuf result = routeNewWorkgroupThread(visitorRequest, thread, workgroup);
        log.info("创建工作组线程完成 - threadUid: {}, 总耗时: {}ms",
                thread.getUid(), System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 获取工作组实体
     */
    private WorkgroupEntity getWorkgroupEntity(String workgroupUid) {
        long startTime = System.currentTimeMillis();
        log.debug("开始获取工作组实体 - workgroupUid: {}", workgroupUid);

        validateUid(workgroupUid, "Workgroup");

        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
        if (!workgroupOptional.isPresent()) {
            log.error("工作组不存在 - workgroupUid: {}", workgroupUid);
            throw new IllegalArgumentException("Workgroup uid " + workgroupUid + " not found");
        }

        WorkgroupEntity workgroup = workgroupOptional.get();
        log.info("工作组实体获取完成 - workgroupUid: {}, 在线客服数量(presence): {}, 耗时: {}ms",
                workgroup.getUid(), presenceFacadeService.countOnlineAgents(workgroup),
                System.currentTimeMillis() - startTime);
        return workgroup;
    }

    /**
     * 获取或创建工作组会话
     */
    private ThreadEntity getOrCreateWorkgroupThread(VisitorRequest visitorRequest, WorkgroupEntity workgroup,
            String topic) {
        // 当强制新建会话时，直接创建新会话，跳过复用逻辑
        if (Boolean.TRUE.equals(visitorRequest.getForceNewThread())) {
            log.debug("forceNewThread=true, creating new workgroup thread for topic: {}", topic);
            return visitorThreadService.createWorkgroupThread(visitorRequest, workgroup, topic);
        }

        log.debug("开始查询现有工作组线程 - topic: {}", topic);
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            ThreadEntity existingThread = threadOptional.get();
            log.info("发现现有工作组线程 - threadUid: {}, 状态: {}, 创建时间: {}",
                    existingThread.getUid(), existingThread.getStatus(), existingThread.getCreatedAt());
            return existingThread;
        }

        // 创建新会话
        log.debug("未找到现有工作组线程，开始创建新线程");
        return visitorThreadService.createWorkgroupThread(visitorRequest, workgroup, topic);
    }

    /**
     * 处理现有工作组会话
     * 
     * @return 如果是现有活跃会话则返回相应消息，否则返回null继续新会话流程
     */
    private MessageProtobuf handleExistingWorkgroupThread(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.debug("开始处理现有工作组会话 - threadUid: {}, 状态: {}", thread.getUid(), thread.getStatus());

        if (thread.isNew()) {
            log.debug("线程处于新建状态，继续路由流程");
            return null; // 继续新会话流程
        }

        if (thread.isRoboting()) {
            log.info("发现现有机器人会话，进入机器人处理流程");
            return handleExistingRobotThread(visitorRequest, thread, workgroup);
        }

        if (thread.isChatting()) {
            log.info("发现现有聊天会话，进入聊天处理流程");
            return handleExistingChatThread(visitorRequest, thread, workgroup);
        }

        if (thread.isQueuing()) {
            log.info("发现现有排队会话，返回排队消息");
            return getWorkgroupQueuingMessage(visitorRequest, thread);
        }

        if (thread.isOffline()) {
            log.info("发现现有离线会话，进入离线处理流程");
            return handleExistingOfflineThread(visitorRequest, thread, workgroup);
        }

        log.debug("线程状态不匹配任何已知处理流程，继续新会话流程 - 状态: {}", thread.getStatus());
        return null; // 其他状态继续新会话流程
    }

    /**
     * 处理现有机器人会话
     */
    private MessageProtobuf handleExistingRobotThread(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.info("Found existing robot thread");

        if (!visitorRequest.getForceAgent()) {
            // 重新初始化机器人会话
            RobotEntity robotEntity = workgroup.getSettings() != null
                    && workgroup.getSettings().getRobotSettings() != null
                            ? workgroup.getSettings().getRobotSettings().getRobot()
                            : null;
            if (robotEntity != null) {
                thread = visitorThreadService.reInitWorkgroupThreadExtra(visitorRequest, thread, workgroup);
                String robotString = ConvertAiUtils.convertToRobotProtobufString(robotEntity);
                thread.setRobot(robotString);
                ThreadEntity savedThread = saveThread(thread);

                log.info("Continuing existing robot thread: {}", thread.getTopic());
                return getRobotContinueMessage(robotEntity, savedThread);
            }
        }
        // 强制转人工的情况下继续后续流程
        return null;
    }

    /**
     * 处理现有聊天会话
     */
    private MessageProtobuf handleExistingChatThread(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.info("Found existing chat thread");

        // 重新初始化会话
        thread = visitorThreadService.reInitWorkgroupThreadExtra(visitorRequest, thread, workgroup);
        log.info("Continuing existing chat thread: {}", thread.getTopic());
        return getWorkgroupContinueMessage(visitorRequest, thread);
    }

    /**
     * 处理现有离线会话
     */
    private MessageProtobuf handleExistingOfflineThread(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.info("Found existing offline thread");

        // 如果没有可用客服，继续使用现有离线会话
        if (presenceFacadeService.getAvailableAgents(workgroup).isEmpty()) {
            return null; // 继续使用现有会话
        }
        return null; // 有可用客服时重新路由
    }

    /**
     * 路由新工作组会话
     */
    private MessageProtobuf routeNewWorkgroupThread(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.debug("开始新工作组会话路由决策 - threadUid: {}, workgroupUid: {}",
                thread.getUid(), workgroup.getUid());

        // 决定路由方向：机器人 or 人工
        boolean shouldUseRobot = shouldRouteToRobot(visitorRequest, workgroup);
        if (shouldUseRobot) {
            log.info("路由到机器人处理");
            return routeToRobot(visitorRequest, thread, workgroup);
        } else {
            log.info("路由到人工客服处理");
            return routeToAgent(visitorRequest, thread, workgroup);
        }
    }

    /**
     * 判断是否应该路由到机器人
     */
    private boolean shouldRouteToRobot(VisitorRequest visitorRequest, WorkgroupEntity workgroup) {
        log.debug("开始机器人路由决策判断");

        // 强制转人工
        if (visitorRequest.getForceAgent()) {
            log.info("用户强制要求转人工，不使用机器人");
            return false;
        }

        // 检查机器人配置和服务时间
        boolean isOffline = !presenceFacadeService.isWorkgroupOnline(workgroup);
        boolean isInServiceTime = workgroup.getSettings() != null
                && workgroup.getSettings().getMessageLeaveSettings() != null
                        ? workgroup.getSettings().getMessageLeaveSettings().isInServiceTime()
                        : true;

        log.debug("路由决策参数 - 工作组离线状态: {}, 在服务时间内: {}",
                isOffline, isInServiceTime);

        boolean transferToRobot = workgroup.getSettings() != null && workgroup.getSettings().getRobotSettings() != null
                ? workgroup.getSettings().getRobotSettings().shouldTransferToRobot(isOffline, isInServiceTime)
                : false;
        log.debug("机器人设置决策结果: {}", transferToRobot);

        if (transferToRobot) {
            RobotEntity robot = workgroup.getSettings() != null && workgroup.getSettings().getRobotSettings() != null
                    ? workgroup.getSettings().getRobotSettings().getRobot()
                    : null;
            if (robot != null) {
                log.info("满足机器人路由条件，将路由到机器人 - robotUid: {}, offline: {}, in service time: {}",
                        robot.getUid(), isOffline, isInServiceTime);
                return true;
            } else {
                log.warn("机器人路由条件满足但未找到机器人实体 - workgroupUid: {}", workgroup.getUid());
            }
        } else {
            log.debug("不满足机器人路由条件，将路由到人工客服");
        }

        return false;
    }

    /**
     * 路由到机器人
     */
    private MessageProtobuf routeToRobot(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        RobotEntity robot = workgroup.getSettings() != null && workgroup.getSettings().getRobotSettings() != null
                ? workgroup.getSettings().getRobotSettings().getRobot()
                : null;
        if (robot == null) {
            throw new IllegalStateException("Workgroup robot not found");
        }

        // 重新初始化会话
        thread = visitorThreadService.reInitWorkgroupThreadExtra(visitorRequest, thread, workgroup);

        return routeToRobot(visitorRequest, thread, robot, workgroup);
    }

    /**
     * 路由到人工客服
     */
    private MessageProtobuf routeToAgent(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.debug("开始路由到人工客服 - threadUid: {}, workgroupUid: {}",
                thread.getUid(), workgroup.getUid());

        // 检查是否在工作时间内
        boolean isInServiceTime = workgroup.getSettings() != null
                && workgroup.getSettings().getMessageLeaveSettings() != null
                        ? workgroup.getSettings().getMessageLeaveSettings().isInServiceTime()
                        : true;
        log.debug("服务时间检查 - 是否在服务时间内: {}", isInServiceTime);

        if (!isInServiceTime) {
            log.info("不在服务时间内，路由到离线留言");
            // 选择离线留言接待客服
            AgentEntity messageLeaveAgent = workgroup.getMessageLeaveAgent();
            if (messageLeaveAgent == null) {
                log.error("离线留言接待客服不存在，请配置工作组留言接待客服 - workgroupUid: {}",
                        workgroup.getUid());
                throw new IllegalStateException("Workgroup message leave agent not found");
            }
            log.debug("使用离线留言接待客服 - agentUid: {}", messageLeaveAgent.getUid());
            QueueMemberEntity queueMemberEntity = queueService
                    .enqueueWorkgroupWithResult(thread, messageLeaveAgent, workgroup, visitorRequest)
                    .queueMember();

            // 直接返回离线留言消息
            return getOfflineMessage(visitorRequest, thread, messageLeaveAgent, workgroup, queueMemberEntity);
        }

        // 选择客服
        log.debug("在服务时间内，开始选择客服");
        long selectStartTime = System.currentTimeMillis();
        AgentEntity agentEntity = selectAgent(workgroup, thread);
        log.info("客服选择完成 - agentUid: {}, 选择耗时: {}ms", agentEntity.getUid(), System.currentTimeMillis() - selectStartTime);

        // 加入队列
        log.debug("开始将线程加入工作组队列");
        long enqueueStartTime = System.currentTimeMillis();
        QueueService.QueueEnqueueResult enqueueResult = queueService
            .enqueueWorkgroupWithResult(thread, agentEntity, workgroup, visitorRequest);
        QueueMemberEntity queueMemberEntity = enqueueResult.queueMember();
        log.info("工作组队列加入完成 - queueMemberUid: {}, 耗时: {}ms", queueMemberEntity.getUid(), System.currentTimeMillis() - enqueueStartTime);

        // 处理强制转人工
        if (visitorRequest.getForceAgent()) {
            log.debug("处理强制转人工标记");
            handleForceAgentTransfer(visitorRequest, thread, queueMemberEntity);
        }

        // 根据客服状态进行路由
        log.debug("开始根据客服状态进行最终路由");
        return routeByAgentStatus(agentEntity, thread, queueMemberEntity, workgroup, visitorRequest);
    }

    /**
     * 选择客服
     */
    private AgentEntity selectAgent(WorkgroupEntity workgroup, ThreadEntity thread) {
        AgentEntity agentEntity = workgroupRoutingService.selectAgent(workgroup, thread);

        if (agentEntity == null) {
            // 离线留言接待客服
            agentEntity = workgroup.getMessageLeaveAgent();
            if (agentEntity == null) {
                log.error("离线留言接待客服不存在，请配置工作组留言接待客服");
                throw new IllegalStateException("Workgroup message leave agent not found");
            }
        }

        return agentEntity;
    }

    /**
     * 处理强制转人工
     */
    private void handleForceAgentTransfer(VisitorRequest visitorRequest, ThreadEntity thread,
            QueueMemberEntity queueMemberEntity) {
        if (visitorRequest.getForceAgent()) {
            log.info("Processing force agent transfer");

            // 发布转人工事件
            bytedeskEventPublisher.publishEvent(new ThreadTransferToAgentEvent(this, thread));

            // 更新队列状态
            queueMemberEntity.transferRobotToAgent();

            // 异步处理转人工操作
            Map<String, Object> updates = new HashMap<>();
            updates.put("robotToAgent", true);
            queueMemberMessageService.sendUpdateMessage(queueMemberEntity, updates);
        }
    }

    /**
     * 根据客服状态进行路由
     */
    private MessageProtobuf routeByAgentStatus(AgentEntity agentEntity, ThreadEntity thread,
            QueueMemberEntity queueMemberEntity, WorkgroupEntity workgroup, VisitorRequest visitorRequest) {

        if (presenceFacadeService.isAgentOnlineAndAvailable(agentEntity)) {
            // 客服在线且可接待
            if (queueMemberEntity.getWorkgroupQueue().getChattingCount() < agentEntity.getMaxThreadCount()) {
                return handleAvailableWorkgroup(thread, agentEntity, queueMemberEntity);
            } else {
                return handleQueuedWorkgroup(thread, agentEntity, queueMemberEntity);
            }
        } else {
            // 客服离线或不可接待
            return getOfflineMessage(visitorRequest, thread, agentEntity, workgroup, queueMemberEntity);
        }
    }

    /**
     * 处理可用工作组客服
     */
    private MessageProtobuf handleAvailableWorkgroup(ThreadEntity threadFromRequest, AgentEntity agentEntity,
            QueueMemberEntity queueMemberEntity) {
        log.info("Handling available workgroup agent: {}", agentEntity.getNickname());

        // 获取最新线程状态
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());

        // 设置欢迎内容和线程状态
        String welcomeContent = getAgentWelcomeMessage(agentEntity);
        thread.setUserUid(agentEntity.getUid());
        thread.setChatting().setContent(welcomeContent);

        // 设置线程所有者
        setThreadOwner(thread, agentEntity);

        // 设置客服信息
        UserProtobuf agentProtobuf = agentEntity.toUserProtobuf();
        thread.setAgent(agentProtobuf.toJson());

        // 保存线程
        ThreadEntity savedThread = saveThread(thread);

        // 更新队列状态为自动接受
        updateQueueMemberForAgentAccept(queueMemberEntity);

        // 发布事件
        publishWorkgroupThreadEvents(savedThread);

        // 发送欢迎消息（结构化 WelcomeContent）
        WelcomeContent wc = WelcomeContentUtils.buildAgentWelcomeContent(agentEntity);
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(wc, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);

        return messageProtobuf;
    }

    /**
     * 处理排队工作组客服
     */
    private MessageProtobuf handleQueuedWorkgroup(ThreadEntity threadFromRequest, AgentEntity agentEntity,
            QueueMemberEntity queueMemberEntity) {
        log.info("Handling queued workgroup agent: {}", agentEntity.getNickname());

        // 获取最新线程状态
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());

        // 生成排队消息文本
        int queuingCount = queueMemberEntity.getWorkgroupQueue().getQueuingCount();
        String queueContentText = generateWorkgroupQueueMessage(queueMemberEntity);

        // 构建结构化 QueueContent
        QueueContent.QueueContentBuilder<?, ?> builder = QueueContent.builder()
                .content(queueContentText)
                .position(queueMemberEntity.getQueueNumber())
                .queueSize(queuingCount)
                .serverTimestamp(System.currentTimeMillis());

        // 计算预估等待时间（分钟 -> 秒）与描述
        if (queuingCount > 0) {
            int estimatedMinutes = queuingCount * ESTIMATED_WAIT_TIME_PER_PERSON;
            builder.waitSeconds(estimatedMinutes * 60)
                    .estimatedWaitTime("约" + estimatedMinutes + "分钟");
        } else {
            builder.waitSeconds(0).estimatedWaitTime("即将开始");
        }
        QueueContent queueContent = builder.build();

        // 设置线程状态（使用结构化JSON）
        thread.setUserUid(agentEntity.getUid());
        thread.setQueuing().setContent(queueContent.toJson());

        // 保存线程
        ThreadEntity savedThread = saveThread(thread);

        // 发送结构化排队消息
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(queueContent, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);

        return messageProtobuf;
    }

    /**
     * 获取离线消息
     */
    public MessageProtobuf getOfflineMessage(VisitorRequest visitorRequest, ThreadEntity threadFromRequest,
            AgentEntity agentEntity, WorkgroupEntity workgroup, QueueMemberEntity queueMemberEntity) {
        log.info("Handling offline message for agent: {}", agentEntity.getNickname());

        // 获取最新线程状态
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());

        // 设置离线内容
        String offlineContent = getWorkgroupOfflineMessage(workgroup);
        thread.setOffline().setContent(offlineContent);

        // 设置客服信息
        UserProtobuf agentProtobuf = agentEntity.toUserProtobuf();
        thread.setAgent(agentProtobuf.toJson());

        // 保存线程
        ThreadEntity savedThread = saveThread(thread);

        // 更新队列状态为离线
        queueMemberEntity.setAgentOffline(true);
        queueMemberRestService.save(queueMemberEntity);

        // 创建离线消息
        MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(offlineContent, savedThread);
        messageRestService.save(message);

        // 发送离线消息
        MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);

        // 发布离线事件
        bytedeskEventPublisher.publishEvent(new ThreadAgentOfflineEvent(this, savedThread));

        return messageProtobuf;
    }

    /**
     * 路由到机器人
     */
    public MessageProtobuf routeToRobot(VisitorRequest visitorRequest, ThreadEntity threadFromRequest,
            RobotEntity robotEntity, WorkgroupEntity workgroup) {
        
        validateThread(threadFromRequest, "route to robot");
        if (robotEntity == null) {
            throw new IllegalArgumentException("RobotEntity must not be null");
        }

        // 获取最新线程状态
        ThreadEntity thread = getThreadByUid(threadFromRequest.getUid());

        // 加入队列
        UserProtobuf robotProtobuf = robotEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService
            .enqueueWorkgroupWithResult(thread, robotProtobuf, workgroup, visitorRequest)
            .queueMember();
        log.info("Robot enqueued to queue: {}", queueMemberEntity.getUid());

        // 设置机器人接待状态
        String welcomeContent = getRobotWelcomeMessage(robotEntity);
        thread.setUserUid(robotEntity.getUid());
        thread.setRoboting().setContent(welcomeContent);

        // 设置机器人信息
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robotEntity);
        thread.setRobot(robotString);

        // 保存线程
        ThreadEntity savedThread = saveThread(thread);
        // 更新队列状态
        updateQueueMemberForRobotAccept(queueMemberEntity);
        // 发布事件
        bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));

        // 创建欢迎消息（结构化 WelcomeContent）
        WelcomeContent wc = WelcomeContentUtils.buildRobotWelcomeContent(robotEntity);
        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(wc, savedThread);
        messageRestService.save(message);

        return ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
    }

    /**
     * 获取工作组继续对话消息
     */
    private MessageProtobuf getWorkgroupContinueMessage(VisitorRequest visitorRequest, ThreadEntity thread) {
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("Getting workgroup continue message for user: {}", user.getNickname());

        // 继续会话
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadContinueMessage(user, thread);

        // 微信公众号等渠道不能重复推送"继续会话"消息
        if (!visitorRequest.isWeChat()) {
            messageSendService.sendProtobufMessage(messageProtobuf);
        }

        return messageProtobuf;
    }

    /**
     * 获取工作组排队消息
     */
    private MessageProtobuf getWorkgroupQueuingMessage(VisitorRequest visitorRequest, ThreadEntity thread) {

        // 线程content通常为结构化QueueContent JSON；解析失败则降级为仅文本
        QueueContent qc = null;
        try {
            String c = thread.getContent();
            if (c != null && c.trim().startsWith("{")) {
                qc = JSON.parseObject(c, QueueContent.class);
            }
        } catch (Exception e) {
            log.debug("Parse queue content failed, fallback to text - threadUid: {}, error: {}", thread.getUid(),
                    e.getMessage());
        }
        if (qc == null) {
            qc = QueueContent.builder()
                    .content(thread.getContent())
                    .serverTimestamp(System.currentTimeMillis())
                    .build();
        }
        return ThreadMessageUtil.getThreadQueueMessage(qc, thread);
    }

    /**
     * 获取机器人继续对话消息
     */
    private MessageProtobuf getRobotContinueMessage(RobotEntity robotEntity, ThreadEntity thread) {
        validateThread(thread, "get robot continue message");

        WelcomeContent wc = WelcomeContentUtils.buildRobotWelcomeContent(robotEntity);
        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(wc, thread);

        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取客服欢迎消息
     */
    private String getAgentWelcomeMessage(AgentEntity agentEntity) {
        String customMessage = agentEntity.getSettings() != null
                && agentEntity.getSettings().getServiceSettings() != null
                        ? agentEntity.getSettings().getServiceSettings().getWelcomeTip()
                        : null;
        return getValidWelcomeMessage(customMessage);
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
     * 获取工作组离线消息
     */
    private String getWorkgroupOfflineMessage(WorkgroupEntity workgroup) {
        String customMessage = workgroup.getSettings() != null
                && workgroup.getSettings().getMessageLeaveSettings() != null
                        ? workgroup.getSettings().getMessageLeaveSettings().getMessageLeaveTip()
                        : null;
        if (customMessage == null || customMessage.isEmpty()) {
            customMessage = "请稍后，客服会尽快回复您";
        }
        return customMessage;
    }

    /**
     * 生成工作组排队消息
     */
    private String generateWorkgroupQueueMessage(QueueMemberEntity queueMemberEntity) {
        int queuingCount = queueMemberEntity.getWorkgroupQueue().getQueuingCount();
        return generateQueueMessage(queuingCount);
    }

    /**
     * 设置线程所有者
     */
    private void setThreadOwner(ThreadEntity thread, AgentEntity agentEntity) {
        if (agentEntity.getMember() != null) {
            log.debug("Setting owner for thread: {}", agentEntity.getMember().getUser().getNickname());
            thread.setOwner(agentEntity.getMember().getUser());
        } else {
            log.warn("AgentEntity {} does not have a member associated, owner will not be set.", agentEntity.getUid());
            throw new IllegalStateException("AgentEntity member not found, cannot set owner for thread.");
        }
    }

    /**
     * 更新队列成员状态为客服自动接受
     */
    private void updateQueueMemberForAgentAccept(QueueMemberEntity queueMemberEntity) {
        try {
            queueMemberEntity.agentAutoAcceptThread();

            // 使用MQ异步处理自动接受会话操作
            Map<String, Object> updates = new HashMap<>();
            updates.put("agentAutoAcceptThread", true);
            queueMemberMessageService.sendUpdateMessage(queueMemberEntity, updates);

            log.debug("Updated queue member status for agent auto-accept: {}", queueMemberEntity.getUid());
        } catch (Exception e) {
            log.error("Failed to update queue member for agent auto-accept: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update queue member status", e);
        }
    }

    /**
     * 更新队列成员状态为机器人自动接受
     */
    private void updateQueueMemberForRobotAccept(QueueMemberEntity queueMemberEntity) {
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
     * 发布工作组线程相关事件
     */
    private void publishWorkgroupThreadEvents(ThreadEntity savedThread) {
        try {
            bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, savedThread));
            bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
            log.debug("Published workgroup thread events for thread: {}", savedThread.getUid());
        } catch (Exception e) {
            log.warn("Failed to publish thread events for workgroup thread {}: {}", savedThread.getUid(),
                    e.getMessage());
        }
    }

    @EventListener
    public void handleWorkgroupAutoAssign(AgentUpdateStatusEvent event) {
        AgentEntity agent = event.getAgent();
        if (!shouldTriggerWorkgroupAutoAssign(agent)) {
            return;
        }
        List<WorkgroupEntity> workgroups = workgroupRepository.findByAgentUid(agent.getUid());
        if (workgroups == null || workgroups.isEmpty()) {
            return;
        }
        int capacityHint = estimateAvailableSlots(agent);
        for (WorkgroupEntity workgroup : workgroups) {
            if (workgroup == null || !StringUtils.hasText(workgroup.getUid())) {
                continue;
            }
            queueAutoAssignService.triggerWorkgroupAutoAssign(
                    workgroup.getUid(),
                    agent.getUid(),
                    QueueAutoAssignTriggerSource.WORKGROUP_STATUS_EVENT,
                    capacityHint);
        }
    }

    private boolean shouldTriggerWorkgroupAutoAssign(AgentEntity agent) {
        return agent != null
                && StringUtils.hasText(agent.getUid())
                && presenceFacadeService.isAgentOnlineAndAvailable(agent);
    }

    private int estimateAvailableSlots(AgentEntity agent) {
        int maxThreadCount = agent.getMaxThreadCount();
        return maxThreadCount > 0 ? maxThreadCount : 1;
    }
}
