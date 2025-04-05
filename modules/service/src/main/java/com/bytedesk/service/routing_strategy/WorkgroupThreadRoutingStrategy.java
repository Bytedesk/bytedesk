/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 16:26:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.thread.ThreadProcessStatusEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberAcceptTypeEnum;
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

    private final ApplicationEventPublisher applicationEventPublisher;

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
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            //
            if (threadOptional.get().isNew()) {
                thread = threadOptional.get();
            } else if (threadOptional.get().isChatting()) {
                thread = threadOptional.get();
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
                thread = threadOptional.get();
            }
        }

        if (thread == null) {
            // 不存在会话，创建会话
            thread = visitorThreadService.createWorkgroupThread(visitorRequest, workgroup, topic);
        }

        // 未强制转人工的情况下，判断是否转机器人
        if (!visitorRequest.getForceAgent()) {
            Boolean isOffline = !workgroup.isConnected();
            Boolean isInServiceTime = workgroup.getMessageLeaveSettings().isInServiceTime();
            Boolean transferToRobot = workgroup.getRobotSettings().shouldTransferToRobot(isOffline, isInServiceTime);
            if (transferToRobot) {
                // 转机器人
                RobotEntity robot = workgroup.getRobotSettings().getRobot();
                if (robot != null) {
                    thread = visitorThreadService.reInitRobotThreadExtra(thread, robot);
                    // 返回机器人欢迎消息
                    return routeToRobot(visitorRequest, thread, robot);
                } else {
                    throw new RuntimeException("Workgroup robot not found");
                }
            }
        }

        // 下面人工接待
        AgentEntity agent = workgroupRoutingService.selectAgent(workgroup, thread, workgroup.getAvailableAgents());
        if (agent == null) {
            return getOfflineMessage(visitorRequest, thread, workgroup);
        }
        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueWorkgroup(thread, agent, workgroup, visitorRequest);
        log.info("routeAgent Enqueued to queue {}", queueMemberEntity.getUid());
        //
        if (agent.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            // if (agent.canAcceptMore()) {
            if (queueMemberEntity.getQueue().getQueuingCount() < agent.getMaxThreadCount()) {
                // 未满则接待
                return handleAvailableWorkgroup(thread, agent, queueMemberEntity);
            } else {
                // 排队，已满则排队
                return handleQueuedWorkgroup(thread, agent, queueMemberEntity);
            }
        } else {
            // 离线状态永远显示离线提示语，不显示"继续会话"
            // 客服离线 或 非接待状态
            return getOfflineMessage(visitorRequest, thread, workgroup);
        }
    }

    private MessageProtobuf handleAvailableWorkgroup(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {
        // 未满则接待
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        ThreadEntity thread = threadOptional.get();

        String content = agent.getServiceSettings().getWelcomeTip();
        // 未满则接待
        thread.setUserUid(agent.getUid());
        thread.setStarted();
        thread.setUnreadCount(1);
        thread.setContent(content);
        thread.setOwner(agent.getMember().getUser());
        //
        UserProtobuf agentProtobuf = ServiceConvertUtils.convertToUserProtobuf(agent);
        thread.setAgent(JSON.toJSONString(agentProtobuf));
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        // 客服接待
        queueMemberEntity.setAcceptTime(LocalDateTime.now());
        queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
        queueMemberRestService.save(queueMemberEntity);
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(content, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        //
        applicationEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        //
        return messageProtobuf;
    }

    private MessageProtobuf handleQueuedWorkgroup(ThreadEntity threadFromRequest, AgentEntity agent,
            QueueMemberEntity queueMemberEntity) {

        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        ThreadEntity thread = threadOptional.get();

        // 排队，已满则排队
        // String queueTip = agent.getQueueSettings().getQueueTip();
        String content = "";
        if (queueMemberEntity.getQueue().getQueuingCount() == 0) {
            // 客服接待刚满员，下一个就是他，
            content = "请稍后，下一个就是您";
            // String content = String.format(queueTip, queueMemberEntity.getQueueNumber(),
            // queueMemberEntity.getWaitTime());
        } else {
            // 前面有排队人数
            content = " 当前排队人数：" + queueMemberEntity.getQueue().getQueuingCount() + " 大约等待时间："
                    + queueMemberEntity.getQueue().getQueuingCount() * 2 + "  分钟";
        }

        // 进入排队队列
        thread.setUserUid(agent.getUid());
        thread.setQueuing();
        thread.setUnreadCount(0);
        thread.setContent(content);
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getAgentThreadQueueMessage(agent, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        //
        applicationEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        //
        return messageProtobuf;
    }

    public MessageProtobuf getOfflineMessage(VisitorRequest visitorRequest, ThreadEntity threadFromRequest,
            WorkgroupEntity workgroup) {
        //
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");
        //
        ThreadEntity thread = threadOptional.get();
        thread.setClose()
                .setOffline()
                .setContent(workgroup.getMessageLeaveSettings().getMessageLeaveTip());
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        // 查询最新一条消息，如果距离当前时间不超过30分钟，则直接使用之前的消息，否则创建新的消息
        Optional<MessageEntity> messageOptional = messageRestService.findLatestByThreadUid(savedThread.getUid());
        if (messageOptional.isPresent()) {
            MessageEntity message = messageOptional.get();
            if (message.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(30))) {
                // 距离当前时间不超过30分钟，则直接使用之前的消息
                // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
                MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
                messageSendService.sendProtobufMessage(messageProtobuf);
                return messageProtobuf;
            }
        }
        // 创建新的留言消息
        MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(savedThread);
        messageRestService.save(message);
        // 返回留言消息
        // 部分用户测试的，离线状态收不到消息，以为是bug，其实不是，是离线状态不发送消息。防止此种情况，所以还是推送一下
        MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        messageSendService.sendProtobufMessage(messageProtobuf);
        //
        applicationEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        //
        return messageProtobuf;
    }

    // Q-原样返回会话
    private MessageProtobuf getWorkgroupContinueMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getWorkgroupContinueMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadContinueMessage(user, thread);
        // 微信公众号等渠道不能重复推送”继续会话“消息
        if (!visitorRequest.isWeChat()) {
            // 广播消息，由消息通道统一处理
            messageSendService.sendProtobufMessage(messageProtobuf);
        }
        //
        return messageProtobuf;
    }

    private MessageProtobuf getWorkgroupQueuingMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getWorkgroupQueuingMessage: user: {}, agent {}", user.toString(), thread.getAgent());
        //
        return ThreadMessageUtil.getThreadQueuingMessage(user, thread);
    }

    public MessageProtobuf routeToRobot(VisitorRequest request, @Nonnull ThreadEntity threadFromRequest,
            @Nonnull RobotEntity robot) {

        // 直接使用threadFromRequest，修改保存报错，所以重新查询，待完善
        Optional<ThreadEntity> threadOptional = threadService.findByUid(threadFromRequest.getUid());
        Assert.isTrue(threadOptional.isPresent(), "Thread with uid " + threadFromRequest.getUid() + " not found");

        ThreadEntity thread = threadOptional.get();
        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robot, request);
        log.info("routeRobot Enqueued to queue {}", queueMemberEntity.getUid());

        String content = robot.getServiceSettings().getWelcomeTip();
        // 更新线程状态
        thread.setUserUid(robot.getUid());
        thread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        thread.setAgent(ConvertAiUtils.convertToRobotProtobufString(robot));
        thread.setContent(content);
        thread.setUnreadCount(0);
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }

        // 更新排队状态
        // queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
        queueMemberEntity.setAcceptTime(LocalDateTime.now());
        queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
        queueMemberRestService.save(queueMemberEntity);

        return ThreadMessageUtil.getThreadRobotWelcomeMessage(content, savedThread);

    }

}
