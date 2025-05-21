/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 21:29:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 23:01:34
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件接收配置类
 * https://docs.spring.io/spring-integration/reference/mail.html
 */
@Configuration
@EnableIntegration
@ConditionalOnProperty(name = "bytedesk.mail.receiver.enabled", havingValue = "true", matchIfMissing = false)
public class MailReceiverConfig {

    @Value("${bytedesk.mail.receiver.host}")
    private String mailHost;

    @Value("${bytedesk.mail.receiver.port}")
    private Integer mailPort;

    @Value("${bytedesk.mail.receiver.protocol}")
    private String mailProtocol;

    @Value("${bytedesk.mail.receiver.username}")
    private String mailUsername;

    @Value("${bytedesk.mail.receiver.password}")
    private String mailPassword;

    @Value("${bytedesk.mail.receiver.ssl-enable:true}")
    private Boolean sslEnable;

    @Value("${bytedesk.mail.receiver.poll-interval:60000}")
    private long pollInterval;

    @Value("${bytedesk.mail.receiver.delete-after-receive:false}")
    private Boolean deleteAfterReceive;
    
    @Autowired
    private EmailReceiveService emailReceiveService;

    @Bean
    public MessageChannel receiveEmailChannel() {
        return new DirectChannel();
    }

    @Bean
    @Primary
    public MailReceiver bytedeskMailReceiver() {
        String url = String.format("%s://%s:%s@%s:%d/INBOX", 
                mailProtocol, mailUsername, mailPassword, mailHost, mailPort);

        ImapMailReceiver mailReceiver = new ImapMailReceiver(url);
        
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.debug", "false");
        javaMailProperties.put("mail.store.protocol", mailProtocol);
        
        if (sslEnable) {
            javaMailProperties.put("mail.imap.ssl.enable", "true");
            javaMailProperties.put("mail.imap.ssl.trust", "*");
        }
        
        mailReceiver.setJavaMailProperties(javaMailProperties);
        mailReceiver.setShouldDeleteMessages(deleteAfterReceive);
        mailReceiver.setShouldMarkMessagesAsRead(true);
        
        return mailReceiver;
    }

    @Bean
    @InboundChannelAdapter(value = "receiveEmailChannel", poller = @Poller(fixedDelay = "${bytedesk.mail.receiver.poll-interval:60000}"))
    public MessageSource<Object> bytedeskMailSource() {
        return new MailReceivingMessageSource(bytedeskMailReceiver());
    }
    
    @Bean
    @ServiceActivator(inputChannel = "receiveEmailChannel")
    public MessageHandler receiveEmailHandler() {
        return message -> {
            try {
                if (message.getPayload() instanceof MimeMessage) {
                    MimeMessage emailMessage = (MimeMessage) message.getPayload();
                    emailReceiveService.processEmail(emailMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
