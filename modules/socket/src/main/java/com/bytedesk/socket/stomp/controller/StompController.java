/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-16 16:51:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.stomp.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
// import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.bytedesk.socket.stomp.service.StompMqService;

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

    private final StompMqService stompMqService;

    private final SimpMessagingTemplate simpMessagingTemplate;

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
    public void message(Principal principal,
            @DestinationVariable(value = "sid") String sid,
            @DestinationVariable(value = "uid") String uid,
            String message) {
        log.debug("principal: {}, sid: {}, uid: {}, message: {}", principal, sid, uid, message);
        // MessageResponse messageResponse = JSON.parseObject(message, MessageResponse.class);
        // // 发送回执
        // JSONObject ackObject = new JSONObject();
        // ackObject.put("type", MessageTypeConsts.NOTIFICATION_ACK_SUCCESS);
        // ackObject.put("mid", messageResponse.getMid());
        // simpMessagingTemplate.convertAndSend(MqConsts.TOPIC_PREFIX + sid + '.' + uid, ackObject);
        // 转发给mq
        stompMqService.sendMessageToMq(message);
    }

    /**
     * 测试发布消息接口
     * 
     * @param principal principal
     * @param uid       adminUid
     * @param message   msg
     */
    @MessageMapping("/test.{topic}")
    public void receiveTestMessage(Principal principal,
            @DestinationVariable(value = "topic") String topic,
            @NonNull String message) {
        // TODO: 发送回执
        log.debug("topic: test.{}, message: {}", topic, message);
        // 测试转发，客户端需要首先订阅此主题，如：test.thread.topic
        simpMessagingTemplate.convertAndSend("/topic/test." + topic, message);
    }

}
