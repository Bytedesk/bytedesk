/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-27 14:50:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.message_unread.MessageUnreadService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jackning (270580156@qq.com)
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageRestController extends BaseRestController<MessageRequest> {

    private final MessageRestService messageRestService;

    private final IMessageSendService messageSendService;

    private final MessageUnreadService messageUnreadService;

    @Override
    public ResponseEntity<?> queryByOrg(MessageRequest request) {

        Page<MessageResponse> messagePage = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(messagePage));
    }

    public ResponseEntity<?> queryByUser(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByUser(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByTopic(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> queryByUid(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @GetMapping("/query/thread/uid")
    public ResponseEntity<?> queryByThreadUid(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> create(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * 客户端定期ping，
     * TODO:
     * 1. 返回未读消息数，如果大于0，则客户端拉取未读消息
     * 2. 返回客户端连接状态（在服务器端的），如果断开，则客户端重新连接
     * 
     * @return
     */
    @GetMapping("/ping")
    public ResponseEntity<?> ping(MessageRequest request) {

        int count = messageUnreadService.getUnreadCount(request.getUid());

        return ResponseEntity.ok(JsonResult.success("pong", count));
    }

    /**
     * 当客户端长连接断开时，启用此rest接口发送消息
     * send offline message
     *
     * @param map map
     * @return json
     */
    @PostMapping("/rest/send")
    public ResponseEntity<?> sendRestMessage(@RequestBody Map<String, String> map) {

        String json = (String) map.get("json");
        log.debug("json {}", json);
        // stompMqService.sendJsonMessageToMq(json);
        messageSendService.sendJsonMessage(json);
        //
        return ResponseEntity.ok(JsonResult.success(json));
    }


    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "消息", action = "导出", description = "export message")
    @GetMapping("/export")
    public Object export(MessageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            messageRestService,
            MessageExcel.class,
            "Message",
            "Message"
        );
    }

    

    
}
