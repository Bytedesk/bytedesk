/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-31 16:23:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 12:00:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.utils;

import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadSequenceResponse;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class MessageUtils {
    

    public static void attachSequenceNumber(MessageExtra extra, String threadUid) {
        if (extra == null || !StringUtils.hasText(threadUid)) {
            return;
        }
        try {
            ThreadRestService threadRestService = ApplicationContextHolder.getBean(ThreadRestService.class);
            ThreadSequenceResponse response = threadRestService.allocateMessageMetadata(threadUid);
            if (response != null && response.getSequenceNumber() != null) {
                extra.setSequenceNumber(response.getSequenceNumber());
            }
        } catch (Exception ex) {
            log.warn("attachSequenceNumber failed for thread {}", threadUid, ex);
        }
    }

    public static MessageProtobuf createLoginNoticeMessage(String messageUid, ThreadProtobuf threadProtobuf, String orgUid, String content) {
        // 
        UserProtobuf system = UserUtils.getSystemUser();
        MessageExtra messageExtra = MessageExtra.fromOrgUid(orgUid);
        MessageUtils.attachSequenceNumber(messageExtra, threadProtobuf.getUid());
        // 
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .type(MessageTypeEnum.NOTICE)
                .content(content)
                .status(MessageStatusEnum.READ)
                .createdAt(BdDateUtils.now())
                .channel(ChannelEnum.SYSTEM)
                .thread(threadProtobuf)
                .user(system)
                .extra(messageExtra.toJson())
                .build();
        return message;
    }

    public static MessageProtobuf createNoticeMessage(String messageUid, ThreadProtobuf threadProtobuf, String orgUid, String content) {
        // 
        UserProtobuf system = UserUtils.getSystemUser();
        MessageExtra messageExtra = MessageExtra.fromOrgUid(orgUid);
        MessageUtils.attachSequenceNumber(messageExtra, threadProtobuf.getUid());
        // 
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .type(MessageTypeEnum.NOTICE)
                .content(content)
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(BdDateUtils.now())
                .channel(ChannelEnum.SYSTEM)
                .thread(threadProtobuf)
                .user(system)
                .extra(messageExtra.toJson())
                .build();
        return message;
    }
    
    public static MessageProtobuf createThreadMessage(String messageUid, ThreadEntity thread, MessageTypeEnum type, String content) {
        //
        UserProtobuf sender = UserUtils.getSystemUser();
        ThreadProtobuf threadProtobuf = thread.toProtobuf();
        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageUtils.attachSequenceNumber(extra, thread.getUid());
        //
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .type(type)
                .content(content)
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(BdDateUtils.now())
                .channel(ChannelEnum.SYSTEM)
                .thread(threadProtobuf)
                .user(sender)
                .extra(extra.toJson())
                .build();
        return message;
    }

    public static MessageProtobuf createAutoCloseMessage(ThreadEntity thread, String content) {
        return MessageUtils.createThreadMessage(UidUtils.getInstance().getUid(),
                thread,
                MessageTypeEnum.AUTO_CLOSED,
                content);
    }

    public static MessageProtobuf createAgentCloseMessage(ThreadEntity thread, String content) {
        return MessageUtils.createThreadMessage(UidUtils.getInstance().getUid(),
                thread,
                MessageTypeEnum.AGENT_CLOSED,
                content);
    }

    public static MessageProtobuf createAgentReplyTimeoutMessage(ThreadEntity thread, String content) {
        return MessageUtils.createThreadMessage(UidUtils.getInstance().getUid(),
                thread,
                MessageTypeEnum.NOTIFICATION_AGENT_REPLY_TIMEOUT,
                content);
    }

    public static MessageProtobuf createRateSubmittedMessage(ThreadEntity thread, String content) {
        return MessageUtils.createThreadMessage(UidUtils.getInstance().getUid(),
                thread,
                MessageTypeEnum.NOTIFICATION_RATE_SUBMITTED,
                content);
    }

    public static MessageProtobuf createRateInviteMessage(ThreadEntity thread, String content) {
        return MessageUtils.createThreadMessage(UidUtils.getInstance().getUid(),
                thread,
                MessageTypeEnum.RATE_INVITE,
                content);
    }

    public static MessageProtobuf createSystemMessage(ThreadEntity thread, String content) {
        return MessageUtils.createThreadMessage(UidUtils.getInstance().getUid(),
                thread,
                MessageTypeEnum.TEXT,
                content);
    }

    public static void notifyUser(MessageProtobuf messageProtobuf) {
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishMessageJsonEvent(messageProtobuf.toJson());
    }

    // public static MessageEntity getThreadMessage(String content, String type, String extra, String user, ThreadEntity thread) {

    //     MessageEntity message = MessageEntity.builder()
    //             .uid(UidUtils.getInstance().getUid())
    //             .content(content)
    //             .type(type)
    //             .status(MessageStatusEnum.READ.name())
    //             .channel(ChannelEnum.SYSTEM.name())
    //             .user(user)
    //             .orgUid(thread.getOrgUid())
    //             .createdAt(BdDateUtils.now())
    //             .updatedAt(BdDateUtils.now())
    //             .thread(thread)
    //             .extra(extra)
    //             .build();
        
    //     return message;
    // }

    
    
}
