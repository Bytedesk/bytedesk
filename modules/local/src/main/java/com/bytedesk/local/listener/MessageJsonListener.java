/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 16:13:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-16 19:57:03
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

import com.bytedesk.core.message.MessageJsonEvent;
import com.bytedesk.core.socket.service.MessageSocketService;
import com.bytedesk.core.socket.utils.MessageConvertUtils;
import com.bytedesk.local.caffeine.CaffeineCacheService;
import com.bytedesk.core.socket.protobuf.model.MessageProto;

// import com.bytedesk.core.socket.service.MessageSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageJsonListener implements ApplicationListener<MessageJsonEvent> {

    private final MessageSocketService messageSocketService;

    // private final RedisMessageCacheProtobufService
    // redisMessageCacheProtobufService;
    private final CaffeineCacheService caffeineCacheService;

    @Override
    public void onApplicationEvent(@NonNull MessageJsonEvent event) {
        log.info("MessageJsonListener {}", event.getJson());

        // TODO: 拦截被拉黑/屏蔽用户消息，并给与提示

        // TODO: 过滤敏感词，将敏感词替换为*

        // String filterJson = TabooUtil.replaceSensitiveWord(json, '*');
        String messageJson = event.getJson();
        // log.info("json {}, \nfilterJson {}, \nsize {}", json, filterJson,
        // TabooUtil.sensitiveWordMap.size());

        // MessageResponse messageResponse = JSON.parseObject(messageJson,
        // MessageResponse.class);
        // // 替换掉客户端时间戳，统一各个客户端时间戳，防止出现因为客户端时间戳不一致导致的消息乱序
        // messageResponse.setCreatedAt(new Date());
        // //
        // String newStompJson = JSON.toJSONString(messageResponse);
        // //
        messageSocketService.sendJsonMessage(messageJson);
        caffeineCacheService.push(messageJson);
        //
        try {
            // 转换为protobuf格式，转发到rabbitmq，发送给mqtt客户端
            MessageProto.Message message = MessageConvertUtils.toProtoBean(MessageProto.Message.newBuilder(),
                    messageJson);
            messageSocketService.sendProtoMessage(message);

            // TODO: 自动回复

            // TODO: 离线推送

            // TODO: 缓存消息
            // redisMessageCacheProtobufService.push(message.toByteArray());

            // TODO: webhook

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
