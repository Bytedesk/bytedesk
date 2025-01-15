package com.bytedesk.ticket.agi.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

public interface TicketNotificationRepository extends JpaRepository<TicketNotificationEntity, Long> {
    
    List<TicketNotificationEntity> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT n FROM TicketNotificationEntity n WHERE n.userId = ?1 AND n.targetType = ?2 " +
           "AND n.targetId = ?3 AND n.read = false")
    List<TicketNotificationEntity> findUnreadByTarget(Long userId, String targetType, Long targetId);
    
    @Modifying
    @Query("UPDATE TicketNotificationEntity n SET n.read = true, n.readAt = CURRENT_TIMESTAMP " +
           "WHERE n.userId = ?1 AND n.read = false")
    void markAllAsRead(Long userId);
    
    @Query("SELECT COUNT(n) FROM TicketNotificationEntity n WHERE n.userId = ?1 AND n.read = false")
    int countUnread(Long userId);
} 