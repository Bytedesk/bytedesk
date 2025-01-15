/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:52:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:39:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.agi.satisfaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TicketSatisfactionRepository extends JpaRepository<TicketSatisfactionEntity, Long> {
    
    Optional<TicketSatisfactionEntity> findByTicketId(Long ticketId);
    
    @Query("SELECT AVG(s.rating) FROM TicketSatisfactionEntity s WHERE s.agentId = ?1 " +
           "AND s.createdAt BETWEEN ?2 AND ?3")
    Double getAverageRating(Long agentId, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT s.rating as rating, COUNT(s) as count FROM TicketSatisfactionEntity s " +
           "WHERE s.createdAt BETWEEN ?1 AND ?2 GROUP BY s.rating")
    List<Map<String, Object>> getRatingDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
//     @Query("SELECT s FROM TicketSatisfactionEntity s WHERE s.reminderSent = false " +
//            "AND s.createdAt < ?1")
//     List<TicketSatisfactionEntity> findPendingReminders(LocalDateTime before);
    
//     @Query("SELECT AVG(s.rating) as avgRating, " +
//            "AVG(s.responseTime) as avgResponseTime, " +
//            "AVG(s.resolutionTime) as avgResolutionTime " +
//            "FROM TicketSatisfactionEntity s " +
//            "WHERE s.categoryId = ?1 AND s.createdAt BETWEEN ?2 AND ?3")
//     Map<String, Double> getCategoryStats(Long categoryId, LocalDateTime startTime, LocalDateTime endTime);
} 