/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 11:25:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

// import com.bytedesk.core.annotation.TabooFilter;
import com.bytedesk.core.message.IMessageSendService;
// import com.bytedesk.core.socket.MqService;

import java.security.Principal;

/**
 * https://docs.spring.io/spring-framework/reference/web/websocket/stomp/handle-annotations.html
 *
 * @author bytedesk.com
 */
@AllArgsConstructor
@Slf4j
@Controller
public class StompController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final IMessageSendService messageSendService;

    /**
     * stompClient.publish('/app/sid.uid', message)
     * 访客端发送消息
     * 
     * @param principal principal
     * @param sid       agent.uid or workgroup.wid
     * @param uid       visitor.uid
     * @param message   content
     */
    @MessageMapping("/{sid}.{uid}")
    // @TabooFilter(value = "message", throwException = false)
    public void message(Principal principal,
            @DestinationVariable(value = "sid") String sid,
            @DestinationVariable(value = "uid") String uid,
            String message) {
        // principal: null, sid: org.workgroup.df_wg_uid, uid: 1513088171901063, message:
        log.debug("principal: {}, sid: {}, uid: {}, message: {}", principal, sid, uid, message);
        // TODO: 发送回执
        // 转发给mq
        messageSendService.sendJsonMessage(message);
    }

    /**
     * 测试发布消息接口
     * 
     * @param principal principal
     * @param uid  The unique identifier for the admin user
     * @param message   msg
     */
    @MessageMapping("/test.{topic}")
    // @TabooFilter
    public void receiveTestMessage(Principal principal,
            @DestinationVariable(value = "topic") String topic,
            @NonNull String message) {
        // TODO: 发送回执
        log.debug("topic: test.{}, message: {}", topic, message);
        // 测试转发，客户端需要首先订阅此主题，如：test.thread.topic
        simpMessagingTemplate.convertAndSend("/topic/test." + topic, message);
    }

}
