/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:34:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 14:35:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bytedesk.ticket.ticket.dto.TicketCreateRequest;
import com.bytedesk.ticket.ticket.dto.TicketSearchRequest;
import com.bytedesk.ticket.ticket.dto.TicketUpdateRequest;

public interface TicketService {

    TicketEntity createTicket(TicketCreateRequest request);
    
    TicketEntity updateTicket(Long ticketId, TicketUpdateRequest request);
    
    void deleteTicket(Long ticketId);
    
    TicketEntity getTicket(Long ticketId);
    
    Page<TicketEntity> searchTickets(TicketSearchRequest request, Pageable pageable);
        
    void resolve(Long ticketId, String resolution);
    
    void close(Long ticketId, String reason);
    
    void reopen(Long ticketId, String reason);
        
    void transferTicket(Long ticketId, Long fromUserId, Long toUserId, String reason);
    
    void setDueDate(Long ticketId, LocalDateTime dueDate);
    
    void updateDueDate(Long ticketId, LocalDateTime newDueDate, String reason);
    
    Page<TicketEntity> getTickets(Pageable pageable);
    
    Page<TicketEntity> searchTickets(TicketSearchCriteria criteria, Pageable pageable);
    
    void assignTicket(Long ticketId, Long assignedTo);
    
    void updateStatus(Long ticketId, String status);
    
    void updatePriority(Long ticketId, String priority);
    
    void updateDueDate(Long ticketId, LocalDateTime dueDate);
    
    Page<TicketEntity> getMyTickets(Long userId, Pageable pageable);
    
    Page<TicketEntity> getAssignedTickets(Long assignedTo, Pageable pageable);
    
    Page<TicketEntity> getOverdueTickets(String status, Pageable pageable);
    
    void resolveTicket(Long ticketId, String resolution);
    
    void closeTicket(Long ticketId);
    
    void reopenTicket(Long ticketId);
    
    void addSatisfactionRating(Long ticketId, Integer rating, String comment);
    
    List<TicketEntity> getUnresolvedTickets();
    
    List<TicketEntity> getUnassignedTickets();
} 