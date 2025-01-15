package com.bytedesk.ticket.agi.satisfaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.agi.satisfaction.exception.TicketSatisfactionException;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketService;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
// import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class TicketSatisfactionServiceImpl implements TicketSatisfactionService {

    @Autowired
    private TicketSatisfactionRepository satisfactionRepository;
    
    @Autowired
    private TicketService ticketService;
    
    // @Autowired
    // private TicketNotificationService notificationService;

    @Override
    @Transactional
    public void submitRating(Long ticketId, Integer rating, String comment) {
        // 验证参数
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        // 获取工单
        TicketEntity ticket = ticketService.getTicket(ticketId);
        
        // 检查工单状态
        if (!"closed".equals(ticket.getStatus())) {
            throw new TicketSatisfactionException("Can only rate closed tickets");
        }
        
        // 检查是否已评价
        if (satisfactionRepository.findByTicketId(ticketId).isPresent()) {
            throw new TicketSatisfactionException("Ticket has already been rated");
        }
        
        try {
            // 创建评价记录
            TicketSatisfactionEntity satisfaction = new TicketSatisfactionEntity();
            satisfaction.setTicketId(ticketId);
            satisfaction.setRating(rating);
            satisfaction.setComment(comment);
            satisfaction.setUserId(ticket.getUserId());
            satisfaction.setAgentId(ticket.getAssignedTo());
            satisfaction.setCategoryId(ticket.getCategoryId());
            
            // 计算响应时间
            // if (ticket.getFirstResponseAt() != null) {
            //     satisfaction.setResponseTime(
            //         calculateWorkingMinutes(ticket.getCreatedAt(), ticket.getFirstResponseAt()));
            // }
            
            // 计算解决时间
            // if (ticket.getResolvedAt() != null) {
            //     satisfaction.setResolutionTime(
            //         calculateWorkingMinutes(ticket.getCreatedAt(), ticket.getResolvedAt()));
            // }
            
            satisfactionRepository.save(satisfaction);
            
            // 发送通知
            notifyRatingSubmitted(ticket, rating);
            
            log.info("Satisfaction rating submitted for ticket {}: {} stars", ticketId, rating);
            
        } catch (Exception e) {
            log.error("Failed to submit satisfaction rating for ticket " + ticketId, e);
            throw new TicketSatisfactionException("Failed to submit rating");
        }
    }

    @Override
    public TicketSatisfactionEntity getTicketRating(Long ticketId) {
        return satisfactionRepository.findByTicketId(ticketId)
            .orElse(null);
    }

    @Override
    public Map<Integer, Long> getRatingDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        
        Map<Integer, Long> distribution = new HashMap<>();
        // 初始化所有评分的计数为0
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }
        
        // 获取实际分布并更新
        satisfactionRepository.getRatingDistribution(startTime, endTime)
            .forEach(m -> distribution.put(
                (Integer) m.get("rating"),
                (Long) m.get("count")
            ));
            
        return distribution;
    }

    @Override
    public Double getAverageRating(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        return satisfactionRepository.getAverageRating(userId, startTime, endTime);
    }

    @Override
    @Transactional
    public void sendRatingReminder(Long ticketId) {
        TicketEntity ticket = ticketService.getTicket(ticketId);
        
        // 检查工单状态
        if (!"closed".equals(ticket.getStatus())) {
            return;
        }
        
        // 检查是否已评价
        if (getTicketRating(ticketId) != null) {
            return;
        }
        
        // 检查关闭时间
        LocalDateTime closedAt = ticket.getClosedAt();
        if (closedAt == null || closedAt.isAfter(LocalDateTime.now().minusDays(1))) {
            return;  // 只提醒关闭超过1天的工单
        }
        
        try {
            // 发送提醒
            // notificationService.sendRatingReminderNotification(ticket.getUserId(), ticketId);
            // log.info("Sent rating reminder for ticket {}", ticketId);
            
        } catch (Exception e) {
            log.error("Failed to send rating reminder for ticket " + ticketId, e);
        }
    }

    @Scheduled(cron = "0 0 10 * * ?")  // 每天上午10点执行
    @Transactional
    public void sendPendingRatingReminders() {
        // 获取3天前关闭但未评价的工单
        // LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        // List<TicketSatisfactionEntity> pendingRatings = 
        //     satisfactionRepository.findPendingReminders(threeDaysAgo);
            
        // log.info("Found {} tickets pending rating reminders", pendingRatings.size());
            
        // for (TicketSatisfactionEntity satisfaction : pendingRatings) {
        //     try {
        //         sendRatingReminder(satisfaction.getTicketId());
        //         // satisfaction.setReminderSent(true);
        //         satisfactionRepository.save(satisfaction);
        //     } catch (Exception e) {
        //         log.error("Failed to process rating reminder for ticket: " + 
        //             satisfaction.getTicketId(), e);
        //     }
        // }
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (endTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("End time cannot be in the future");
        }
    }

    // private long calculateWorkingMinutes(LocalDateTime startTime, LocalDateTime endTime) {
    //     // TODO: 考虑工作时间计算
    //     return ChronoUnit.MINUTES.between(startTime, endTime);
    // }

    private void notifyRatingSubmitted(TicketEntity ticket, Integer rating) {
        // if (ticket.getAssignedTo() != null) {
        //     notificationService.sendTicketRatedNotification(
        //         ticket.getAssignedTo(), ticket.getId(), rating);
        // }
        
        // 如果评分较低，通知主管
        // if (rating <= 2) {
        //     notificationService.sendLowRatingAlertNotification(
        //         ticket.getId(), rating, ticket.getAssignedTo());
        // }
    }
} 