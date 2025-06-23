/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:38:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-23 10:27:21
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
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/message/unread")
@AllArgsConstructor
public class MessageUnreadRestController extends BaseRestController<MessageUnreadRequest> {

    private final MessageUnreadRestService messageUnreadService;

    @Override
    public ResponseEntity<?> queryByOrg(MessageUnreadRequest request) {
        
        Page<MessageUnreadResponse> page = messageUnreadService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success("get unread messages success", page));
    }

    @Override
    public ResponseEntity<?> queryByUser(MessageUnreadRequest request) {

        List<MessageResponse> messageList = messageUnreadService.getMessages(request.getUserUid());

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messageList));
    }

    @Override
    public ResponseEntity<?> queryByUid(MessageUnreadRequest request) {
        
        Optional<MessageUnreadEntity> messageUnread = messageUnreadService.findByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success("get unread message success", messageUnread));
    }

    @Override
    public ResponseEntity<?> create(MessageUnreadRequest request) {
        
        MessageUnreadResponse messageUnread = messageUnreadService.create(request);

        return ResponseEntity.ok(JsonResult.success("create unread message success", messageUnread));
    }

    @Override
    public ResponseEntity<?> update(MessageUnreadRequest request) {
        
        MessageUnreadResponse messageUnread = messageUnreadService.update(request);

        return ResponseEntity.ok(JsonResult.success("update unread message success", messageUnread));
    }

    @Override
    public ResponseEntity<?> delete(MessageUnreadRequest request) {
        
        messageUnreadService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete unread message success"));
    }

    @Override
    public Object export(MessageUnreadRequest request, HttpServletResponse response) {
        //
        // messageUnreadService.export(request, response);

        return JsonResult.success("export unread messages success");
    }
    

}
