/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 15:18:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bytedesk.core.apilimit.ApiRateLimiter;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.IpUtils;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message_unread.MessageUnreadService;
import com.bytedesk.core.utils.JsonResult;
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

    // @VisitorAnnotation(title = "visitor", action = "pre", description = "pre visit page")
    // @GetMapping("/pre")
    // public ResponseEntity<?> pre(HttpServletRequest request) {
    //     return ResponseEntity.ok(JsonResult.success("pre"));
    // }

    @VisitorAnnotation(title = "visitor", action = "init", description = "init visitor")
    @ApiRateLimiter(value = 10.0, timeout = 1)
    @PostMapping("/init")
    public ResponseEntity<?> init(@RequestBody VisitorRequest visitorRequest, HttpServletRequest request) {
        //
        String ip = IpUtils.getIp(request);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }
        if (!StringUtils.hasText(visitorRequest.getNickname())) {
            visitorRequest.setNickname(ipService.createVisitorNickname(request));
        }
        VisitorResponse visitor = visitorService.create(visitorRequest);
 
        return ResponseEntity.ok(JsonResult.success(visitor));
    }

    @VisitorAnnotation(title = "visitor", action = "requestThread", description = "request thread")
    @PostMapping("/thread")
    public ResponseEntity<?> requestThread(@RequestBody VisitorRequest visitorRequest) {
        //
        MessageProtobuf messageProtobuf = visitorService.requestThread(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(messageProtobuf));
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
    public ResponseEntity<?> sendRestMessage(@RequestBody Map<String, String> map) {
        //
        String json = (String) map.get("json");
        log.debug("json {}", json);
        // stompMqService.sendJsonMessageToMq(json);
        messageSendService.sendJsonMessage(json);
        //
        return ResponseEntity.ok(JsonResult.success(json));
    }


}
