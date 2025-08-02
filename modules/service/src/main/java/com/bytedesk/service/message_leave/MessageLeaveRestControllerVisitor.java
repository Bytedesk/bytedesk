/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-27 10:55:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 21:41:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/visitor/api/v1/message/leave")
@AllArgsConstructor
@Tag(name = "留言消息匿名管理", description = "留言消息匿名相关接口")
public class MessageLeaveRestControllerVisitor {

    private final MessageLeaveRestService messageLeaveRestService;

    @PostMapping("/create")
    @Operation(summary = "创建留言消息")
    public ResponseEntity<?> create(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/query")
    @Operation(summary = "查询留言消息")
    public ResponseEntity<?> query(@RequestBody MessageLeaveRequest request) {
       
        Page<MessageLeaveResponse> response = messageLeaveRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/confirm")
    @Operation(summary = "确认留言消息")
    public ResponseEntity<?> confirm(@RequestBody MessageLeaveRequest request) {
       
        // MessageLeaveResponse response = messageLeaveService.confirm(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝留言消息")
    public ResponseEntity<?> reject(@RequestBody MessageLeaveRequest request) {
       
        // MessageLeaveResponse response = messageLeaveService.reject(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}
