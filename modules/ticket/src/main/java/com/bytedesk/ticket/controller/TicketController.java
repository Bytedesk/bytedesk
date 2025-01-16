/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-16 18:53:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.ticket.model.TicketEntity;
import com.bytedesk.ticket.model.TicketAttachment;
import com.bytedesk.ticket.model.TicketComment;
import com.bytedesk.ticket.model.TicketStatistics;
import com.bytedesk.ticket.service.TicketService;

import lombok.AllArgsConstructor;

import com.bytedesk.ticket.dto.TicketRequest;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.ticket.dto.CommentRequest;

@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class TicketController extends BaseRestController<TicketRequest> {
    
    private final TicketService ticketService;

    @PostMapping
    public TicketEntity createTicket(@RequestBody TicketRequest ticket) {
        return ticketService.createTicket(ticket);
    }
    
    @PutMapping("/{id}")
    public TicketEntity updateTicket(@PathVariable Long id, @RequestBody TicketRequest ticket) {
        return ticketService.updateTicket(id, ticket);
    }
    
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

    @Override
    public ResponseEntity<?> create(TicketRequest arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> delete(TicketRequest arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public ResponseEntity<?> queryByOrg(TicketRequest arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public ResponseEntity<?> queryByUser(TicketRequest arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public ResponseEntity<?> update(TicketRequest arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
} 