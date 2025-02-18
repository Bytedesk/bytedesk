/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 16:41:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.DateUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.comment.TicketCommentRequest;

import jakarta.servlet.http.HttpServletResponse;

import com.bytedesk.ticket.comment.TicketCommentEntity;

@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketRestController extends BaseRestController<TicketRequest> {
    
    private final TicketRestService ticketRestService;

    private final TicketService ticketService;

    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> queryByOrg(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/service-thread-topic")
    public ResponseEntity<?> queryByServiceThreadTopic(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/thread-uid")
    public ResponseEntity<?> queryByThreadUid(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(TicketRequest request) {

        TicketResponse response = ticketRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(TicketRequest request) {

        TicketResponse response = ticketRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(TicketRequest request) {

        ticketRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }
    
    @PostMapping("/{id}/comments")
    public TicketCommentEntity addComment(@PathVariable Long id, @RequestBody TicketCommentRequest comment) {
        return ticketRestService.addComment(id, comment);
    }
    
    @PostMapping("/{id}/attachments")
    public TicketAttachmentEntity uploadAttachment(@PathVariable Long id, @RequestParam MultipartFile file) {
        return ticketRestService.uploadAttachment(id, file);
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "ticket", action = "export", description = "export ticket")
    @GetMapping("/export")
    public Object export(TicketRequest request, HttpServletResponse response) {
        // query data to export
        Page<TicketResponse> ticketPage = ticketRestService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "Ticket-" + DateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<TicketExcel> excelList = ticketPage.getContent().stream().map(ticketResponse -> {
                return modelMapper.map(ticketResponse, TicketExcel.class);
            }).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), TicketExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("Ticket")
                    .doWrite(excelList);

        } catch (Exception e) {
            // reset response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "failure");
            jsonObject.put("message", "download failed " + e.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(jsonObject));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return "";
    }

    /**
     * 查询工单，并过滤掉没有任务的工单
     */
    @GetMapping("/query/filter")
    public ResponseEntity<?> queryTicketFilter(TicketRequest request) {
        Page<TicketResponse> page = ticketService.queryTicketFilter(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 查询我创建的工单
     */
    @GetMapping("/query/created")
    public ResponseEntity<?> queryCreated(TicketRequest request) {
        Page<TicketResponse> page = ticketService.queryCreated(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 查询待我处理的工单
     */
    @GetMapping("/query/claimed")
    public ResponseEntity<?> queryClaimed(TicketRequest request) {
        Page<TicketResponse> page = ticketService.queryClaimed(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 查询待分配的工单
     */
    @GetMapping("/query/unassigned")
    public ResponseEntity<?> queryUnassigned(TicketRequest request) {
        Page<TicketResponse> page = ticketService.queryUnassigned(request);
        return ResponseEntity.ok(JsonResult.success(page));
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
     * 退回工单
     */
    @PostMapping("/unclaim")
    public ResponseEntity<?> unclaimTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.unclaimTicket(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 完成工单
     */
    @PostMapping("/complete")
    public ResponseEntity<?> completeTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.completeTicket(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 查询工单处理历史
     */
    @GetMapping("/history")
    public ResponseEntity<?> queryTicketHistory(TicketRequest request) {
        List<TicketHistoryResponse> histories = ticketService.queryTicketHistory(request);
        return ResponseEntity.ok(JsonResult.success(histories));
    }
} 