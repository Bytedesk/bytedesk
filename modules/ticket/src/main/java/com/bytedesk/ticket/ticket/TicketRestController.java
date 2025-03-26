/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 13:44:29
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
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.comment.TicketCommentRequest;
import com.bytedesk.ticket.ticket.dto.TicketHistoryActivityResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryProcessResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryTaskResponse;

import jakarta.servlet.http.HttpServletResponse;

import com.bytedesk.ticket.comment.TicketCommentEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketRestController extends BaseRestController<TicketRequest> {
    
    private final TicketRestService ticketRestService;

    private final TicketService ticketService;

    private final ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('TICKET_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize("hasAuthority('TICKET_READ')")
    @Override
    public ResponseEntity<?> queryByUser(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize("hasAuthority('TICKET_READ')")
    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByTopic(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize("hasAuthority('TICKET_READ')")
    @GetMapping("/query/thread/uid")
    public ResponseEntity<?> queryByThreadUid(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize("hasAuthority('TICKET_READ')")
    @Override
    public ResponseEntity<?> queryByUid(TicketRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @PreAuthorize("hasAuthority('TICKET_CREATE')")
    @Override
    public ResponseEntity<?> create(TicketRequest request) {

        TicketResponse response = ticketRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize("hasAuthority('TICKET_UPDATE')")
    @Override
    public ResponseEntity<?> update(TicketRequest request) {

        TicketResponse response = ticketRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize("hasAuthority('TICKET_DELETE')")
    @Override
    public ResponseEntity<?> delete(TicketRequest request) {

        ticketRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }
    
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/{id}/comments")
    public TicketCommentEntity addComment(@PathVariable Long id, @RequestBody TicketCommentRequest comment) {
        return ticketRestService.addComment(id, comment);
    }
    
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/{id}/attachments")
    public TicketAttachmentEntity uploadAttachment(@PathVariable Long id, @RequestParam MultipartFile file) {
        return ticketRestService.uploadAttachment(id, file);
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @PreAuthorize("hasAuthority('TICKET_EXPORT')")
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
            String fileName = "Ticket-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
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
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @GetMapping("/query/filter")
    public ResponseEntity<?> queryTicketFilter(TicketRequest request) {

        Page<TicketResponse> page = ticketService.queryTicketFilter(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 查询我创建的工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @GetMapping("/query/created")
    public ResponseEntity<?> queryCreated(TicketRequest request) {

        Page<TicketResponse> page = ticketService.queryCreated(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 查询待我处理的工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @GetMapping("/query/claimed")
    public ResponseEntity<?> queryClaimed(TicketRequest request) {

        Page<TicketResponse> page = ticketService.queryClaimed(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 查询待分配的工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    @GetMapping("/query/unassigned")
    public ResponseEntity<?> queryUnassigned(TicketRequest request) {

        Page<TicketResponse> page = ticketService.queryUnassigned(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 认领工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/claim")
    public ResponseEntity<?> claimTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.claimTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 开始处理工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/start")
    public ResponseEntity<?> startTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.startTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 退回工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/unclaim")
    public ResponseEntity<?> unclaimTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.unclaimTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 转派工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    @PostMapping("/transfer")
    public ResponseEntity<?> transferTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.transferTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 挂起工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/hold")
    public ResponseEntity<?> holdTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.holdTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 恢复工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/resume")
    public ResponseEntity<?> resumeTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.resumeTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 待回应工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/pend")
    public ResponseEntity<?> pendTicket(@RequestBody TicketRequest request) {

        TicketResponse response = ticketService.pendTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 重新打开工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/reopen")
    public ResponseEntity<?> reopenTicket(@RequestBody TicketRequest request) {
        
        TicketResponse response = ticketService.reopenTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 升级工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    @PostMapping("/escalate")
    public ResponseEntity<?> escalateTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.escalateTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 完成工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/resolve")
    public ResponseEntity<?> resolveTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.resolveTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 客户验证工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.verifyTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 关闭工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/close")
    public ResponseEntity<?> closeTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.closeTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 取消工单
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.cancelTicket(request);
        if (response == null) {
            return ResponseEntity.ok(JsonResult.error("工单不存在"));
        }
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 查询工单任务历史
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @GetMapping("/history/task")
    public ResponseEntity<?> queryTicketTaskHistory(TicketRequest request) {
        List<TicketHistoryTaskResponse> histories = ticketService.queryTicketTaskHistory(request);
        return ResponseEntity.ok(JsonResult.success(histories));
    }

    /**
     * 查询工单流程实例历史
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @GetMapping("/history/process")
    public ResponseEntity<?> queryTicketProcessHistory(TicketRequest request) {
        List<TicketHistoryProcessResponse> histories = ticketService.queryTicketProcessHistory(request);
        return ResponseEntity.ok(JsonResult.success(histories));
    }

    /**
     * 查询工单活动历史
     */
    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @GetMapping("/history/activity")
    public ResponseEntity<?> queryTicketActivityHistory(TicketRequest request) {
        List<TicketHistoryActivityResponse> activities = ticketService.queryTicketActivityHistory(request);
        return ResponseEntity.ok(JsonResult.success(activities));
    }

    

} 