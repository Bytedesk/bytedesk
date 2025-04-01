/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 22:36:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 23:01:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * https://docs.spring.io/spring-integration/reference/mail.html
 */
@Configuration
@ConditionalOnProperty(name = "bytedesk.mail.integration.enabled", havingValue = "true", matchIfMissing = false)
public class MailIntegrationConfig {

    @Value("${bytedesk.mail.imap.url:imaps://username:password@imap.example.com:993/INBOX}")
    private String imapUrl;
    
    @Value("${bytedesk.mail.polling.delay:60000}")
    private long pollingDelay;
    
    @Bean
    public MessageChannel mailChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public ImapMailReceiver imapMailReceiver() {
        ImapMailReceiver receiver = new ImapMailReceiver(imapUrl);
        receiver.setShouldMarkMessagesAsRead(true);
        
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.imap.socketFactory.fallback", false);
        javaMailProperties.put("mail.store.protocol", "imaps");
        javaMailProperties.put("mail.debug", "false");
        
        receiver.setJavaMailProperties(javaMailProperties);
        return receiver;
    }
    
    @Bean
    @InboundChannelAdapter(value = "mailChannel", poller = @Poller(fixedDelay = "${mail.polling.delay}"))
    public MessageSource<Object> mailMessageSource() {
        return new MailReceivingMessageSource(imapMailReceiver());
    }
    
    @Bean
    @ServiceActivator(inputChannel = "mailChannel")
    public MessageHandler mailMessageHandler() {
        return message -> {
            MimeMessage mimeMessage = (MimeMessage) message.getPayload();
            try {
                System.out.println("收到新邮件，主题: " + mimeMessage.getSubject());
                // 这里可以添加处理邮件的逻辑
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

}
