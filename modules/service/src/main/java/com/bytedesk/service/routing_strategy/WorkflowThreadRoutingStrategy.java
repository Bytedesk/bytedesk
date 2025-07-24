/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 13:04:55
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
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.ConvertUtils;
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

    private final ThreadRestService threadRestService;

    private final VisitorThreadService visitorThreadService;

    private final QueueService queueService;

    private final QueueMemberRestService queueMemberRestService;

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
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            // 
            if (threadOptional.get().isNew()) {
                thread = threadOptional.get();
            } else if (threadOptional.get().isChatting()) {
                thread = threadOptional.get();
                // 
                thread = visitorThreadService.reInitWorkflowThreadExtra(thread, workflowEntity); // 方便测试
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing robot thread {}", topic);
                return getWorkflowContinueMessage(workflowEntity, thread);
            }
        }

        // 如果会话不存在，或者会话已经关闭，则创建新的会话
        if (thread == null) {
            thread = visitorThreadService.createWorkflowThread(request, workflowEntity, topic);
        }

        // 排队计数
        UserProtobuf workflowProtobuf = ConvertUtils.convertToUserProtobuf(workflowEntity);
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, workflowProtobuf, request);
        log.info("routeWorkflow Enqueued to queue {}", queueMemberEntity.getUid());

        String content = "您好，请问有什么可以帮助您？";
        thread.setChatting().setContent(content).setUnreadCount(0);
        // 
        String workflowString = ConvertUtils.convertToUserProtobufString(workflowEntity);
        thread.setRobot(workflowString);
        // 
        ThreadEntity savedThread = threadRestService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("Failed to save thread");
        }
        // 更新排队状态
        queueMemberEntity.robotAutoAcceptThread();
        queueMemberRestService.save(queueMemberEntity);
        // 
        applicationEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
        // 
        MessageEntity message = ThreadMessageUtil.getThreadWorkflowWelcomeMessage(content, savedThread);
        messageRestService.save(message);

        return ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
    }

    private MessageProtobuf getWorkflowContinueMessage(WorkflowEntity workflow, @Nonnull ThreadEntity thread) {

        String content = "您好，请问有什么可以帮助您？";
        MessageEntity message = ThreadMessageUtil.getThreadWorkflowWelcomeMessage(content, thread);

        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

}
