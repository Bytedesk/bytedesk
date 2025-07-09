/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 16:24:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.data.domain.Page;
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
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.IpUtils;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRequest;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.message_unread.MessageUnreadRequest;
import com.bytedesk.service.message_unread.MessageUnreadResponse;
import com.bytedesk.service.message_unread.MessageUnreadRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * anonymous api, no need to login
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/visitor")
@Tag(name = "访客匿名管理", description = "访客匿名相关接口")
public class VisitorRestControllerVisitor {

    private final VisitorRestService visitorRestService;

    private final MessageUnreadRestService messageUnreadService;

    private final IMessageSendService messageSendService;

    private final MessageRestService messageRestService;

    private final IpService ipService;

    private final RobotService robotService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @ApiRateLimiter(value = 1, timeout = 1)
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
        VisitorResponse visitor = visitorRestService.create(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitor));
    }

    @PostMapping("/thread")
    public ResponseEntity<?> requestThread(@RequestBody VisitorRequest visitorRequest, HttpServletRequest httpRequest) {
        //
        String ip = IpUtils.getIp(httpRequest);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }
        //
        MessageProtobuf messageProtobuf = visitorRestService.requestThread(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(messageProtobuf));
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping(MessageUnreadRequest request) {

        visitorRestService.updateStatus(request.getUid(), VisitorStatusEnum.ONLINE.name());

        long count = messageUnreadService.getUnreadCount(request);

        return ResponseEntity.ok(JsonResult.success("pong", count));
    }

    /**
     * 根据主题查询消息
     * 
     * @param request 查询请求
     * @return 分页消息列表
     */
    // @Operation(summary = "根据主题查询消息", description = "根据主题查询相关消息")
    @GetMapping("/message/thread/topic")
    public ResponseEntity<?> queryByThreadTopic(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 根据会话UID查询消息
     * 
     * @param request 查询请求
     * @return 分页消息列表
     */
    // @Operation(summary = "根据会话UID查询消息", description = "通过会话唯一标识符查询相关消息")
    @GetMapping("/message/thread/uid")
    public ResponseEntity<?> queryByThreadUid(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // 访客拉取未读消息
    // @VisitorAnnotation(title = "visitor", action = "getMessageUnread", description = "get unread messages")
    @GetMapping("/message/unread")
    public ResponseEntity<?> getMessageUnread(MessageUnreadRequest request) {
        
        Page<MessageUnreadResponse> messages = messageUnreadService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messages));
    }

    // 获取未读消息数
    @GetMapping("/message/unread/count")
    public ResponseEntity<?> getMessageUnreadCount(MessageUnreadRequest request) {

        long count = messageUnreadService.getUnreadCount(request);

        return ResponseEntity.ok(JsonResult.success("get unread messages count success", count));
    }

    // 清空当前用户所有未读消息
    @PostMapping("/message/unread/clear")
    public ResponseEntity<?> clearMessageUnread(@RequestBody MessageUnreadRequest request) {
        // 
        messageUnreadService.clearUnreadMessages(request);
        // 看下是否清空了
        long count = messageUnreadService.getUnreadCount(request);

        return ResponseEntity.ok(JsonResult.success("clear unread messages count success", count));
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
        // KeepAliveHelper.addKeepAliveEvent(emitter, 30000);
        
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
            // KeepAliveHelper.removeKeepAliveEvent(emitter);
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
        // KeepAliveHelper.addKeepAliveEvent(emitter, 30000);
        
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
            // KeepAliveHelper.removeKeepAliveEvent(emitter);
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
