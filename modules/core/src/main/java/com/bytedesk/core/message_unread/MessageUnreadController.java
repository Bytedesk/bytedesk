/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 12:38:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-05 13:04:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/message_unread")
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

}
