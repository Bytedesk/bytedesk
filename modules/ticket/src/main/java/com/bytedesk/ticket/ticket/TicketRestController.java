/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-20 17:04:15
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
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.attachment.TicketAttachment;
import com.bytedesk.ticket.comment.CommentRequest;
import com.bytedesk.ticket.comment.TicketComment;

@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketRestController extends BaseRestController<TicketRequest> {
    
    private final TicketService ticketService;

    @Override
    public ResponseEntity<?> queryByOrg(TicketRequest request) {

        Page<TicketResponse> page = ticketService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TicketRequest request) {
        Page<TicketResponse> page = ticketService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }


    @Override
    public ResponseEntity<?> create(TicketRequest request) {
        TicketEntity entity = ticketService.createTicket(request);
        return ResponseEntity.ok(JsonResult.success(entity));
    }


    @Override
    public ResponseEntity<?> update(TicketRequest request) {
        TicketResponse response = ticketService.update(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(TicketRequest request) {
        ticketService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }


    // @PostMapping
    // public TicketEntity createTicket(@RequestBody TicketRequest ticket) {
    //     return ticketService.createTicket(ticket);
    // }
    
    // @PutMapping("/{id}")
    // public TicketEntity updateTicket(@PathVariable Long id, @RequestBody TicketRequest ticket) {
    //     return ticketService.updateTicket(id, ticket);
    // }
    
    @PostMapping("/{id}/comments")
    public TicketComment addComment(@PathVariable Long id, @RequestBody CommentRequest comment) {
        return ticketService.addComment(id, comment);
    }
    
    @PostMapping("/{id}/attachments")
    public TicketAttachment uploadAttachment(@PathVariable Long id, @RequestParam MultipartFile file) {
        return ticketService.uploadAttachment(id, file);
    }
    
    @GetMapping("/statistics")
    public TicketStatistics getStatistics() {
        return ticketService.getStatistics();
    }

    
} 