/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 11:22:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 11:57:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for handling notice operations
 * 
 * @author jackning 270580156@qq.com
 * @since 2024-12-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final ThreadRestService threadRestService;
    private final IMessageSendService messageSendService;
    private final UidUtils uidUtils;
    private final ModelMapper modelMapper;

    /**
     * Send a general notice to a user
     * 
     * @param request the notice request
     * @throws IllegalArgumentException if request is null or invalid
     */
    public void sendNotice(NoticeRequest request) {
        validateNoticeRequest(request);
        log.debug("Sending notice for user: {}", request.getUserUid());
        
        try {
            sendNoticeMessage(request, MessageType.GENERAL);
            log.info("Notice sent successfully for user: {}", request.getUserUid());
        } catch (Exception e) {
            log.error("Failed to send notice for user: {}", request.getUserUid(), e);
            throw new RuntimeException("Failed to send notice", e);
        }
    }

    /**
     * Send a login notice to a user
     * 
     * @param request the notice request
     * @throws IllegalArgumentException if request is null or invalid
     */
    public void sendLoginNotice(NoticeRequest request) {
        validateNoticeRequest(request);
        log.debug("Sending login notice for user: {}", request.getUserUid());
        
        try {
            sendNoticeMessage(request, MessageType.LOGIN);
            log.info("Login notice sent successfully for user: {}", request.getUserUid());
        } catch (Exception e) {
            log.error("Failed to send login notice for user: {}", request.getUserUid(), e);
            throw new RuntimeException("Failed to send login notice", e);
        }
    }

    /**
     * Send notice message based on the message type
     * 
     * @param request the notice request
     * @param messageType the type of message to create
     */
    private void sendNoticeMessage(NoticeRequest request, MessageType messageType) {
        // Convert request to protobuf
        NoticeProtobuf noticeProtobuf = modelMapper.map(request, NoticeProtobuf.class);
        String jsonContent = noticeProtobuf.toJson();
        
        // Get system topic for the user
        String topic = TopicUtils.getSystemTopic(request.getUserUid());
        
        // Find thread for the topic
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isEmpty()) {
            log.warn("No thread found for topic: {}, user: {}", topic, request.getUserUid());
            return;
        }
        
        ThreadEntity thread = threadOptional.get();
        
        // Create and send message based on type
        MessageProtobuf message = createMessageByType(messageType, thread, request, jsonContent);
        messageSendService.sendProtobufMessage(message);
    }

    /**
     * Create message based on the message type
     * 
     * @param messageType the type of message
     * @param thread the thread entity
     * @param request the notice request
     * @param jsonContent the JSON content
     * @return the created message
     */
    private MessageProtobuf createMessageByType(MessageType messageType, ThreadEntity thread, 
                                              NoticeRequest request, String jsonContent) {
        return switch (messageType) {
            case GENERAL -> MessageUtils.createNoticeMessage(
                uidUtils.getUid(), thread.toProtobuf(), request.getOrgUid(), jsonContent);
            case LOGIN -> MessageUtils.createLoginNoticeMessage(
                uidUtils.getUid(), thread.toProtobuf(), request.getOrgUid(), jsonContent);
        };
    }

    /**
     * Validate the notice request
     * 
     * @param request the notice request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    private void validateNoticeRequest(NoticeRequest request) {
        Assert.notNull(request, "Notice request cannot be null");
        Assert.hasText(request.getUserUid(), "User UID cannot be null or empty");
        Assert.hasText(request.getOrgUid(), "Organization UID cannot be null or empty");
    }

    /**
     * Enum for message types
     */
    private enum MessageType {
        GENERAL,
        LOGIN
    }
}
