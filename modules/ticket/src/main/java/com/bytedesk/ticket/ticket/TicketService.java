package com.bytedesk.ticket.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.agi.attachment.TicketAttachmentService;
import com.bytedesk.ticket.ticket.dto.*;
import com.bytedesk.ticket.ticket.exception.*;
import com.bytedesk.ticket.ticket.search.TicketSearchSpecification;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketAttachmentService attachmentService;
    
    // 循环引用
    // @Autowired
    // private TicketNotificationService notificationService;

    
    @Transactional
    public TicketEntity createTicket(TicketCreateRequest request) {
        // 创建工单
        TicketEntity ticket = new TicketEntity();
        ticket.setTitle(request.getTitle());
        ticket.setContent(request.getContent());
        ticket.setUserId(request.getUserId());
        ticket.setCategoryId(request.getCategoryId());
        ticket.setPriority(request.getPriority());
        ticket.setSource(request.getSource());
        // ticket.setTags(String.join(",", request.getTags()));
        ticket.setDueDate(request.getDueDate());
        ticket.setStatus("open");
        
        ticket = ticketRepository.save(ticket);
        
        // 保存附件
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            // request.getAttachments().forEach(file -> 
            //     attachmentService.saveTicketAttachment(ticket.getId(), file));
        }
        
        // 发送通知
        notifyTicketCreated(ticket);
        
        return ticket;
    }

    
    @Transactional
    public TicketEntity updateTicket(Long ticketId, TicketUpdateRequest request) {
        TicketEntity ticket = getTicket(ticketId);
        
        // 更新基本信息
        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            ticket.setContent(request.getContent());
        }
        if (request.getCategoryId() != null) {
            ticket.setCategoryId(request.getCategoryId());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getTags() != null) {
            // ticket.setTags(String.join(",", request.getTags()));
        }
        if (request.getDueDate() != null) {
            ticket.setDueDate(request.getDueDate());
        }
        
        ticket = ticketRepository.save(ticket);
        
        // 处理附件
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            // request.getAttachments().forEach(file -> 
            //     attachmentService.saveTicketAttachment(ticket.getId(), file));
        }
        
        // 发送通知
        notifyTicketUpdated(ticket);
        
        return ticket;
    }

    
    @Transactional
    public void deleteTicket(Long ticketId) {
        TicketEntity ticket = getTicket(ticketId);
        
        // 删除附件
        attachmentService.deleteTicketAttachments(ticketId);
        
        // 删除工单
        ticketRepository.delete(ticket);
    }

    
    public TicketEntity getTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    }

    
    @Transactional
    public void updateStatus(Long ticketId, String status) {
        TicketEntity ticket = getTicket(ticketId);
        validateStatusTransition(ticket.getStatus(), status);
        
        ticket.setStatus(status);
        // ticket.setStatusUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        // 发送通知
        notifyStatusChanged(ticket, status);
    }

    
    @Transactional
    public void resolve(Long ticketId, String resolution) {
        TicketEntity ticket = getTicket(ticketId);
        validateStatusTransition(ticket.getStatus(), "resolved");
        
        ticket.setStatus("resolved");
        // ticket.setResolution(resolution);
        ticket.setResolvedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        // 发送通知
        notifyTicketResolved(ticket);
    }

    
    @Transactional
    public void close(Long ticketId, String reason) {
        TicketEntity ticket = getTicket(ticketId);
        validateStatusTransition(ticket.getStatus(), "closed");
        
        ticket.setStatus("closed");
        // ticket.setClosedReason(reason);
        ticket.setClosedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        // 发送通知
        notifyTicketClosed(ticket);
    }

    
    @Transactional
    public void reopen(Long ticketId, String reason) {
        TicketEntity ticket = getTicket(ticketId);
        validateStatusTransition(ticket.getStatus(), "open");
        
        ticket.setStatus("open");
        // ticket.setReopenReason(reason);
        // ticket.setReopenedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        // 发送通知
        notifyTicketReopened(ticket);
    }

    
    @Transactional
    public void assignTicket(Long ticketId, Long userId) {
        TicketEntity ticket = getTicket(ticketId);
        
        Long previousAssignee = ticket.getAssignedTo();
        ticket.setAssignedTo(userId);
        // ticket.setAssignedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        // 发送通知
        notifyTicketAssigned(ticket, previousAssignee);
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        // 实现状态转换验证逻辑
        // if (!isValidStatusTransition(currentStatus, newStatus)) {
        //     throw new InvalidStatusTransitionException(currentStatus, newStatus);
        // }
    }

    // private boolean isValidStatusTransition(String currentStatus, String newStatus) {
    //     // 实现状态转换规则
    //     return switch (currentStatus) {
    //         case "open" -> List.of("in_progress", "resolved", "closed").contains(newStatus);
    //         case "in_progress" -> List.of("resolved", "closed").contains(newStatus);
    //         case "resolved" -> List.of("closed", "open").contains(newStatus);
    //         case "closed" -> List.of("open").contains(newStatus);
    //         default -> false;
    //     };
    // }

    private void notifyTicketCreated(TicketEntity ticket) {
        // notificationService.sendTicketCreatedNotification(ticket.getUserId(), ticket.getId());
    }

    private void notifyTicketUpdated(TicketEntity ticket) {
        if (ticket.getAssignedTo() != null) {
            // notificationService.sendTicketUpdatedNotification(ticket.getAssignedTo(), ticket.getId());
        }
    }

    private void notifyStatusChanged(TicketEntity ticket, String newStatus) {
        // notificationService.sendStatusChangedNotification(ticket.getUserId(), ticket.getId(), newStatus);
        if (ticket.getAssignedTo() != null) {
            // notificationService.sendStatusChangedNotification(ticket.getAssignedTo(), ticket.getId(), newStatus);
        }
    }

    private void notifyTicketResolved(TicketEntity ticket) {
        // notificationService.sendTicketResolvedNotification(ticket.getUserId(), ticket.getId());
    }

    private void notifyTicketClosed(TicketEntity ticket) {
        // notificationService.sendTicketClosedNotification(ticket.getUserId(), ticket.getId());
    }

    private void notifyTicketReopened(TicketEntity ticket) {
        if (ticket.getAssignedTo() != null) {
            // notificationService.sendTicketReopenedNotification(ticket.getAssignedTo(), ticket.getId());
        }
    }

    private void notifyTicketAssigned(TicketEntity ticket, Long previousAssignee) {
        // notificationService.sendTicketAssignedNotification(ticket.getAssignedTo(), ticket.getId());
        if (previousAssignee != null) {
            // notificationService.sendTicketUnassignedNotification(previousAssignee, ticket.getId());
        }
    }

    
    public Page<TicketEntity> searchTickets(TicketSearchRequest request, Pageable pageable) {
        return ticketRepository.findAll(
            TicketSearchSpecification.buildSpecification(request), 
            pageable
        );
    }

    
    public List<TicketEntity> getUnresolvedTickets() {
        return ticketRepository.findUnresolvedTickets();
    }

    
    public List<TicketEntity> getUnassignedTickets() {
        return ticketRepository.findUnassignedTickets();
    }

    
    public Page<TicketEntity> getMyTickets(Long userId, Pageable pageable) {
        TicketSearchRequest request = new TicketSearchRequest();
        request.setAssignedTo(userId);
        request.setStatuses(List.of("open", "in_progress"));
        return searchTickets(request, pageable);
    }

    
    public Page<TicketEntity> getOverdueTickets(String status, Pageable pageable) {
        TicketSearchRequest request = new TicketSearchRequest();
        request.setOverdue(true);
        if (status != null) {
            request.setStatuses(List.of(status));
        }
        return searchTickets(request, pageable);
    }

    
    public void transferTicket(Long ticketId, Long fromUserId, Long toUserId, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'transferTicket'");
    }

    
    public void setDueDate(Long ticketId, LocalDateTime dueDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDueDate'");
    }

    
    public void updateDueDate(Long ticketId, LocalDateTime newDueDate, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDueDate'");
    }

    
    public Page<TicketEntity> getTickets(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTickets'");
    }

    
    public Page<TicketEntity> searchTickets(TicketSearchCriteria criteria, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchTickets'");
    }

    
    public void updatePriority(Long ticketId, String priority) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePriority'");
    }

    
    public void updateDueDate(Long ticketId, LocalDateTime dueDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDueDate'");
    }

    
    public Page<TicketEntity> getAssignedTickets(Long assignedTo, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAssignedTickets'");
    }

    
    public void resolveTicket(Long ticketId, String resolution) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resolveTicket'");
    }

    
    public void closeTicket(Long ticketId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeTicket'");
    }

    
    public void reopenTicket(Long ticketId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reopenTicket'");
    }

    
    public void addSatisfactionRating(Long ticketId, Integer rating, String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addSatisfactionRating'");
    }
} 