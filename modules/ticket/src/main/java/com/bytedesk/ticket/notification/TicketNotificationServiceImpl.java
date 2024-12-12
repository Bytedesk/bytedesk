package com.bytedesk.ticket.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TicketNotificationServiceImpl implements TicketNotificationService {

    @Autowired
    private TicketNotificationRepository notificationRepository;
    
    // @Autowired
    // private TicketService ticketService;
    
    // @Autowired
    // private TicketCommentService commentService;

    @Override
    @Transactional
    public void sendTicketCreatedNotification(Long userId, Long ticketId) {
        // TicketEntity ticket = ticketService.getTicket(ticketId);
        createNotification(userId, "ticket_created", 
            "新工单提醒", 
            String.format("工单 #%d 已创建", ticketId),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendTicketUpdatedNotification(Long userId, Long ticketId) {
        createNotification(userId, "ticket_updated",
            "工单更新提醒",
            String.format("工单 #%d 已更新", ticketId),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendStatusChangedNotification(Long userId, Long ticketId, String status) {
        createNotification(userId, "status_changed",
            "状态变更提醒",
            String.format("工单 #%d 状态已变更为 %s", ticketId, getStatusText(status)),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendTicketResolvedNotification(Long userId, Long ticketId) {
        createNotification(userId, "ticket_resolved",
            "工单已解决",
            String.format("工单 #%d 已解决", ticketId),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendTicketClosedNotification(Long userId, Long ticketId) {
        createNotification(userId, "ticket_closed",
            "工单已关闭",
            String.format("工单 #%d 已关闭", ticketId),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendTicketReopenedNotification(Long userId, Long ticketId) {
        createNotification(userId, "ticket_reopened",
            "工单已重开",
            String.format("工单 #%d 已重新开启", ticketId),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendTicketAssignedNotification(Long userId, Long ticketId) {
        createNotification(userId, "ticket_assigned",
            "工单已分配",
            String.format("工单 #%d 已分配给您", ticketId),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendTicketUnassignedNotification(Long userId, Long ticketId) {
        createNotification(userId, "ticket_unassigned",
            "工单已取消分配",
            String.format("工单 #%d 已取消分配", ticketId),
            "ticket", ticketId);
    }

    @Override
    @Transactional
    public void sendNewCommentNotification(Long ticketId, Long commentId) {
        // TicketEntity ticket = ticketService.getTicket(ticketId);
        // TicketCommentEntity comment = commentService.getComment(commentId);
        
        // 通知工单相关人员
        // if (ticket.getAssignedTo() != null && !ticket.getAssignedTo().equals(comment.getUserId())) {
        //     createNotification(ticket.getAssignedTo(), "new_comment",
        //         "新评论提醒",
        //         String.format("工单 #%d 有新评论", ticketId),
        //         "comment", commentId);
        // }
        
        // if (!ticket.getUserId().equals(comment.getUserId())) {
        //     createNotification(ticket.getUserId(), "new_comment",
        //         "新评论提醒",
        //         String.format("工单 #%d 有新评论", ticketId),
        //         "comment", commentId);
        // }
    }

    @Override
    @Transactional
    public void sendInternalCommentNotification(Long ticketId, Long commentId) {
        // TicketEntity ticket = ticketService.getTicket(ticketId);
        // if (ticket.getAssignedTo() != null) {
        //     createNotification(ticket.getAssignedTo(), "internal_comment",
        //         "内部评论提醒",
        //         String.format("工单 #%d 有新的内部评论", ticketId),
        //         "comment", commentId);
        // }
    }

    @Override
    @Transactional
    public void sendCommentReplyNotification(Long userId, Long ticketId, Long commentId) {
        createNotification(userId, "comment_reply",
            "评论回复提醒",
            String.format("您在工单 #%d 的评论收到了回复", ticketId),
            "comment", commentId);
    }

    @Override
    public List<TicketNotificationEntity> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        TicketNotificationEntity notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    private TicketNotificationEntity createNotification(Long userId, String type, String title, 
            String content, String targetType, Long targetId) {
        TicketNotificationEntity notification = new TicketNotificationEntity();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        return notificationRepository.save(notification);
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "open" -> "待处理";
            case "in_progress" -> "处理中";
            case "resolved" -> "已解决";
            case "closed" -> "已关闭";
            default -> status;
        };
    }

    @Override
    public void sendEscalationNotification(Long userId, Long ticketId, Long ruleId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEscalationNotification'");
    }

    @Override
    public void sendRatingReminderNotification(Long userId, Long ticketId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendRatingReminderNotification'");
    }

    @Override
    public void sendTicketRatedNotification(Long userId, Long ticketId, Integer rating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendTicketRatedNotification'");
    }

    @Override
    public void sendLowRatingAlertNotification(Long ticketId, Integer rating, Long agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendLowRatingAlertNotification'");
    }
} 