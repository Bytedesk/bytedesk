package com.bytedesk.ticket.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.MessageChannel;

import java.util.Properties;

/**
 * 邮件接收配置类
 */
@Configuration
@EnableIntegration
public class MailReceiverConfig {

    @Value("${bytedesk.mail.receiver.enabled:false}")
    private boolean mailReceiverEnabled;

    @Value("${bytedesk.mail.receiver.host}")
    private String mailHost;

    @Value("${bytedesk.mail.receiver.port}")
    private int mailPort;

    @Value("${bytedesk.mail.receiver.protocol}")
    private String mailProtocol;

    @Value("${bytedesk.mail.receiver.username}")
    private String mailUsername;

    @Value("${bytedesk.mail.receiver.password}")
    private String mailPassword;

    @Value("${bytedesk.mail.receiver.ssl-enable:true}")
    private boolean sslEnable;

    @Value("${bytedesk.mail.receiver.poll-interval:60000}")
    private long pollInterval;

    @Value("${bytedesk.mail.receiver.delete-after-receive:false}")
    private boolean deleteAfterReceive;

    @Bean
    public MessageChannel receiveEmailChannel() {
        return new DirectChannel();
    }

    @Bean
    public MailReceiver imapMailReceiver() {
        if (!mailReceiverEnabled) {
            return null;
        }

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
    @InboundChannelAdapter(channel = "receiveEmailChannel", 
            poller = @Poller(fixedDelay = "${bytedesk.mail.receiver.poll-interval:60000}"))
    public MailReceivingMessageSource mailMessageSource() {
        if (!mailReceiverEnabled) {
            return null;
        }
        return new MailReceivingMessageSource(imapMailReceiver());
    }
}
