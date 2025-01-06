/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:21:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 15:09:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DefaultTicketNotificationSender implements TicketNotificationSender {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void send(TicketNotificationEntity notification) throws Exception {
        // 这里可以实现多种通知方式：邮件、短信、WebSocket等
        sendEmail(notification);
    }
    
    private void sendEmail(TicketNotificationEntity notification) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // TODO: 从用户服务获取用户邮箱
        String userEmail = getUserEmail(notification.getUserId());
        
        helper.setTo(userEmail);
        helper.setSubject(notification.getTitle());
        helper.setText(notification.getContent(), true);
        
        mailSender.send(message);
    }
    
    private String getUserEmail(Long userId) {
        // TODO: 实现从用户服务获取邮箱的逻辑
        return "user@example.com";
    }
} 