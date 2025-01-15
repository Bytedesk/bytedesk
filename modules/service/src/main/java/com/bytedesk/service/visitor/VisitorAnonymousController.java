/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 14:09:06
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
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bytedesk.core.apilimit.ApiRateLimiter;
import com.bytedesk.core.black.access.VisitorAccessService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.IpUtils;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message_unread.MessageUnreadService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.event.VisitorBrowseEvent;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

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
public class VisitorAnonymousController {

    private final VisitorRestService visitorService;

    private final MessageUnreadService messageUnreadService;

    private final IMessageSendService messageSendService;

    private final IpService ipService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Autowired
    private VisitorAccessService visitorAccessService;

    // @VisitorAnnotation(title = "visitor", action = "pre", description = "pre visit page")
    // @GetMapping("/pre")
    // public ResponseEntity<?> pre(HttpServletRequest request) {
    //     return ResponseEntity.ok(JsonResult.success("pre"));
    // }

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
        if (!StringUtils.hasText(visitorRequest.getNickname())) {
            visitorRequest.setNickname(ipService.createVisitorNickname(httpRequest));
        }
        VisitorResponse visitor = visitorService.create(visitorRequest);
        UserProtobuf user = ConvertServiceUtils.convertToUserProtobuf(visitor);
        //
        return ResponseEntity.ok(JsonResult.success(user));
    }

    @VisitorAnnotation(title = "visitor", action = "requestThread", description = "request thread")
    @PostMapping("/thread")
    public ResponseEntity<?> requestThread(@RequestBody VisitorRequest visitorRequest) {
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

    @VisitorAnnotation(title = "visitor", action = "ping", description = "ping")
    @GetMapping("/ping")
    public ResponseEntity<?> ping(VisitorRequest request) {

        visitorService.updateStatus(request.getUid(), VisitorStatusEnum.ONLINE.name());

        int count = messageUnreadService.getUnreadCount(request.getUid());

        return ResponseEntity.ok(JsonResult.success("pong", count));
    }

    // 访客拉取未读消息
    @VisitorAnnotation(title = "visitor", action = "getMessageUnread", description = "get unread messages")
    @GetMapping("/message/unread")
    public ResponseEntity<?> getMessageUnread(VisitorRequest request) {
        // TODO: 拉取visitor_message表，非消息主表
        List<MessageResponse> messages = messageUnreadService.getMessages(request.getUid());

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messages));
    }

    @VisitorAnnotation(title = "visitor", action = "sendRestMessage", description = "sendRestMessage")
    @PostMapping("/message/send")
    public ResponseEntity<?> sendRestMessage(@RequestHeader(value = "X-Visitor-ID", required = true) String visitorId,
                                           @RequestBody Map<String, String> map) {
        // 检查访问权限
        if (!visitorAccessService.isAllowed(visitorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(JsonResult.error("Access denied for visitor: " + visitorId));
        }
        
        String json = (String) map.get("json");
        log.debug("json {}", json);
        messageSendService.sendJsonMessage(json);
        
        return ResponseEntity.ok(JsonResult.success(json));
    }


}
