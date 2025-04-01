package com.bytedesk.ticket.email.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.email.service.EmailReceiveService;

import jakarta.mail.internet.MimeMessage;

/**
 * 邮件处理器
 */
@Component
public class EmailHandler {
    
    private final Logger logger = LoggerFactory.getLogger(EmailHandler.class);
    
    @Autowired
    private EmailReceiveService emailReceiveService;
    
    @Bean
    @ServiceActivator(inputChannel = "receiveEmailChannel")
    public MessageHandler handleMessage() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) {
                try {
                    Object payload = message.getPayload();
                    logger.debug("收到新邮件: {}", message);
                    
                    if (payload instanceof MimeMessage) {
                        MimeMessage mimeMessage = (MimeMessage) payload;
                        emailReceiveService.processEmail(mimeMessage);
                    } else {
                        logger.warn("未知邮件格式: {}", payload.getClass().getName());
                    }
                } catch (Exception e) {
                    logger.error("处理邮件时出错", e);
                }
            }
        };
    }
}
