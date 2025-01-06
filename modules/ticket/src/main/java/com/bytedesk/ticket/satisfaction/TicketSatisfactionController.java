/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:55:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 15:54:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.satisfaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.ticket.satisfaction.dto.SatisfactionRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketSatisfactionController {

    @Autowired
    private TicketSatisfactionService satisfactionService;

    @PostMapping("/{ticketId}/satisfaction")
    public ResponseEntity<Void> submitRating(
            @PathVariable Long ticketId,
            @Valid @RequestBody SatisfactionRequest request,
            @AuthenticationPrincipal Long userId) {
        // satisfactionService.submitRating(ticketId, request, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{ticketId}/satisfaction")
    public ResponseEntity<TicketSatisfactionEntity> getTicketRating(
            @PathVariable Long ticketId) {
        return ResponseEntity.ok(satisfactionService.getTicketRating(ticketId));
    }

    @GetMapping("/satisfaction/distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(satisfactionService.getRatingDistribution(startTime, endTime));
    }

    @GetMapping("/satisfaction/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Double> getAverageRating(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(satisfactionService.getAverageRating(userId, startTime, endTime));
    }

    @PostMapping("/{ticketId}/satisfaction/reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> sendRatingReminder(@PathVariable Long ticketId) {
        satisfactionService.sendRatingReminder(ticketId);
        return ResponseEntity.ok().build();
    }
} 