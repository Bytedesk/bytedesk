package com.bytedesk.socket.stomp.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
// import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.bytedesk.socket.stomp.service.StompMqService;

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;

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
     * 一对一聊天
     *
     * @param principal principal
     * @param message   msg
     */
    // @MessageMapping("/message")
    // public void message(Principal principal, Message message) {
    // log.debug("one message from: {} content: {}", principal.getName(),
    // message.toString());
    // // 消息原路返回
    // simpMessagingTemplate.convertAndSendToUser(principal.getName(),
    // "/queue/message", message);
    // }

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

    /**
     * 发送普通客服消息
     * 
     * @param principal principal
     * @param wid       wid
     * @param uid       uid
     * @param message   msg
     */
    @MessageMapping("/{wid}.{uid}")
    public void message(Principal principal,
            @DestinationVariable(value = "wid") String wid,
            @DestinationVariable(value = "uid") String uid,
            String message) {
        // TODO: 发送回执
        log.debug("message: {}, {}, {}", wid, uid, message);
        // 转发给mq
        stompMqService.sendMessageToMq(message);
    }

    /**
     * 订阅会话id，返回会话中的用户
     * 注意: 不能监听订阅 /topic/
     *
     * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-subscribe-mapping
     *
     * @param principal principal
     * @param threadId  tid
     * @return list
     */
    @SubscribeMapping("/thread.{threadId}")
    public List<String> subscribeThread(Principal principal,
            @DestinationVariable(value = "threadId") String threadId) {
        log.debug("subscribe from: " + principal.getName() + " threadId:" + threadId);
        // TODO: 从数据库中查询并返回
        return new ArrayList<>();
    }

    /**
     * 点对点推送
     *
     * @param text      text
     * @param sessionId sessionId
     * @return string
     * @throws Exception
     */
    @MessageMapping(value = "/speak")
    @SendToUser(value = "/personal")
    public String speak(@Payload String text,
            @Header("simpSessionId") String sessionId) throws Exception {
        log.debug("收到私人消息:" + text);
        return text;
    }

    /**
     * 异常信息推送
     *
     * broadcast=false 说明：
     * If the user has more than one session, by default all of the sessions
     * subscribed to the given destination are targeted.
     * However sometimes, it may be necessary to target only the session that sent
     * the message being handled.
     * This can be done by setting the broadcast
     *
     * @param exception
     * @return
     */
    @MessageExceptionHandler
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public String handleException(Throwable exception) {
        exception.printStackTrace();
        return exception.getMessage();
    }

}
