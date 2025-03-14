/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-13 10:04:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 11:46:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.black.BlackService;
import com.bytedesk.core.thread.ThreadProtobuf;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessagePersistCache messagePersistCache;

    private final BlackService blackService;

    public String processMessageJson(String messageJson) {

        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            messageProtobuf.setStatus(MessageStatusEnum.SUCCESS);
        }

        ThreadProtobuf thread = messageProtobuf.getThread();
        if (thread == null) {
            throw new RuntimeException("thread is null");
        }

        // Replace client timestamp
        messageProtobuf.setCreatedAt(LocalDateTime.now());

        // Check blacklist
        if (blackService.isBlackList(messageProtobuf)) {
            return null;
        }

        // Filter sensitive words
        messageJson = filterTaboo(JSON.toJSONString(messageProtobuf));

        // Cache message for persistence
        messagePersistCache.pushForPersist(messageJson);

        return messageJson;
    }

    // 过滤敏感词
    private String filterTaboo(String messageJson) {
        // TODO: 过滤敏感词，将敏感词替换为*
        // String filterJson = TabooUtil.replaceSensitiveWord(json, '*');
        return messageJson;
    }

}
