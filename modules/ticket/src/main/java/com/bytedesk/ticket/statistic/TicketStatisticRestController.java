/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 12:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 13:40:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket/statistic")
@RequiredArgsConstructor
public class TicketStatisticRestController extends BaseRestController<TicketStatisticRequest> {

    private final TicketStatisticRestService ticketStatisticRestService;

    private final TicketStatisticService ticketStatisticService;

    @Override
    public ResponseEntity<?> queryByOrg(TicketStatisticRequest request) {

        Page<TicketStatisticResponse> page = ticketStatisticRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TicketStatisticRequest request) {

        Page<TicketStatisticResponse> page = ticketStatisticRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(TicketStatisticRequest request) {
        
        TicketStatisticResponse response = ticketStatisticRestService.create(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("创建失败"));
        }

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(TicketStatisticRequest request) {
        
        TicketStatisticResponse response = ticketStatisticRestService.update(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("更新失败"));
        }

        return ResponseEntity.ok(JsonResult.success(response));
    }


    @Override
    public ResponseEntity<?> delete(TicketStatisticRequest request) {
        
        ticketStatisticRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // 计算所有工单统计
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateAllStatistics() {

        ticketStatisticService.calculateAllStatistics();

        return ResponseEntity.ok(JsonResult.success());
    }

    
}
