/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 23:06:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.annotation.BlackIpFilter;
import com.bytedesk.core.annotation.BlackUserFilter;
import com.bytedesk.core.annotation.TabooJsonFilter;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.IpUtils;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.message_unread.MessageUnreadService;
import com.bytedesk.service.utils.KeepAliveHelper;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.visitor.event.VisitorBrowseEvent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * anonymous api, no need to login
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1")
public class VisitorRestControllerAnonymous {

    private final VisitorRestService visitorService;

    private final MessageUnreadService messageUnreadService;

    private final IMessageSendService messageSendService;

    private final IpService ipService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final RobotService robotService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @VisitorAnnotation(title = "visitor", action = "init", description = "init visitor")
    @ApiRateLimiter(value = 10.0, timeout = 1)
    @PostMapping("/init")
    public ResponseEntity<?> init(@RequestBody VisitorRequest visitorRequest, HttpServletRequest httpRequest) {
        //
        String ip = IpUtils.getIp(httpRequest);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }
        // 
        if (!StringUtils.hasText(visitorRequest.getNickname())) {
            visitorRequest.setNickname(ipService.createVisitorNickname(httpRequest));
        }
        // 
        VisitorResponse visitor = visitorService.create(visitorRequest);
        // 
        UserProtobuf user = ServiceConvertUtils.convertToUserProtobuf(visitor);
        //
        return ResponseEntity.ok(JsonResult.success(user));
    }

    @VisitorAnnotation(title = "visitor", action = "requestThread", description = "request thread")
    @PostMapping("/thread")
    public ResponseEntity<?> requestThread(@RequestBody VisitorRequest visitorRequest, HttpServletRequest httpRequest) {
        //
        String ip = IpUtils.getIp(httpRequest);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }
        //
        MessageProtobuf messageProtobuf = visitorService.requestThread(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(messageProtobuf));
    }

    @VisitorAnnotation(title = "visitor", action = "browse", description = "visitor browse")
    @PostMapping("/browse")
    public ResponseEntity<?> browse(VisitorRequest visitorRequest, HttpServletRequest httpRequest) {
        //
        String ip = IpUtils.getIp(httpRequest);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }
        //
        bytedeskEventPublisher.publishEvent(new VisitorBrowseEvent(this, visitorRequest));
        //
        return ResponseEntity.ok(JsonResult.success("browse success"));
    }

    // @VisitorAnnotation(title = "visitor", action = "ping", description = "ping")
    @GetMapping("/ping")
    public ResponseEntity<?> ping(VisitorRequest request) {

        visitorService.updateStatus(request.getUid(), VisitorStatusEnum.ONLINE.name());

        int count = messageUnreadService.getUnreadCount(request.getUid());

        return ResponseEntity.ok(JsonResult.success("pong", count));
    }

    // 访客拉取未读消息
    // TODO: 拉取visitor_message表，非消息主表
    @VisitorAnnotation(title = "visitor", action = "getMessageUnread", description = "get unread messages")
    @GetMapping("/message/unread")
    public ResponseEntity<?> getMessageUnread(VisitorRequest request) {
        
        List<MessageResponse> messages = messageUnreadService.getMessages(request.getUid());

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messages));
    }

    // 访客发送http消息
    @BlackIpFilter(title = "black", action = "sendRestMessage")
    @BlackUserFilter(title = "black", action = "sendRestMessage")
    @TabooJsonFilter(title = "敏感词", action = "sendRestMessage")
    @VisitorAnnotation(title = "visitor", action = "sendRestMessage", description = "sendRestMessage")
    @PostMapping("/message/send")
    public ResponseEntity<?> sendRestMessage(@RequestBody Map<String, String> map) {
        // 
        String json = (String) map.get("json");
        log.debug("json {}", json);
        messageSendService.sendJsonMessage(json);
        
        return ResponseEntity.ok(JsonResult.success(json));
    }

    @TabooJsonFilter(title = "敏感词", action = "sendSseMemberMessage")
    @VisitorAnnotation(title = "visitor", action = "sendSseMemberMessage", description = "sendSseMemberMessage")
    @GetMapping(value = "/member/message/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendSseMemberMessage(@RequestParam(value = "message") String message) {
        
        // 延长超时时间至10分钟
        SseEmitter emitter = new SseEmitter(600_000L);
        
        // 添加心跳机制，每30秒发送一个保活消息
        KeepAliveHelper.addKeepAliveEvent(emitter, 30000);
        
        executorService.execute(() -> {
            try {
                robotService.processSseMemberMessage(message, emitter);
            } catch (Exception e) {
                log.error("Error processing SSE request", e);
                emitter.completeWithError(e);
            }
        });
        
        // 添加超时和完成时的回调
        emitter.onTimeout(() -> {
            log.warn("sendSseMemberMessage SSE connection timed out");
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("sendSseMemberMessage SSE connection completed");
            KeepAliveHelper.removeKeepAliveEvent(emitter);
        });
        
        return emitter;
    }

    @BlackIpFilter(title = "black", action = "sendSseVisitorMessage")
    @BlackUserFilter(title = "black", action = "sendSseVisitorMessage")
    @TabooJsonFilter(title = "敏感词", action = "sendSseVisitorMessage")
    @VisitorAnnotation(title = "visitor", action = "sendSseVisitorMessage", description = "sendSseVisitorMessage")
    @GetMapping(value = "/message/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendSseVisitorMessage(@RequestParam(value = "message") String message) {
        
        // 延长超时时间至10分钟
        SseEmitter emitter = new SseEmitter(600_000L);
        
        // 添加心跳机制，每30秒发送一个保活消息
        KeepAliveHelper.addKeepAliveEvent(emitter, 30000);
        
        executorService.execute(() -> {
            try {
                robotService.processSseVisitorMessage(message, emitter);
            } catch (Exception e) {
                log.error("sendSseVisitorMessage Error processing SSE request", e);
                emitter.completeWithError(e);
            }
        });
        
        // 添加超时和完成时的回调
        emitter.onTimeout(() -> {
            log.warn("sendSseVisitorMessage SSE connection timed out");
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("sendSseVisitorMessage SSE connection completed");
            KeepAliveHelper.removeKeepAliveEvent(emitter);
        });
        
        return emitter;
    }


    // message/sync
    @BlackIpFilter(title = "black", action = "sync")
    @BlackUserFilter(title = "black", action = "sync")
    @TabooJsonFilter(title = "敏感词", action = "sync")
    @ApiRateLimiter(value = 10.0, timeout = 1)
    @VisitorAnnotation(title = "visitor", action = "sync", description = "sync visitor message")
    @PostMapping("/message/sync")
    public ResponseEntity<?> sync(@RequestBody VisitorRequest visitorRequest) {

        

        return ResponseEntity.ok(JsonResult.success("sync success"));
    }


    // 在 Bean 销毁时关闭线程池
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

}
