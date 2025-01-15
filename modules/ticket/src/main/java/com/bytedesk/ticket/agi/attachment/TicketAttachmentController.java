/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:44:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:10:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.agi.attachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/tickets/attachments")
public class TicketAttachmentController {

    @Autowired
    private TicketAttachmentService attachmentService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketAttachmentEntity> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ticketId") Long ticketId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "type", defaultValue = "ticket") String type,
            @RequestParam(value = "referenceId", required = false) Long referenceId) throws IOException {
        // return ResponseEntity.ok(attachmentService.uploadAttachment(file, ticketId, 
        //     Long.valueOf(userDetails.getUsername()), type, referenceId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long attachmentId) throws IOException {
        // TicketAttachmentEntity attachment = attachmentService.getAttachment(attachmentId);
        // byte[] data = attachmentService.downloadAttachment(attachmentId);
        // ByteArrayResource resource = new ByteArrayResource(data);
        
        // return ResponseEntity.ok()
        //         .header(HttpHeaders.CONTENT_DISPOSITION, 
        //             "attachment; filename=\"" + attachment.getFilename() + "\"")
        //         .contentType(MediaType.parseMediaType(attachment.getContentType()))
        //         .contentLength(data.length)
        //         .body(resource);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{attachmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Page<TicketAttachmentEntity>> getAttachmentsByTicket(
            @PathVariable Long ticketId,
            @RequestParam(value = "type", required = false) String type,
            Pageable pageable) {
        // if (type != null) {
        //     return ResponseEntity.ok(attachmentService.getAttachmentsByTicketAndType(ticketId, type, pageable));
        // }
        // return ResponseEntity.ok(attachmentService.getAttachmentsByTicket(ticketId, pageable));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reference/{type}/{referenceId}")
    public ResponseEntity<Page<TicketAttachmentEntity>> getAttachmentsByReference(
            @PathVariable String type,
            @PathVariable Long referenceId,
            Pageable pageable) {
        // return ResponseEntity.ok(attachmentService.getAttachmentsByReference(type, referenceId, pageable));
        return ResponseEntity.ok().build();
    }
} 