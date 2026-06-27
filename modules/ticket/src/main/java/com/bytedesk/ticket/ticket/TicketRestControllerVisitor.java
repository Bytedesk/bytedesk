/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-18 16:18:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 16:53:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket.dto.TicketHistoryActivityResponse;
import com.bytedesk.ticket.ticket.dto.TicketWorkflowTaskResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/visitor/api/v1/ticket")
@AllArgsConstructor
public class TicketRestControllerVisitor {

    private final TicketRestService ticketRestService;
    private final TicketService ticketService;

    // query by visitor uid
    @GetMapping("/query")
    public ResponseEntity<?> queryByVisitorUid(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(TicketRequest request) {

        TicketResponse response = ticketRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    // create ticket by visitor
    @PostMapping("/create")
    public ResponseEntity<?> createByVisitor(@RequestBody TicketRequest request) {

        TicketResponse response = ticketRestService.createVisitor(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteByVisitor(@RequestBody TicketRequest request) {

        ticketRestService.deleteByVisitor(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/history/activity")
    public ResponseEntity<?> queryTicketActivityHistory(TicketRequest request) {

        List<TicketHistoryActivityResponse> activities = ticketService.queryTicketActivityHistory(request);

        return ResponseEntity.ok(JsonResult.success(activities));
    }

    /**
     * 访客端查询当前流程实例的活动任务和可执行操作。
     * 仅当访客是工单报告人时才返回可操作的验证动作（COMPLETE_VERIFIED / COMPLETE_REJECTED）。
     */
    @GetMapping("/workflow/actions")
    public ResponseEntity<?> queryWorkflowActions(TicketRequest request) {

        List<TicketWorkflowTaskResponse> actions = ticketService.queryWorkflowActions(request);

        return ResponseEntity.ok(JsonResult.success(actions));
    }

    /**
     * 访客端按当前 Flowable 活动任务执行流程动作。
     * 仅当访客是工单报告人时允许执行验证操作。
     */
    @PostMapping("/workflow/action")
    public ResponseEntity<?> executeWorkflowAction(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.executeWorkflowAction(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    
}
