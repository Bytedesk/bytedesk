/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 14:59:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:30:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.agi.notification;

import java.util.List;

public interface TicketNotificationService {
    
    void sendTicketCreatedNotification(Long userId, Long ticketId);
    void sendTicketUpdatedNotification(Long userId, Long ticketId);
    void sendStatusChangedNotification(Long userId, Long ticketId, String status);
    void sendTicketResolvedNotification(Long userId, Long ticketId);
    void sendTicketClosedNotification(Long userId, Long ticketId);
    void sendTicketReopenedNotification(Long userId, Long ticketId);
    void sendTicketAssignedNotification(Long userId, Long ticketId);
    void sendTicketUnassignedNotification(Long userId, Long ticketId);
    void sendNewCommentNotification(Long ticketId, Long commentId);
    void sendInternalCommentNotification(Long ticketId, Long commentId);
    void sendCommentReplyNotification(Long userId, Long ticketId, Long commentId);
    
    List<TicketNotificationEntity> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    
    void sendEscalationNotification(Long userId, Long ticketId, Long ruleId);
    void sendRatingReminderNotification(Long userId, Long ticketId);
    void sendTicketRatedNotification(Long userId, Long ticketId, Integer rating);
    void sendLowRatingAlertNotification(Long ticketId, Integer rating, Long agentId);



    // 发送工单创建通知
    // void sendTicketCreatedNotification(Long userId, Long ticketId);
    // 发送工单更新通知
    // void sendTicketUpdatedNotification(Long userId, Long ticketId);
    // 发送状态变更通知
    // void sendStatusChangedNotification(Long userId, Long ticketId, String newStatus);
    // 发送工单分配通知
    // void sendTicketAssignedNotification(Long userId, Long ticketId);
    // 发送工单升级通知
    // void sendEscalationNotification(Long userId, Long ticketId, Long ruleId);
    // 发送评价提醒通知
    // void sendRatingReminderNotification(Long userId, Long ticketId);
    // 发送工单评价通知
    // void sendTicketRatedNotification(Long userId, Long ticketId, Integer rating);
    // 发送低评分警报通知
    // void sendLowRatingAlertNotification(Long ticketId, Integer rating, Long agentId);
    // 发送内部评论通知
    // void sendInternalCommentNotification(Long ticketId, Long commentId);
    // 发送新评论通知
    // void sendNewCommentNotification(Long ticketId, Long commentId);
} 