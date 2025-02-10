/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 18:59:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-10 22:37:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing;

import java.time.LocalDateTime;

// import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberAcceptTypeEnum;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRoutingService;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Primary
@Service
@AllArgsConstructor
public class RouteService {

    private final ThreadRestService threadService;

    private final IMessageSendService messageSendService;

    private final AgentRestService agentRestService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService; ;

    private final MessageRestService messageRestService;

    private final WorkgroupRoutingService workgroupRoutingService;

    private final RobotRestService robotRestService;

    public MessageProtobuf routeToRobot(VisitorRequest request, @Nonnull ThreadEntity thread,
            @Nonnull RobotEntity robot) {
        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robot, request);
        log.info("routeRobot Enqueued to queue {}", queueMemberEntity.toString());
        //
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        thread.setRobot(true);
        thread.setUnreadCount(0);
        thread.setType(ThreadTypeEnum.ROBOT.name());
        threadService.save(thread);
        // 增加接待数量，待优化
        robot.increaseThreadCount();
        robotRestService.save(robot);
        // 更新排队状态，待优化
        queueMemberEntity.setStatus(QueueMemberStatusEnum.SERVING.name());
        queueMemberEntity.setAcceptTime(LocalDateTime.now());
        queueMemberEntity.setAcceptType(QueueMemberAcceptTypeEnum.AUTO.name());
        queueMemberRestService.save(queueMemberEntity);
        //
        return ThreadMessageUtil.getThreadRobotWelcomeMessage(thread);
    }

    public MessageProtobuf routeToAgent(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread,
            @Nonnull AgentEntity agent) {
        // log.info("RouteService routeAgent: {}", agent.getUid());
        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueAgent(thread, agent, visitorRequest);
        log.info("routeAgent Enqueued to queue {}", queueMemberEntity.toString());
        MessageProtobuf messageProtobuf = null;
        // 判断客服是否在线且接待状态
        if (agent.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            // 判断是否达到最大接待人数，如果达到则进入排队
            if (agent.canAcceptMore()) {
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
                messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agent, thread);
                messageSendService.sendProtobufMessage(messageProtobuf);
            } else {
                // 已满则排队
                // String queueTip = agent.getQueueSettings().getQueueTip();
                String content = "";
                if (queueMemberEntity.getBeforeNumber() == 0) {
                    // 客服接待刚满员，下一个就是他，
                    content = "请稍后，下一个就是您";
                    // String content = String.format(queueTip, queueMemberEntity.getQueueNumber(), queueMemberEntity.getWaitTime());
                } else {
                    // 前面有排队人数
                    content = " 当前排队人数：" + queueMemberEntity.getBeforeNumber() + " 大约等待时间：" + queueMemberEntity.getBeforeNumber() * 2 + "  分钟";
                }
                
                // 进入排队队列
                thread.setQueuing();
                thread.setUnreadCount(0);
                thread.setContent(content);
                thread.setQueueNumber(queueMemberEntity.getQueueNumber());
                // 
                messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(agent, thread);
                messageSendService.sendProtobufMessage(messageProtobuf);
            }
            threadService.save(thread);
            //
            return messageProtobuf;
        } else {
            // 客服离线或小休不接待状态，则进入留言
            thread.setOffline();
            thread.setUnreadCount(0);
            thread.setContent(agent.getLeaveMsgSettings().getLeaveMsgTip());
            thread.setQueueNumber(queueMemberEntity.getQueueNumber());
            threadService.save(thread);
            //
            MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(agent, thread);
            // 保存留言消息
            messageRestService.save(message);
            // 返回留言消息
            return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
        }
    }

    public MessageProtobuf routeToWorkgroup(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        log.info("RouteServiceImplVip routeWorkgroup: {}", workgroup.getUid());
        if (workgroup.getAgents().isEmpty()) {
            throw new RuntimeException("No agents found in workgroup with uid " + workgroup.getUid());
        }
        // 下面人工接待
        AgentEntity agent = workgroupRoutingService.selectAgent(workgroup, thread, workgroup.getAvailableAgents());
        if (agent == null) {
            return getOfflineMessage(visitorRequest, thread, workgroup);
        }
        // 排队计数
        QueueMemberEntity queueMemberEntity = queueService.enqueueWorkgroup(thread, agent, workgroup, visitorRequest);
        MessageProtobuf messageProtobuf = null;
        log.info("routeAgent Enqueued to queue {}", queueMemberEntity.toString());
        //
        if (agent.isConnectedAndAvailable()) {
            // 客服在线 且 接待状态
            if (agent.canAcceptMore()) {
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
                thread.setOwner(agent.getMember().getUser());
                UserProtobuf agentProtobuf = ConvertServiceUtils.convertToUserProtobuf(agent);
                thread.setAgent(JSON.toJSONString(agentProtobuf));
                // 
                messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agent, thread);
                messageSendService.sendProtobufMessage(messageProtobuf);
            } else {
                // 排队，已满则排队
                // String queueTip = agent.getQueueSettings().getQueueTip();
                String content = "";
                if (queueMemberEntity.getBeforeNumber() == 0) {
                    // 客服接待刚满员，下一个就是他，
                    content = "请稍后，下一个就是您";
                    // String content = String.format(queueTip, queueMemberEntity.getQueueNumber(), queueMemberEntity.getWaitTime());
                } else {
                    // 前面有排队人数
                    content = " 当前排队人数：" + queueMemberEntity.getBeforeNumber() + " 大约等待时间：" + queueMemberEntity.getBeforeNumber() * 2 + "  分钟";
                }
                
                // 进入排队队列
                thread.setQueuing();
                thread.setUnreadCount(0);
                thread.setContent(content);
                thread.setQueueNumber(queueMemberEntity.getQueueNumber());
                // 
                messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(agent, thread);
                messageSendService.sendProtobufMessage(messageProtobuf);
            }
            // 
            threadService.save(thread);

            return messageProtobuf;
        } else {
            // 离线状态永远显示离线提示语，不显示“继续会话”
            // 客服离线 或 非接待状态
            return getOfflineMessage(visitorRequest, thread, workgroup);
        }
    }

    public MessageProtobuf getOfflineMessage(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        thread.setOffline();
        thread.setContent(workgroup.getLeaveMsgSettings().getLeaveMsgTip());
        threadService.save(thread);
        //
        MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(workgroup, thread);
        // 保存留言消息
        messageRestService.save(message);
            // 返回留言消息
            return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
    }



}
