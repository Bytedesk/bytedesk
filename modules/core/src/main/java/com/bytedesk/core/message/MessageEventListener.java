/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 16:02:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-17 17:30:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.io.IOException;
import java.time.LocalDateTime;
// import java.util.Date;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.utils.MessageConvertUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageEventListener {

    private final MessagePersistCache messagePersistCache;

    private final MessagePersistService messagePersistService;

    private final MessageSocketService messageSocketService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        log.info("MessageJsonEvent {}", event.getJson());
        //
        String messageJson = event.getJson();
        //
        messageJson = processMessage(messageJson);
        messageSocketService.sendJsonMessage(messageJson);
        //
        try {
            MessageProto.Message message = MessageConvertUtils.toProtoBean(MessageProto.Message.newBuilder(),
                    messageJson);
            messageSocketService.sendProtoMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processMessage(String messageJson) {
        // log.info("processMessage {}", messageJson);
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            messageProtobuf.setStatus(MessageStatusEnum.SUCCESS);
        }
        //
        ThreadProtobuf thread = messageProtobuf.getThread();
        if (thread == null) {
            throw new RuntimeException("thread is null");
        }
        // 替换掉客户端时间戳，统一各个客户端时间戳，防止出现因为客户端时间戳不一致导致的消息乱序
        messageProtobuf.setCreatedAt(LocalDateTime.now());

        // 1. 拦截黑名单用户消息
        // 2. 过滤敏感词，将敏感词替换为*
        // String filterJson = TabooUtil.replaceSensitiveWord(json, '*');
        // 3. 自动回复
        // 4. 关键词回复
        // 5. 大模型回复

        String msgJson = JSON.toJSONString(messageProtobuf);
        // 缓存消息，用于定期持久化到数据库
        messagePersistCache.pushForPersist(msgJson);
        //
        return msgJson;
    }

    @EventListener
    public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
        // log.info("message quartz five second event: " + event);
        List<String> messageJsonList = messagePersistCache.getListForPersist();
        if (messageJsonList == null || messageJsonList.isEmpty()) {
            return;
        }
        messageJsonList.forEach(item -> {
            messagePersistService.persist(item);
        });
    }

}
