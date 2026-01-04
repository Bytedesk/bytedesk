/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 16:38:23
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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket.dto.TicketHistoryActivityResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryProcessResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryTaskResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;

    /**
     * 认领工单
     */
    @PostMapping("/claim")
    public ResponseEntity<?> claimTicket(@RequestBody TicketRequest request) {
        
        TicketResponse response = ticketService.claimTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 开始处理工单
     */
    @PostMapping("/start")
    public ResponseEntity<?> startTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.startTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 退回工单
     */
    @PostMapping("/unclaim")
    public ResponseEntity<?> unclaimTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.unclaimTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 转派工单
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transferTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.transferTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 挂起工单
     */
    @PostMapping("/hold")
    public ResponseEntity<?> holdTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.holdTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 恢复工单
     */
    @PostMapping("/resume")
    public ResponseEntity<?> resumeTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.resumeTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 待回应工单
     */
    @PostMapping("/pend")
    public ResponseEntity<?> pendTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.pendTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 重新打开工单
     */
    @PostMapping("/reopen")
    public ResponseEntity<?> reopenTicket(@RequestBody TicketRequest request) {
        
        TicketResponse response = ticketService.reopenTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 升级工单
     */
    @PostMapping("/escalate")
    public ResponseEntity<?> escalateTicket(@RequestBody TicketRequest request) {
        
        TicketResponse response = ticketService.escalateTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 完成工单
     */
    @PostMapping("/resolve")
    public ResponseEntity<?> resolveTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.resolveTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 客户验证工单
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.verifyTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 关闭工单
     */
    @PostMapping("/close")
    public ResponseEntity<?> closeTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.closeTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 取消工单
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.cancelTicket(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 查询工单任务历史
     */
    @GetMapping("/history/task")
    public ResponseEntity<?> queryTicketTaskHistory(TicketRequest request) {

        List<TicketHistoryTaskResponse> histories = ticketService.queryTicketTaskHistory(request);

        return ResponseEntity.ok(JsonResult.success(histories));
    }

    /**
     * 查询工单流程实例历史
     */
    @GetMapping("/history/process")
    public ResponseEntity<?> queryTicketProcessHistory(TicketRequest request) {

        List<TicketHistoryProcessResponse> histories = ticketService.queryTicketProcessHistory(request);

        return ResponseEntity.ok(JsonResult.success(histories));
    }

    /**
     * 查询工单活动历史
     */
    @GetMapping("/history/activity")
    public ResponseEntity<?> queryTicketActivityHistory(TicketRequest request) {

        List<TicketHistoryActivityResponse> activities = ticketService.queryTicketActivityHistory(request);

        return ResponseEntity.ok(JsonResult.success(activities));
    }

    

}