/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-12 10:30:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:33:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bytedesk.voc.config.VocConfig;
import com.bytedesk.voc.feedback.FeedbackEntity;
import com.bytedesk.voc.feedback.FeedbackService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VocNotificationServiceImpl implements VocNotificationService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VocConfig vocConfig;
    
    @Autowired
    private FeedbackService feedbackService;
    
    // @Autowired
    // private ReplyService replyService;

    @Override
    @Async
    public void notifyNewFeedback(Long feedbackId) {
        if (!vocConfig.getEmailNotification()) {
            return;
        }

        try {
            FeedbackEntity feedback = feedbackService.getFeedback(feedbackId);
            String subject = String.format("新反馈: %s", feedback.getType());
            String content = String.format("""
                收到新的反馈:
                类型: %s
                内容: %s
                """, feedback.getType(), feedback.getContent());
                
            sendEmail(vocConfig.getNotificationEmail(), subject, content);
        } catch (Exception e) {
            log.error("Failed to send new feedback notification", e);
        }
    }

    @Override
    @Async
    public void notifyFeedbackAssigned(Long feedbackId, Long assignedTo) {
        // TODO: 实现分配通知
    }

    @Override
    @Async
    public void notifyStatusChanged(Long feedbackId, String status) {
        // TODO: 实现状态变更通知
    }

    @Override
    @Async
    public void notifyNewReply(Long feedbackId, Long replyId) {
        // TODO: 实现新回复通知
    }

    @Override
    @Async
    public void notifyInternalReply(Long feedbackId, Long replyId) {
        // TODO: 实现内部回复通知
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
} 