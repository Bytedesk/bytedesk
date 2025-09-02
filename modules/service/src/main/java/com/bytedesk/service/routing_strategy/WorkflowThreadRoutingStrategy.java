/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 17:36:09
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
import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.ai.workflow.WorkflowRestService;
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
 * 工作流线程路由策略
 * 
 * <p>功能特点：
 * - 基于工作流的自动化对话处理
 * - 不支持转人工，完全自动化流程
 * - 支持会话复用和状态管理
 * 
 * <p>处理流程：
 * 1. 验证工作流配置
 * 2. 检查现有会话状态
 * 3. 创建或复用工作流会话
 * 4. 启动工作流处理
 * 
 * @author jackning 270580156@qq.com
 * @since 1.0.0
 */
@Slf4j
@Component("workflowThreadStrategy")
@AllArgsConstructor
public class WorkflowThreadRoutingStrategy extends AbstractThreadRoutingStrategy {

    private final WorkflowRestService workflowRestService;
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
        return executeWithExceptionHandling("create workflow thread", visitorRequest.getSid(),
                () -> createWorkflowThread(visitorRequest));
    }

    /**
     * 创建工作流会话
     * 
     * @param request 访客请求
     * @return 消息协议对象
     */
    public MessageProtobuf createWorkflowThread(VisitorRequest request) {
        // 1. 验证和获取工作流配置
        WorkflowEntity workflowEntity = getWorkflowEntity(request.getSid());
        
        // 2. 生成会话主题并检查现有会话
        String topic = TopicUtils.formatOrgWorkflowThreadTopic(workflowEntity.getUid(), request.getUid());
        // 
        ThreadEntity thread = getOrCreateWorkflowThread(request, workflowEntity, topic);
        
        // 3. 处理现有活跃会话
        if (isExistingWorkflowThread(thread)) {
            return handleExistingWorkflowThread(workflowEntity, thread);
        }
        
        // 4. 处理新会话或重新激活的会话
        return processNewWorkflowThread(request, thread, workflowEntity);
    }

    /**
     * 获取工作流实体
     */
    private WorkflowEntity getWorkflowEntity(String workflowUid) {
        validateUid(workflowUid, "Workflow");
        
        return workflowRestService.findByUid(workflowUid)
                .orElseThrow(() -> {
                    log.error("Workflow uid {} not found", workflowUid);
                    return new IllegalArgumentException("Workflow uid " + workflowUid + " not found");
                });
    }

    /**
     * 获取或创建工作流会话
     */
    private ThreadEntity getOrCreateWorkflowThread(VisitorRequest request, WorkflowEntity workflowEntity, String topic) {
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        
        if (threadOptional.isPresent()) {
            ThreadEntity existingThread = threadOptional.get();
            
            // 检查现有会话状态
            if (existingThread.isNew()) {
                log.debug("Found new workflow thread: {}", topic);
                return existingThread;
            } else if (existingThread.isChatting()) {
                log.debug("Found existing chatting workflow thread: {}", topic);
                // 重新初始化会话用于测试
                return visitorThreadService.reInitWorkflowThreadExtra(existingThread, workflowEntity);
            }
        }

        // 创建新会话
        log.debug("Creating new workflow thread for topic: {}", topic);
        return visitorThreadService.createWorkflowThread(request, workflowEntity, topic);
    }

    /**
     * 检查是否为现有的工作流会话
     */
    private boolean isExistingWorkflowThread(ThreadEntity thread) {
        return thread.isChatting() && !thread.isNew();
    }

    /**
     * 处理现有的工作流会话
     */
    private MessageProtobuf handleExistingWorkflowThread(WorkflowEntity workflowEntity, ThreadEntity thread) {
        log.info("Continuing existing workflow thread: {}", thread.getUid());
        return getWorkflowContinueMessage(workflowEntity, thread);
    }

    /**
     * 处理新的工作流会话
     */
    private MessageProtobuf processNewWorkflowThread(VisitorRequest request, ThreadEntity thread, WorkflowEntity workflowEntity) {
        // 1. 加入队列
        UserProtobuf workflowProtobuf = ServiceConvertUtils.convertToUserProtobuf(workflowEntity);
        QueueMemberEntity queueMemberEntity = queueService.enqueueRobot(thread, workflowProtobuf, request);
        log.info("Workflow enqueued to queue: {}", queueMemberEntity.getUid());

        // 2. 配置线程状态
        String welcomeContent = getWorkflowWelcomeMessage();
        thread.setChatting().setContent(welcomeContent);
        
        // 3. 设置工作流信息
        String workflowString = ServiceConvertUtils.convertToUserProtobufString(workflowEntity);
        thread.setRobot(workflowString);
        
        // 4. 保存线程
        ThreadEntity savedThread = saveThread(thread);
        
        // 5. 更新队列状态
        updateQueueMemberForWorkflow(queueMemberEntity);
        
        // 6. 发布事件
        publishWorkflowThreadEvent(savedThread);
        
        // 7. 创建并保存欢迎消息
        return createAndSaveWelcomeMessage(welcomeContent, savedThread);
    }

    /**
     * 获取工作流欢迎消息
     * 工作流使用默认欢迎消息
     */
    private String getWorkflowWelcomeMessage() {
        return DEFAULT_WELCOME_MESSAGE;
    }

    /**
     * 更新队列成员状态为工作流自动接受
     */
    private void updateQueueMemberForWorkflow(QueueMemberEntity queueMemberEntity) {
        try {
            queueMemberEntity.robotAutoAcceptThread();
            queueMemberRestService.save(queueMemberEntity);
            log.debug("Updated queue member status for workflow auto-accept: {}", queueMemberEntity.getUid());
        } catch (Exception e) {
            log.error("Failed to update queue member for workflow auto-accept: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update queue member status", e);
        }
    }

    /**
     * 发布工作流线程事件
     */
    private void publishWorkflowThreadEvent(ThreadEntity savedThread) {
        try {
            applicationEventPublisher.publishEvent(new ThreadProcessCreateEvent(this, savedThread));
            log.debug("Published thread process create event for workflow thread: {}", savedThread.getUid());
        } catch (Exception e) {
            log.warn("Failed to publish thread event for workflow thread {}: {}", savedThread.getUid(), e.getMessage());
        }
    }

    /**
     * 创建并保存工作流欢迎消息
     */
    private MessageProtobuf createAndSaveWelcomeMessage(String content, ThreadEntity thread) {
        try {
            MessageEntity message = ThreadMessageUtil.getThreadWorkflowWelcomeMessage(content, thread);
            messageRestService.save(message);
            
            MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
            log.debug("Created workflow welcome message for thread: {}", thread.getUid());
            
            return messageProtobuf;
        } catch (Exception e) {
            log.error("Failed to create welcome message for workflow thread {}: {}", thread.getUid(), e.getMessage(), e);
            throw new RuntimeException("Failed to create welcome message", e);
        }
    }

    /**
     * 获取工作流继续对话消息
     */
    private MessageProtobuf getWorkflowContinueMessage(WorkflowEntity workflowEntity, @Nonnull ThreadEntity thread) {
        validateThread(thread, "get workflow continue message");
        
        String content = getWorkflowWelcomeMessage();
        MessageEntity message = ThreadMessageUtil.getThreadWorkflowWelcomeMessage(content, thread);
        
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }
}
