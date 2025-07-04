/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 13:51:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/message/leave")
@AllArgsConstructor
public class MessageLeaveRestController extends BaseRestController<MessageLeaveRequest> {

    private final MessageLeaveRestService messageLeaveRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(MessageLeaveRequest request) {

        Page<MessageLeaveResponse> page = messageLeaveRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(MessageLeaveRequest request) {
        
        Page<MessageLeaveResponse> page = messageLeaveRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(MessageLeaveRequest request) {
        
        MessageLeaveResponse response = messageLeaveRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> create(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    // reply
    @PostMapping("/reply")
    public ResponseEntity<?> reply(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveRestService.reply(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(MessageLeaveRequest request) {
        
        messageLeaveRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(MessageLeaveRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            messageLeaveRestService,
            MessageLeaveExcel.class,
            "留言消息",
            "message-leave"
        );
    }

}
