package com.bytedesk.ticket.ticket;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.ticket.ticket.dto.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // 创建工单
    @PostMapping
    public ResponseEntity<TicketEntity> createTicket(
            @Valid @RequestBody TicketCreateRequest request,
            @AuthenticationPrincipal Long userId) {
        request.setUserId(userId);
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    // 更新工单
    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketEntity> updateTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketUpdateRequest request) {
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, request));
    }

    // 删除工单
    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.ok().build();
    }

    // 获取工单详情
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketEntity> getTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getTicket(ticketId));
    }

    // 搜索工单
    @GetMapping("/search")
    public ResponseEntity<Page<TicketEntity>> searchTickets(
            @ModelAttribute TicketSearchRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.searchTickets(request, pageable));
    }

    // 更新工单状态
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketStatusRequest request) {
        switch (request.getStatus()) {
            case "resolved" -> ticketService.resolve(ticketId, request.getResolution());
            case "closed" -> ticketService.close(ticketId, request.getReason());
            case "open" -> ticketService.reopen(ticketId, request.getReason());
            default -> ticketService.updateStatus(ticketId, request.getStatus());
        }
        return ResponseEntity.ok().build();
    }

    // 分配工单
    @PutMapping("/{ticketId}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> assignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketAssignRequest request) {
        ticketService.assignTicket(ticketId, request.getAssigneeId());
        return ResponseEntity.ok().build();
    }

    // 获取我的工单
    @GetMapping("/my")
    public ResponseEntity<Page<TicketEntity>> getMyTickets(
            @AuthenticationPrincipal Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.getMyTickets(userId, pageable));
    }

    // 获取逾期工单
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<TicketEntity>> getOverdueTickets(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.getOverdueTickets(status, pageable));
    }

    // 获取未分配工单
    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<TicketEntity>> getUnassignedTickets() {
        return ResponseEntity.ok(ticketService.getUnassignedTickets());
    }

    // 获取未解决工单
    @GetMapping("/unresolved")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<TicketEntity>> getUnresolvedTickets() {
        return ResponseEntity.ok(ticketService.getUnresolvedTickets());
    }

    // 批量更新工单
    @PutMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> batchUpdateTickets(
            @RequestBody List<TicketUpdateRequest> requests) {
        // requests.forEach(request -> 
        //     ticketService.updateTicket(request.getId(), request));
        return ResponseEntity.ok().build();
    }

    // 导出工单
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<byte[]> exportTickets(
            @ModelAttribute TicketSearchRequest request,
            @RequestParam(defaultValue = "excel") String format) {
        // TODO: 实现导出功能
        return ResponseEntity.ok().build();
    }
} 