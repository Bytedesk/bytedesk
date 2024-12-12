/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:34:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:36:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.time.LocalDateTime;

public interface TicketRepository extends JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {
    
    Page<TicketEntity> findByUserId(Long userId, Pageable pageable);
    
    Page<TicketEntity> findByAssignedTo(Long assignedTo, Pageable pageable);
    
    Page<TicketEntity> findByStatus(String status, Pageable pageable);
    
    Page<TicketEntity> findByPriority(String priority, Pageable pageable);
    
    Page<TicketEntity> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT t FROM TicketEntity t WHERE t.status = ?1 AND t.dueDate < CURRENT_TIMESTAMP")
    Page<TicketEntity> findOverdueTickets(String status, Pageable pageable);
    
    @Modifying
    @Query("UPDATE TicketEntity t SET t.status = ?2 WHERE t.id = ?1")
    void updateStatus(Long ticketId, String status);
    
    @Modifying
    @Query("UPDATE TicketEntity t SET t.priority = ?2 WHERE t.id = ?1")
    void updatePriority(Long ticketId, String priority);
    
    @Modifying
    @Query("UPDATE TicketEntity t SET t.assignedTo = ?2 WHERE t.id = ?1")
    void updateAssignedTo(Long ticketId, Long assignedTo);
    
    @Query("SELECT t FROM TicketEntity t WHERE t.status NOT IN ('resolved', 'closed')")
    List<TicketEntity> findUnresolvedTickets();
    
    @Query("SELECT t FROM TicketEntity t WHERE t.assignedTo IS NULL AND t.status = 'open'")
    List<TicketEntity> findUnassignedTickets();
    
    List<TicketEntity> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<TicketEntity> findByAssignedToAndCreatedAtBetween(Long assignedTo, LocalDateTime startTime, LocalDateTime endTime);
    
    List<TicketEntity> findByCategoryIdAndCreatedAtBetween(Long categoryId, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE t.assignedTo = ?1 AND " +
           "t.status NOT IN ('resolved', 'closed')")
    int countActiveTickets(Long userId);
    
    @Query(value = "SELECT * FROM bytedesk_ticket t WHERE " +
           "MATCH(title, content) AGAINST(?1 IN BOOLEAN MODE)", 
           nativeQuery = true)
    List<TicketEntity> fullTextSearch(String keyword);
    
//     @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, t.createdAt, t.firstResponseAt)) " +
//            "FROM TicketEntity t WHERE t.assignedTo = ?1 AND t.firstResponseAt IS NOT NULL")
//     Long calculateAverageResponseTime(Long agentId);
} 