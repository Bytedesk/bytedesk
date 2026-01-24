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

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.annotation.Idempotent;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketRestController extends BaseRestController<TicketRequest, TicketRestService> {
    
    private final TicketRestService ticketRestService;

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "组织查询", description = "query ticket by org")
    @Override
    public ResponseEntity<?> queryByOrg(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "用户查询", description = "query ticket by user")
    @Override
    public ResponseEntity<?> queryByUser(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "查询详情", description = "query ticket by uid")
    @Override
    public ResponseEntity<?> queryByUid(TicketRequest request) {
        
        TicketResponse response = ticketRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "按主题查询", description = "query ticket by topic")
    @GetMapping("/query/thread/topic")
    public ResponseEntity<?> queryByThreadTopic(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByThreadTopic(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 兼容旧接口：历史客户端使用 /query/topic。
     * 实际语义为按 threadTopic 查询。
     */
    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "按主题查询(兼容)", description = "query ticket by topic (compat)")
    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByTopicCompat(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByThreadTopic(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "按会话查询", description = "query ticket by thread uid")
    @GetMapping("/query/thread/uid")
    public ResponseEntity<?> queryByThreadUid(TicketRequest request) {

        TicketResponse ticket = ticketRestService.queryByThreadUid(request);

        return ResponseEntity.ok(JsonResult.success(ticket));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "按访客会话查询", description = "query ticket by visitor thread uid")
    @GetMapping("/query/visitor/thread/uid")
    public ResponseEntity<?> queryByVisitorThreadUid(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByVisitorThreadUid(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_CREATE)
    @ActionAnnotation(title = "工单", action = "新建", description = "create ticket")
    @Override
    @Idempotent(ttlSeconds = 120)
    public ResponseEntity<?> create(TicketRequest request) {

        TicketResponse response = ticketRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_UPDATE)
    @ActionAnnotation(title = "工单", action = "更新", description = "update ticket")
    @Override
    public ResponseEntity<?> update(TicketRequest request) {

        TicketResponse response = ticketRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_DELETE)
    @ActionAnnotation(title = "工单", action = "删除", description = "delete ticket")
    @Override
    public ResponseEntity<?> delete(TicketRequest request) {

        ticketRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @PreAuthorize(TicketPermissions.HAS_TICKET_EXPORT)
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

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = "工单", action = "状态统计", description = "count ticket by status")
    @GetMapping("/count/status")
    public ResponseEntity<?> countStatus(TicketRequest request) {

        TicketStatusCountResponse counts = ticketRestService.countStatus(request);

        return ResponseEntity.ok(JsonResult.success(counts));
    }


}