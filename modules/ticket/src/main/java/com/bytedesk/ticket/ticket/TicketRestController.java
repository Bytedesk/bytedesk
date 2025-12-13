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

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket.dto.TicketHistoryActivityResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryProcessResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryTaskResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketRestController extends BaseRestController<TicketRequest, TicketRestService> {
    
    private final TicketRestService ticketRestService;

    private final TicketService ticketService;

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "组织查询", description = "query ticket by org")
    @Override
    public ResponseEntity<?> queryByOrg(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "用户查询", description = "query ticket by user")
    @Override
    public ResponseEntity<?> queryByUser(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "查询详情", description = "query ticket by uid")
    @Override
    public ResponseEntity<?> queryByUid(TicketRequest request) {
        
        TicketResponse response = ticketRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "按主题查询", description = "query ticket by topic")
    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByTopic(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "按会话查询", description = "query ticket by thread uid")
    @GetMapping("/query/thread/uid")
    public ResponseEntity<?> queryByThreadUid(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_CREATE_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "新建", description = "create ticket")
    @Override
    public ResponseEntity<?> create(TicketRequest request) {

        TicketResponse response = ticketRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "更新", description = "update ticket")
    @Override
    public ResponseEntity<?> update(TicketRequest request) {

        TicketResponse response = ticketRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_DELETE_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "删除", description = "delete ticket")
    @Override
    public ResponseEntity<?> delete(TicketRequest request) {

        ticketRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @PreAuthorize(TicketPermissions.HAS_TICKET_EXPORT_ANY_LEVEL)
    @ActionAnnotation(title = "工单", action = "导出", description = "export ticket")
    @GetMapping("/export")
    public Object export(TicketRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ticketRestService,
            TicketExcel.class,
            "Ticket",
            "Ticket"
        );
    }

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
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
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