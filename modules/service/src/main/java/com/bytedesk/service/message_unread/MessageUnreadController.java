/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:38:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-13 22:33:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.message.MessageRequest;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/message/unread")
@AllArgsConstructor
public class MessageUnreadController extends BaseRestController<MessageUnreadRequest> {

    private final MessageUnreadService messageUnreadService;

    @Override
    public ResponseEntity<?> queryByOrg(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    // 登录用户拉取未读消息
    @GetMapping("/query")
    @Override
    public ResponseEntity<?> queryByUser(MessageUnreadRequest request) {

        List<MessageResponse> messageList = messageUnreadService.getMessages(request.getUserUid());

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messageList));
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


    @Override
    public ResponseEntity<?> create(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Object export(MessageUnreadRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(MessageUnreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

}
