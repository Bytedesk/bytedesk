/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-31 16:23:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 14:24:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.time.LocalDateTime;
// import java.util.Date;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.utils.ApplicationContextHolder;

public class MessageUtils {
    
    public static MessageExtra getMessageExtra(String orgUid) {
        return MessageExtra.builder().orgUid(orgUid).build();
    }

    public static MessageProtobuf createNoticeMessage(String messageUid, ThreadProtobuf threadProtobuf, String orgUid, String content) {
        // 
        UserProtobuf sender = UserUtils.getSystemChannelUser();
        // 
        MessageExtra messageExtra = MessageUtils.getMessageExtra(orgUid);
        String extra = JSON.toJSONString(messageExtra);
        // 
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .type(MessageTypeEnum.NOTICE)
                .content(content)
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(LocalDateTime.now())
                .client(ClientEnum.SYSTEM)
                .thread(threadProtobuf)
                .user(sender)
                .extra(extra)
                .build();
        return message;
    }
    
    public static MessageProtobuf createThreadMessage(String messageUid, ThreadEntity thread, MessageTypeEnum type, String content) {
        //
        UserProtobuf sender = UserUtils.getSystemChannelUser();
        // 
        ThreadProtobuf threadProtobuf = thread.toProtobuf();
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        //
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .type(type)
                .content(content)
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(LocalDateTime.now())
                .client(ClientEnum.SYSTEM)
                .thread(threadProtobuf)
                .user(sender)
                .extra(JSON.toJSONString(extra))
                .build();
        return message;
    }

    public static void notifyUser(MessageProtobuf messageProtobuf) {
        String json = JSON.toJSONString(messageProtobuf);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishMessageJsonEvent(json);
    }
    
}
