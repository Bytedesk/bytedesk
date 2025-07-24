/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 09:33:58
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
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.core.workflow.WorkflowRestService;
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

// 工作流对话策略器人
@Slf4j
@Component("workflowThreadStrategy")
@AllArgsConstructor
public class WorkflowThreadRoutingStrategy implements ThreadRoutingStrategy {

    private final WorkflowRestService workflowRestService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService;;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MessageRestService messageRestService;

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return createWorkflowThread(visitorRequest);
    }

    // 工作流对话，不支持转人工
    public MessageProtobuf createWorkflowThread(VisitorRequest request) {
        String workflowUid = request.getSid();
        WorkflowEntity workflowEntity = workflowRestService.findByUid(workflowUid)
                .orElseThrow(() -> new RuntimeException("Workflow uid " + workflowUid + " not found"));
        //
        String topic = TopicUtils.formatOrgWorkflowThreadTopic(workflowEntity.getUid(), request.getUid());
        ThreadEntity thread = null;
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            // 
            if (threadOptional.get().isNew()) {
                thread = threadOptional.get();
            } else if (threadOptional.get().isRoboting()) {
                thread = threadOptional.get();
                // 
                thread = visitorThreadService.reInitRobotThreadExtra(thread, workflowEntity); // 方便测试
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing robot thread {}", topic);
                return getRobotContinueMessage(robotEntity, thread);
            }
        }

        // 如果会话不存在，或者会话已经关闭，则创建新的会话
        if (thread == null) {
            thread = visitorThreadService.createRobotThread(request, robotEntity, topic);
        }

        // 排队计数
        UserProtobuf robotProtobuf = robotEntity.toUserProtobuf();
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, robotProtobuf, request);
        log.info("routeRobot Enqueued to queue {}", queueMemberEntity.getUid());

        String content = robotEntity.getServiceSettings().getWelcomeTip();
        if (content == null || content.isEmpty()) {
            content = "您好，请问有什么可以帮助您？";
        }
        // 更新线程状态
        thread.setRoboting().setContent(content).setUnreadCount(0);
        // 
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robotEntity);
        thread.setRobot(robotString);
        // 
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        // 更新排队状态
        queueMemberEntity.robotAutoAcceptThread();
        queueMemberRestService.save(queueMemberEntity);
        // 
        applicationEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        // 
        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(content, savedThread);
        messageRestService.save(message);

        return ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
    }

    private MessageProtobuf getWorkflowContinueMessage(WorkflowEntity workflow, @Nonnull ThreadEntity thread) {

        String content = "您好，请问有什么可以帮助您？";
        MessageEntity message = ThreadMessageUtil.getThreadRobotWelcomeMessage(content, thread);

        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

}
