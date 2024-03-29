/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 16:13:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 12:30:42
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

import com.bytedesk.core.constant.ClientConsts;
import com.bytedesk.core.constant.ThreadTypeConsts;
import com.bytedesk.core.event.MessageBytesEvent;
import com.bytedesk.socket.protobuf.model.MessageProto;
import com.bytedesk.socket.redis.RedisMessageCacheProtobufService;
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

    private final RedisMessageCacheProtobufService redisMessageCacheProtobufService;

    @Override
    public void onApplicationEvent(@NonNull MessageBytesEvent event) {

        // TODO: 拦截被拉黑/屏蔽用户消息，并给与提示

        // TODO: 过滤敏感词，将敏感词替换为*
        
        //
        try {
            //
            MessageProto.Message messageProto = MessageProto.Message.parseFrom(event.getMessageBytes());
            // String messageType = messageProto.getType();
            String threadType = messageProto.getThread().getType();

            try {
                /*
                {
                    "mid": "33f4ff7661ad416083ae8d2f62156b4d",
                    "timestamp": "2024-02-28 16:37:11",
                    "client": "electron",
                    "version": "1",
                    "type": "text",
                    "user": {
                        "uid": "95e7ae58d2904f0d867233b7dc4e59ac",
                        "username": "13311156271",
                        "nickname": "jack71",
                        "avatar": "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png"
                    },
                    "text": {
                        "content": "1"
                    },
                    "thread": {
                        "tid": "d06f004a4e304fe1a62f15015a93c3ee",
                        "type": "member",
                        "client": "electron",
                        "nickname": "jack73",
                        "avatar": "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png",
                        "content": "1",
                        "timestamp": "2024-02-28 16:37:11",
                        "topic": "95d80f0668564bfc9bc058bbf5149287"
                    }
                }
                 */
                log.debug("MessageBytesListener onApplicationEvent json {}", MessageConvertUtils.toJson(messageProto));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //
            if (threadType.equals(ThreadTypeConsts.APPOINTED)
                || threadType.equals(ThreadTypeConsts.WORKGROUP)) {

                String threadClient = messageProto.getThread().getClient();
                if (threadClient.contains(ClientConsts.CLIENT_WEB)) {
                    log.debug("MessageBytesListener onApplicationEvent web chat {}", threadClient);
                    // JSON
                    try {
                        String json = MessageConvertUtils.toJson(messageProto);
                        log.info("proto to json {}", json);
                        messageSocketService.sendJsonMessage(json);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return;
                }
            }
            // protobuf
            messageSocketService.sendProtoMessage(messageProto);

            // TODO: 自动回复

            // TODO: 离线推送

            // TODO: 缓存消息
            redisMessageCacheProtobufService.push(event.getMessageBytes());

            // TODO: webhook
            


        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

}
