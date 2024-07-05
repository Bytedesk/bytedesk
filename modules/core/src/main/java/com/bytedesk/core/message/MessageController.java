/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-05 12:36:07
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

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.message_unread.MessageUnreadService;
import com.bytedesk.core.socket.service.MqService;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageController extends BaseController<MessageRequest> {

    private final MessageService messageService;

    private final MqService stompMqService;

    private final MessageUnreadService messageUnreadService;

    /**
     * 管理后台 根据 orgUids 查询
     * 
     * @param request
     * @return
     */
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MessageRequest request) {

        Page<MessageResponse> messagePage = messageService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(messagePage));
    }

    /**
     * @return json
     */
    @GetMapping("/query/topic")
    public ResponseEntity<?> query(MessageRequest request) {

        Page<MessageResponse> response = messageService.queryByThreadTopic(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // @GetMapping("/query/unread")
    // public ResponseEntity<?> queryUnread(MessageRequest request) {
    // String threadsKey = String.join("-", request.getThreads()); //
    // 使用"-"或其他分隔符连接数组元素
    // Page<MessageResponse> response = messageService.queryByThread(request,
    // threadsKey);
    // // Page<MessageResponse> response = messageService.queryUnread(request);
    // //
    // return ResponseEntity.ok(JsonResult.success(response));
    // }

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
        stompMqService.sendJsonMessageToMq(json);
        //
        return ResponseEntity.ok(JsonResult.success(json));
    }
}
