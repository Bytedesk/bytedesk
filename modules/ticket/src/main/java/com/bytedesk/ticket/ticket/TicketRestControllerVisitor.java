/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-18 16:18:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 16:29:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/visitor/api/v1/ticket")
@AllArgsConstructor
public class TicketRestControllerVisitor {

    private final TicketRestService ticketRestService;

    // query by visitor uid
    @GetMapping("/query/visitorUid")
    public ResponseEntity<?> queryByVisitorUid(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByVisitorUid(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    
}
