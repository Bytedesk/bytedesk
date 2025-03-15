/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-20 13:19:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 14:15:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_message;

import org.springframework.data.domain.Page;
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
@AllArgsConstructor
@RequestMapping("/visitor/v1/message")
public class VisitorMessageRestController extends BaseRestController<MessageRequest> {
    
    private final VisitorMessageRestService messageRestService;

    @Override
    public ResponseEntity<?> queryByOrg(MessageRequest request) {
        
        Page<MessageResponse> page = messageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(MessageRequest request) {
        
        Page<MessageResponse> page = messageRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByTopic(MessageRequest request) {

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

    @Override
    public Object export(MessageRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
