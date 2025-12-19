/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-18 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-18 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.visitor.VisitorRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工单线程路由策略
 * 
 * <p>功能特点：
 * - 根据传入的 threadUid 直接加载现有会话
 * - 支持工单对话的查看和继续
 * - 不创建新会话，仅加载已存在的工单会话
 * 
 * <p>处理流程：
 * 1. 验证 threadUid 参数
 * 2. 根据 threadUid 查找会话
 * 3. 返回会话信息和最后一条消息
 * 
 * @author jackning 270580156@qq.com
 * @since 1.0.0
 */
@Slf4j
@Component("ticketThreadStrategy")
@AllArgsConstructor
public class TicketThreadRoutingStrategy extends AbstractThreadRoutingStrategy {

    private final ThreadRestService threadRestService;
    private final MessageRestService messageRestService;

    @Override
    protected ThreadRestService getThreadRestService() {
        return threadRestService;
    }

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return executeWithExceptionHandling("load ticket thread", visitorRequest.getSid(),
                () -> loadTicketThread(visitorRequest));
    }

    /**
     * 加载工单会话
     * 
     * <p>根据 VisitorRequest 中的 sid（作为 threadUid）加载已存在的工单会话
     * 
     * @param visitorRequest 访客请求，sid 字段作为 threadUid 使用
     * @return 消息协议对象，包含会话信息
     * @throws IllegalArgumentException 如果 threadUid 为空或会话不存在
     */
    public MessageProtobuf loadTicketThread(VisitorRequest visitorRequest) {
        // 1. 获取 threadUid (使用 sid 字段)
        String threadUid = visitorRequest.getSid();
        
        // 2. 验证 threadUid
        if (!StringUtils.hasText(threadUid)) {
            log.error("Thread UID is required for ticket thread routing");
            throw new IllegalArgumentException("Thread UID is required for ticket thread routing");
        }
        
        log.debug("Loading ticket thread with uid: {}", threadUid);
        
        // 3. 查找会话
        ThreadEntity thread = getThreadByUid(threadUid);
        
        // 4. 验证会话类型（可选：确保是工单类型）
        // if (!ThreadTypeEnum.TICKET.name().equals(thread.getType())) {
        //     log.warn("Thread {} is not a ticket thread, type: {}", threadUid, thread.getType());
        // }
        
        // 5. 返回会话消息
        return getTicketThreadMessage(thread);
    }

    /**
     * 构建工单会话消息响应
     * 
     * @param thread 工单会话实体
     * @return 消息协议对象
     */
    private MessageProtobuf getTicketThreadMessage(ThreadEntity thread) {
        // 查找该会话的最后一条消息
        Optional<MessageEntity> lastMessageOptional = messageRestService.findLatestByThreadUid(thread.getUid());
        
        if (lastMessageOptional.isPresent()) {
            MessageEntity lastMessage = lastMessageOptional.get();
            log.debug("Found last message for ticket thread {}: {}", thread.getUid(), lastMessage.getUid());
            return ServiceConvertUtils.convertToMessageProtobuf(lastMessage, thread);
        }
        
        // 如果没有消息，返回一个空消息但包含会话信息
        log.debug("No messages found for ticket thread {}, returning thread info only", thread.getUid());
        return buildEmptyMessageProtobuf(thread);
    }

    /**
     * 构建空消息协议对象（仅包含会话信息）
     * 
     * @param thread 会话实体
     * @return 消息协议对象
     */
    private MessageProtobuf buildEmptyMessageProtobuf(ThreadEntity thread) {
        MessageProtobuf messageProtobuf = new MessageProtobuf();
        messageProtobuf.setThread(ConvertUtils.convertToThreadProtobuf(thread));
        return messageProtobuf;
    }
}
