/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 16:13:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-16 17:59:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.local.listener;

import java.io.IOException;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageBytesEvent;
import com.bytedesk.local.caffeine.CaffeineCacheService;
import com.bytedesk.socket.protobuf.model.MessageProto;
import com.bytedesk.socket.service.MessageSocketService;
import com.bytedesk.socket.utils.MessageConvertUtils;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageBytesListener implements ApplicationListener<MessageBytesEvent> {

    private final MessageSocketService messageSocketService;

    // private final RedisMessageCacheProtobufService redisMessageCacheProtobufService;

    private final CaffeineCacheService caffeineCacheService;

    @Override
    public void onApplicationEvent(@NonNull MessageBytesEvent event) {

        // TODO: 拦截被拉黑/屏蔽用户消息，并给与提示

        // TODO: 过滤敏感词，将敏感词替换为*
        
        //
        try {
            //
            MessageProto.Message messageProto = MessageProto.Message.parseFrom(event.getMessageBytes());
            // JSON
            try {
                String json = MessageConvertUtils.toJson(messageProto);
                caffeineCacheService.push(json);
                log.info("proto to json {}", json);
                messageSocketService.sendJsonMessage(json);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //
            // protobuf
            messageSocketService.sendProtoMessage(messageProto);

            // TODO: 自动回复

            // TODO: 离线推送

            // TODO: 缓存消息
            // redisMessageCacheProtobufService.push(event.getMessageBytes());

            // TODO: webhook

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

}
