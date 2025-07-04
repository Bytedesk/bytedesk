/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-13 10:04:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:19:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import org.springframework.stereotype.Service;

import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessagePersistCache messagePersistCache;

    public String processMessageJson(String messageJson) {

        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageJson); 

        // 收到消息，更新消息状态为发送成功
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            messageProtobuf.setStatus(MessageStatusEnum.SUCCESS);
        }

        // 防止客户端时间错误，使用服务器时间戳，指定Asia/Shanghai时区
        messageProtobuf.setCreatedAt(BdDateUtils.now());

        // 保存消息
        // Cache message for persistence
        messagePersistCache.pushForPersist(messageJson);

        return messageJson;
    }


}
