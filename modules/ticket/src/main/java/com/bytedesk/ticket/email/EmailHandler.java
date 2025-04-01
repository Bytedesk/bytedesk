/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 21:30:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 22:25:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件处理器
 */
@Slf4j
@Component
public class EmailHandler {

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
                    log.debug("收到新邮件: {}", message);

                    if (payload instanceof MimeMessage) {
                        MimeMessage mimeMessage = (MimeMessage) payload;
                        emailReceiveService.processEmail(mimeMessage);
                    } else {
                        log.warn("未知邮件格式: {}", payload.getClass().getName());
                    }
                } catch (Exception e) {
                    log.error("处理邮件时出错", e);
                }
            }
        };
    }
}
