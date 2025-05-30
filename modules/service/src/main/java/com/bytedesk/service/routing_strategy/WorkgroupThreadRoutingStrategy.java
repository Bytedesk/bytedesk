/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 14:59:26
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
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.thread.event.ThreadAgentOfflineEvent;
import com.bytedesk.core.thread.event.ThreadAgentQueueEvent;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.thread.event.ThreadTransferToAgentEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.workgroup.WorkgroupRoutingService;

import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.utils.OptimisticLockingHandler;
import com.bytedesk.service.queue_member.mq.QueueMemberMessageService;

/**
 * @author Jack Ning 270580156@qq.com
 */
@Slf4j
@Component("workgroupThreadStrategy")
@AllArgsConstructor
public class WorkgroupThreadRoutingStrategy implements ThreadRoutingStrategy {

    private final WorkgroupRestService workgroupService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    private final IMessageSendService messageSendService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService;;

    private final MessageRestService messageRestService;

    private final WorkgroupRoutingService workgroupRoutingService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final OptimisticLockingHandler optimisticLockingHandler;

    private final QueueMemberMessageService queueMemberMessageService;

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return createWorkgroupThread(visitorRequest);
    }

    // 工作组对话，默认机器人接待，支持转人工
    public MessageProtobuf createWorkgroupThread(VisitorRequest visitorRequest) {
        //
        String workgroupUid = visitorRequest.getSid();
        String topic = TopicUtils.formatOrgWorkgroupThreadTopic(workgroupUid, visitorRequest.getUid());
        // 是否已经存在会话
        ThreadEntity thread = null;
        WorkgroupEntity workgroup = null;
        Optional<WorkgroupEntity> workgroupOptional = workgroupService.findByUid(workgroupUid);
        if (workgroupOptional.isPresent()) {
            workgroup = workgroupOptional.get();
        } else {
            throw new RuntimeException("Workgroup uid " + workgroupUid + " not found");
        }
        // 
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            if (threadOptional.get().isNew()) {
                // 中间状态，一般情况下，不会进入
                thread = threadOptional.get();
            } else if (threadOptional.get().isRoboting()) {
                // 如果会话已经存在，并且是机器人接待状态，同一条会话设置转接机器人
                thread = threadOptional.get();
                // 
                if (!visitorRequest.getForceAgent()) {
                    // 
                    RobotEntity robotEntity = workgroup.getRobotSettings().getRobot();
                    // 重新初始化会话，包括重置机器人状态等
                    thread = visitorThreadService.reInitWorkgroupThreadExtra(visitorRequest, thread, workgroup);
                    // 返回未关闭，或 非留言状态的会话
                    String robotString = ConvertAiUtils.convertToRobotProtobufString(robotEntity);
                    thread.setRobot(robotString);
                    ThreadEntity savedThread = threadService.save(thread);
                    if (savedThread == null) {
                        throw new RuntimeException("Failed to save thread");
                    }
                    log.info("Already have a processing robot thread {}", topic);
                    return getRobotContinueMessage(robotEntity, savedThread);
                }
            } else if (threadOptional.get().isChatting()) {
                // 如果会话已经存在，并且是聊天状态
                thread = threadOptional.get();
                // 人工类型，继续会话
                // 重新初始化会话，包括重置机器人状态等
                thread = visitorThreadService.reInitWorkgroupThreadExtra(visitorRequest, thread, workgroup);
                // 返回继续会话消息
                log.info("Already have a processing thread {}", topic);
                return getWorkgroupContinueMessage(visitorRequest, thread);
            } else if (threadOptional.get().isQueuing()) {
                thread = threadOptional.get();
                // 返回排队中的会话
                return getWorkgroupQueuingMessage(visitorRequest, thread);
            } else if (threadOptional.get().isOffline()) {
                // 离线状态，客服离线
                if (workgroup.getAvailableAgents().isEmpty()) {
                    thread = threadOptional.get();
                }
            }
        }
        // 
        if (thread == null) {
            // 不存在会话，或者所有会话处于closed状态，创建会话
            thread = visitorThreadService.createWorkgroupThread(visitorRequest, workgroup, topic);
        }
        // 未强制转人工的情况下，判断是否转机器人
        if (!visitorRequest.getForceAgent()) {
            log.info("Not force transfer to agent");
            Boolean isOffline = !workgroup.isConnected();
            Boolean isInServiceTime = workgroup.getMessageLeaveSettings().isInServiceTime();
            Boolean transferToRobot = workgroup.getRobotSettings().shouldTransferToRobot(isOffline, isInServiceTime);
            if (transferToRobot) {
                // 转机器人
                RobotEntity robot = workgroup.getRobotSettings().getRobot();
                if (robot != null) {
                    // 重新初始化会话，包括重置机器人状态等
                    thread = visitorThreadService.reInitWorkgroupThreadExtra(visitorRequest, thread, workgroup);
                    // 返回机器人欢迎消息
                    return routeToRobot(visitorRequest, thread, robot, workgroup);
                } else {
                    throw new RuntimeException("Workgroup robot not found");
                }
            }
        } else {
            log.info("Force transfer to agent");
        }
        // 下面人工接待
        AgentEntity agentEntity = workgroupRoutingService.selectAgent(workgroup, thread);
        if (agentEntity == null) {
            // 离线留言接待客服
            agentEntity = workgroup.getMessageLeaveAgent();
            if (agentEntity == null) {
                log.error("离线留言接待客服不存在，请配置工作组留言接待客服。尝试从数据库重新加载工作组信息。");
                throw new RuntimeException("Workgroup message leave agent not found");
            }
        }
        // 
        UserProtobuf agent = agentEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService.enqueueWorkgroup(thread, agent, workgroup, visitorRequest);
        // log.info("routeAgent Enqueued to queue {}", queueMemberEntity.getUid());
        if (visitorRequest.getForceAgent()) {
            log.info("force agent transfer to agent {}", agentEntity.getUid());
            // 只有接待客服是robot接待时，前端才会显示转人工按钮，转人工
            bytedeskEventPublisher.publishEvent(new ThreadTransferToAgentEvent(this, thread));
            // 
            queueMemberEntity.transferRobotToAgent();
            queueMemberRestService.save(queueMemberEntity);
            // 
            // 使用MQ异步处理转人工操作
            // Map<String, Object> updates = new HashMap<>();
            // updates.put("robotToAgent", true);
            // queueMemberMessageService.sendUpdateMessage(queueMemberEntity, updates);
        }
        //
        if (agentEntity.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            if (queueMemberEntity.getWorkgroupQueue().getQueuingCount() < agentEntity.getMaxThreadCount()) {
                // 未满则接待
                return handleAvailableWorkgroup(thread, agentEntity, queueMemberEntity);
            } else {
                // 排队，已满则排队
                return handleQueuedWorkgroup(thread, agentEntity, queueMemberEntity);
            }
        } else {
            // 离线状态永远显示离线提示语，不显示"继续会话"
            // 客服离线 或 非接待状态
            return getOfflineMessage(visitorRequest, thread, agentEntity, workgroup, queueMemberEntity);
        }
    }

    private MessageProtobuf handleAvailableWorkgroup(ThreadEntity threadFromRequest, AgentEntity agent, QueueMemberEntity queueMemberEntity) {
        log.info("handleAvailableWorkgroup");
        // 未满则接待
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        ThreadEntity thread = threadOptional.get();
        // 
        String content = agent.getServiceSettings().getWelcomeTip();
        if (content == null || content.isEmpty()) {
            content = "您好，请问有什么可以帮助您？";
        }
        // 未满则接待
        thread.setUserUid(agent.getUid());
        thread.setChatting().setContent(content).setUnreadCount(1);
        
        // Only set owner if member exists
        if (agent.getMember() != null) {
            thread.setOwner(agent.getMember().getUser());
        }
        //
        UserProtobuf agentProtobuf = agent.toUserProtobuf();
        thread.setAgent(agentProtobuf.toJson());
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        // 客服接待
        queueMemberEntity.agentAutoAcceptThread();
        queueMemberRestService.save(queueMemberEntity);
        //
        bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, savedThread));
        bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(content, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        //
        return messageProtobuf;
    }

    private MessageProtobuf handleQueuedWorkgroup(ThreadEntity threadFromRequest, AgentEntity agentEntity, QueueMemberEntity queueMemberEntity) {
        log.info("handleQueuedWorkgroup {}", agentEntity.getNickname());
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        ThreadEntity thread = threadOptional.get();

        // 排队，已满则排队
        String content = "";
        if (queueMemberEntity.getWorkgroupQueue().getQueuingCount() == 0) {
            // 客服接待刚满员，下一个就是他，
            content = "请稍后，下一个就是您";
        } else {
            // 前面有排队人数
            content = " 当前排队人数：" + queueMemberEntity.getWorkgroupQueue().getQueuingCount() + " 大约等待时间："
                    + queueMemberEntity.getWorkgroupQueue().getQueuingCount() * 2 + "  分钟";
        }

        // 进入排队队列
        thread.setUserUid(agentEntity.getUid());
        thread.setQueuing().setContent(content).setUnreadCount(0);
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        //
        bytedeskEventPublisher.publishEvent(new ThreadAgentQueueEvent(this, savedThread));
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getAgentThreadQueueMessage(agentEntity, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        //
        return messageProtobuf;
    }

    public MessageProtobuf getOfflineMessage(VisitorRequest visitorRequest, ThreadEntity threadFromRequest, AgentEntity agentEntity, WorkgroupEntity workgroup, QueueMemberEntity queueMemberEntity) {
        log.info("getOfflineMessage {}", agentEntity.getNickname());
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        ThreadEntity thread = threadOptional.get();
        //
        String content = workgroup.getMessageLeaveSettings().getMessageLeaveTip();
        if (content == null || content.isEmpty()) {
            content = "请稍后，客服会尽快回复您";
        }
        thread.setOffline().setContent(content);
        UserProtobuf agentProtobuf = agentEntity.toUserProtobuf();
        thread.setAgent(agentProtobuf.toJson());
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        // 
        queueMemberEntity.setAgentOffline(true);
        queueMemberRestService.save(queueMemberEntity);
        // 创建新的留言消息
        MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(content, savedThread);
        messageRestService.save(message);
        // 返回留言消息
        // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
        MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        //
        bytedeskEventPublisher.publishEvent(new ThreadAgentOfflineEvent(this, savedThread));
        //
        return messageProtobuf;
    }

    private MessageProtobuf getWorkgroupContinueMessage(VisitorRequest visitorRequest, ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getWorkgroupContinueMessage user: {}", user.getNickname());
        // 继续会话
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadContinueMessage(user, thread);
        // 微信公众号等渠道不能重复推送"继续会话"消息
        if (!visitorRequest.isWeChat()) {
            // 广播消息，由消息通道统一处理
            messageSendService.sendProtobufMessage(messageProtobuf);
        }
        //
        return messageProtobuf;
    }

    private MessageProtobuf getWorkgroupQueuingMessage(VisitorRequest visitorRequest, ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getWorkgroupQueuingMessage: user: {}, agent {}", user.toString(), thread.getAgent());
        //
        return ThreadMessageUtil.getThreadQueuingMessage(user, thread);
    }

    public MessageProtobuf routeToRobot(VisitorRequest visitorRequest, ThreadEntity threadFromRequest, RobotEntity robotEntity, WorkgroupEntity workgroup) {
        Assert.notNull(threadFromRequest, "ThreadEntity must not be null");
        Assert.notNull(robotEntity, "RobotEntity must not be null");

        // 直接使用threadFromRequest，修改保存报错，所以重新查询，待完善
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        // 
        ThreadEntity thread = threadOptional.get();
        UserProtobuf robotProtobuf = robotEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService.enqueueWorkgroup(thread, robotProtobuf, workgroup, visitorRequest);
        log.info("routeRobot Enqueued to queue {}", queueMemberEntity.getUid());
        // 机器人接待
        String content = robotEntity.getServiceSettings().getWelcomeTip();
        if (content == null || content.isEmpty()) {
            content = "您好，请问有什么可以帮助您？";
        }
        // 更新线程状态
        thread.setUserUid(robotEntity.getUid());
        thread.setRoboting().setContent(content).setUnreadCount(0);
        // 
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robotEntity);
        thread.setRobot(robotString);
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        // 更新排队状态
        queueMemberEntity.robotAutoAcceptThread();
        queueMemberRestService.save(queueMemberEntity);
        //
        bytedeskEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));

        // 如果拉取的是访客的消息，会影响前端
        // 查询最新一条消息，如果距离当前时间不超过30分钟，则直接使用之前的消息，否则创建新的消息
        // Optional<MessageEntity> messageOptional = messageRestService.findByThreadUidAndTypeAndUserContains(savedThread.getUid(), MessageTypeEnum.WELCOME.name(), robotEntity.getUid());
        // if (messageOptional.isPresent()) {
        //     MessageEntity message = messageOptional.get();
        //     if (message.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(30))) {
        //         // 距离当前时间不超过30分钟，则直接使用之前的消息
        //         // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
        //         MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        //         // messageSendService.sendProtobufMessage(messageProtobuf);
        //         return messageProtobuf;
        //     }
        // }

        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(content, savedThread);
        messageRestService.save(message);

        return ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
    }

    private MessageProtobuf getRobotContinueMessage(RobotEntity robotEntity, ThreadEntity thread) {
        // 如果拉取的是访客的消息，会影响前端
        // Optional<MessageEntity> messageOptional = messageRestService.findLatestByThreadUid(thread.getUid());
        // if (messageOptional.isPresent()) {
        //     MessageEntity message = messageOptional.get();
        //     if (message.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(30))) {
        //         // 距离当前时间不超过30分钟，则直接使用之前的消息
        //         // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
        //         MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
        //         // messageSendService.sendProtobufMessage(messageProtobuf);
        //         return messageProtobuf;
        //     }
        // }
        //
        String content = robotEntity.getServiceSettings().getWelcomeTip();
        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(content, thread);
        // 
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

}
