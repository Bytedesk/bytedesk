/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-27 10:55:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 12:44:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/message/leave")
@AllArgsConstructor
public class MessageLeaveRestControllerVisitor {

    private final MessageLeaveRestService messageLeaveService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));

    }
    
}
